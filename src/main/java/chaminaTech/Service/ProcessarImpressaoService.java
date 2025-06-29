package chaminaTech.Service;

import chaminaTech.Entity.*;
import chaminaTech.Repository.CaixaRepository;
import chaminaTech.Repository.ImpressaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessarImpressaoService {
    @Autowired
    private ImpressaoRepository impressaoRepository;
    @Autowired
    private CaixaRepository caixaRepository;

    public Map<String, List<ProdutoVenda>> verificarAlteracaoProdutos(Venda vendaOriginal, Venda vendaAtualizada) {
        List<ProdutoVenda> produtosRemovidos = new ArrayList<>();
        List<ProdutoVenda> produtosCadastrados = new ArrayList<>();

        // Agrupar produtos da venda original
        Map<String, ProdutoVenda> mapaProdutosOriginais = vendaOriginal.getProdutoVendas().stream().filter(ProdutoVenda::getAtivo).collect(Collectors.toMap(this::criarChaveAgrupamento, this::criarCopiaProdutoVenda, (pv1, pv2) -> {
            pv1.setQuantidade(pv1.getQuantidade().add(pv2.getQuantidade()));
            return pv1;
        }));

        // Agrupar produtos da venda atualizada
        Map<String, ProdutoVenda> mapaProdutosAtualizados = vendaAtualizada.getProdutoVendas().stream().filter(ProdutoVenda::getAtivo).collect(Collectors.toMap(this::criarChaveAgrupamento, this::criarCopiaProdutoVenda, (pv1, pv2) -> {
            pv1.setQuantidade(pv1.getQuantidade().add(pv2.getQuantidade()));
            return pv1;
        }));

        // Verificar produtos removidos ou com quantidade reduzida
        for (Map.Entry<String, ProdutoVenda> entry : mapaProdutosOriginais.entrySet()) {
            String chave = entry.getKey();
            ProdutoVenda produtoOriginal = entry.getValue();

            if (!mapaProdutosAtualizados.containsKey(chave)) {
                produtosRemovidos.add(produtoOriginal);
            } else {
                ProdutoVenda produtoAtualizado = mapaProdutosAtualizados.get(chave);
                if (produtoAtualizado.getQuantidade().compareTo(produtoOriginal.getQuantidade()) < 0) {
                    ProdutoVenda produtoReduzido = criarCopiaProdutoVenda(produtoOriginal);
                    produtoReduzido.setQuantidade(produtoOriginal.getQuantidade().subtract(produtoAtualizado.getQuantidade()));
                    produtosRemovidos.add(produtoReduzido);
                }
            }
        }

        // Verificar produtos novos ou com quantidade aumentada
        for (Map.Entry<String, ProdutoVenda> entry : mapaProdutosAtualizados.entrySet()) {
            String chave = entry.getKey();
            ProdutoVenda produtoAtualizado = entry.getValue();

            if (!mapaProdutosOriginais.containsKey(chave)) {
                produtosCadastrados.add(produtoAtualizado);
            } else {
                ProdutoVenda produtoOriginal = mapaProdutosOriginais.get(chave);
                if (produtoAtualizado.getQuantidade().compareTo(produtoOriginal.getQuantidade()) > 0) {
                    ProdutoVenda produtoAdicionado = criarCopiaProdutoVenda(produtoAtualizado);
                    produtoAdicionado.setQuantidade(produtoAtualizado.getQuantidade().subtract(produtoOriginal.getQuantidade()));
                    produtosCadastrados.add(produtoAdicionado);
                }
            }
        }

        Map<String, List<ProdutoVenda>> alteracoes = new HashMap<>();
        alteracoes.put("removidos", produtosRemovidos);
        alteracoes.put("cadastrados", produtosCadastrados);
        return alteracoes;
    }

    public void processarImpressaoProdutos(Venda venda, List<ProdutoVenda> produtosParaImprimir, Integer numeroCupom, boolean removido) {
        // Mapa para agrupar produtos por impressora
        Map<String, List<ProdutoVenda>> mapaImpressoras = new HashMap<>();

        // Iterar pelos produtos e agrupar pelas impressoras
        for (ProdutoVenda produtoVenda : produtosParaImprimir) {
            Produto produto = produtoVenda.getProduto();
            if (produto.getDeveImprimir()) {
                for (int i = 0; i < produto.getImpressoras().size(); i++) {
                    String nomeImpressora = produto.getImpressoras().get(i).getNomeImpressora();
                    mapaImpressoras.putIfAbsent(nomeImpressora, new ArrayList<>());
                    mapaImpressoras.get(nomeImpressora).add(produtoVenda);
                }
            }
        }

        // Processar a impressão para cada impressora
        for (Map.Entry<String, List<ProdutoVenda>> entry : mapaImpressoras.entrySet()) {
            String nomeImpressora = entry.getKey();
            List<ProdutoVenda> produtosParaImprimirNaImpressora = entry.getValue();

            // Agrupar produtos iguais (mesmo nome, mesmas observações) para somar quantidades
            List<ProdutoVenda> produtosAgrupados = agruparProdutos(produtosParaImprimirNaImpressora);

            // Preparar o conteúdo consolidado para a impressora
//            String conteudoStr = prepararConteudoImpressaoProdutosNovos(venda, produtosAgrupados, numeroCupom);
            String conteudoStr = removido ? prepararConteudoImpressaoProdutosDeletados(venda, produtosAgrupados, numeroCupom) : prepararConteudoImpressaoProdutosNovos(venda, produtosAgrupados, numeroCupom);

            byte[] conteudoBytes = conteudoStr.getBytes(StandardCharsets.UTF_8); // Converter para byte[]

            // Criar e salvar o registro de impressão
            Impressao impressao = new Impressao();
            impressao.setMatrizId(venda.getMatriz().getId());
            impressao.setNomeImpressora(nomeImpressora);
            impressao.setConteudoImpressao(conteudoBytes);  // Salvar como byte[]
            impressao.setStatus(true); // Marca como pronto para impressão
            impressaoRepository.save(impressao);
        }
    }

    private List<ProdutoVenda> agruparProdutos(List<ProdutoVenda> produtos) {
        // Mapa para armazenar produtos agrupados por chave única
        Map<String, ProdutoVenda> produtosAgrupados = new HashMap<>();

        for (ProdutoVenda produtoVenda : produtos) {
            // Criar uma chave única com base no produto e nas observações
            String chaveProduto = criarChaveAgrupamento(produtoVenda);

            if (produtosAgrupados.containsKey(chaveProduto)) {
                // Se o produto já existe no agrupamento, somar a quantidade em uma cópia
                ProdutoVenda produtoAgrupado = produtosAgrupados.get(chaveProduto);
                produtoAgrupado.setQuantidade(produtoAgrupado.getQuantidade().add(produtoVenda.getQuantidade()));
            } else {
                // Se não existe, criar uma cópia e adicionar ao agrupamento
                ProdutoVenda copiaProdutoVenda = criarCopiaProdutoVenda(produtoVenda);
                produtosAgrupados.put(chaveProduto, copiaProdutoVenda);
            }
        }

        // Retornar a lista de produtos agrupados
        return new ArrayList<>(produtosAgrupados.values());
    }

    public String prepararConteudoImpressaoProdutosNovos(Venda venda, List<ProdutoVenda> produtosParaImprimir, int numeroCupom) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            if (venda.getBalcao()) {
                centralizarTexto(conteudo, "BALCAO");
            } else if (venda.getRetirada()) {
                centralizarTexto(conteudo, "RETIRADA");
            } else if (venda.getEntrega()) {
                centralizarTexto(conteudo, "ENTREGA");
            } else if (venda.getMesa() != null) {
                centralizarTexto(conteudo, "MESA");
            }

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3); // Fonte maior

            if (venda.getMesa() != null) {
                ativarNegrito(conteudo);
                conteudo.append("Mesa: ").append(venda.getMesa()).append("\n");
                desativarNegrito(conteudo);
            } else if (venda.getBalcao() || venda.getRetirada() || venda.getEntrega()) {
                ativarNegrito(conteudo);
                conteudo.append("Cupom: ").append(numeroCupom).append("\n");
                desativarNegrito(conteudo);
            }

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(venda.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(venda.getDataEdicao())).append("\n");

            definirTamanhoFonte(conteudo, 1, 1); // Resetar tamanho da fonte para padrão
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3); // Cabeçalho da tabela de produtos
            ativarNegrito(conteudo);
            conteudo.append(String.format("%-5s %-5s\n", "Qtde.", "Produto"));
            desativarNegrito(conteudo);
            definirTamanhoFonte(conteudo, 1, 1); // Resetar para padrão
            conteudo.append("=".repeat(48)).append("\n");

            for (ProdutoVenda produtoVenda : produtosParaImprimir) {
                int quantidade = produtoVenda.getQuantidade().intValue();
                definirTamanhoFonte(conteudo, 3, 3); // Fonte maior para o produto
                ativarNegrito(conteudo);
                conteudo.append(String.format("%-2d %-5s\n", quantidade, produtoVenda.getProduto().getNome()));
                desativarNegrito(conteudo);

                if (produtoVenda.getObservacoesProdutoVenda() != null && !produtoVenda.getObservacoesProdutoVenda().isEmpty()) {
                    definirTamanhoFonte(conteudo, 4, 3); // Fonte menor para observações
                    for (Observacoes observacao : produtoVenda.getObservacoesProdutoVenda()) {
                        conteudo.append(String.format("%-3s %-1s %s\n", "", "-", observacao.getObservacao()));
                    }
                }

                if (produtoVenda.getObservacaoProdutoVenda() != null && !produtoVenda.getObservacaoProdutoVenda().isEmpty()) {
                    definirTamanhoFonte(conteudo, 4, 3); // Fonte menor para observações
                    conteudo.append(String.format("%-3s %-1s %s\n", "", "-", produtoVenda.getObservacaoProdutoVenda()));
                }

                desativarNegrito(conteudo);
                definirTamanhoFonte(conteudo, 1, 2);
                conteudo.append("-".repeat(48)).append("\n");
            }

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public String prepararConteudoImpressaoProdutosDeletados(Venda venda, List<ProdutoVenda> produtosParaImprimir, int numeroCupom) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");

            definirTamanhoFonte(conteudo, 4, 4);  // Fonte maior
            ativarNegrito(conteudo);  // Ativar negrito
            conteudo.append("=".repeat(32)).append("\n");
            centralizarTexto(conteudo, "CANCELADO");
            conteudo.append("=".repeat(32)).append("\n");
            desativarNegrito(conteudo);

            if (venda.getBalcao()) {
                centralizarTexto(conteudo, "BALCAO");
            } else if (venda.getRetirada()) {
                centralizarTexto(conteudo, "RETIRADA");
            } else if (venda.getEntrega()) {
                centralizarTexto(conteudo, "ENTREGA");
            } else if (venda.getMesa() != null) {
                centralizarTexto(conteudo, "MESA");
            }

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3); // Fonte maior

            if (venda.getMesa() != null) {
                ativarNegrito(conteudo);
                conteudo.append("Mesa: ").append(venda.getMesa()).append("\n");
                desativarNegrito(conteudo);
            } else if (venda.getBalcao() || venda.getRetirada() || venda.getEntrega()) {
                ativarNegrito(conteudo);
                conteudo.append("Cupom: ").append(numeroCupom).append("\n");
                desativarNegrito(conteudo);
            }

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(venda.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(venda.getDataEdicao())).append("\n");

            definirTamanhoFonte(conteudo, 1, 1); // Resetar tamanho da fonte para padrão
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3); // Cabeçalho da tabela de produtos
            ativarNegrito(conteudo);
            conteudo.append(String.format("%-5s %-5s\n", "Qtde.", "Produto"));
            desativarNegrito(conteudo);
            definirTamanhoFonte(conteudo, 1, 1); // Resetar para padrão
            conteudo.append("=".repeat(48)).append("\n");

            for (ProdutoVenda produtoVenda : produtosParaImprimir) {
                int quantidade = produtoVenda.getQuantidade().intValue();
                definirTamanhoFonte(conteudo, 3, 3); // Fonte maior para o produto
                ativarNegrito(conteudo);
                conteudo.append(String.format("%-2d %-5s\n", quantidade, produtoVenda.getProduto().getNome()));
                desativarNegrito(conteudo);

                if (produtoVenda.getObservacoesProdutoVenda() != null && !produtoVenda.getObservacoesProdutoVenda().isEmpty()) {
                    definirTamanhoFonte(conteudo, 4, 3); // Fonte menor para observações
                    for (Observacoes observacao : produtoVenda.getObservacoesProdutoVenda()) {
                        conteudo.append(String.format("%-3s %-1s %s\n", "", "-", observacao.getObservacao()));
                    }
                }

                if (produtoVenda.getObservacaoProdutoVenda() != null && !produtoVenda.getObservacaoProdutoVenda().isEmpty()) {
                    definirTamanhoFonte(conteudo, 4, 3); // Fonte menor para observações
                    conteudo.append(String.format("%-3s %-1s %s\n", "", "-", produtoVenda.getObservacaoProdutoVenda()));
                }

                desativarNegrito(conteudo);
                definirTamanhoFonte(conteudo, 1, 2);
                conteudo.append("-".repeat(48)).append("\n");
            }

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    ///////////// comprovante recebimento venda
    public void processarImpressaoComprovanteRecebimento(Venda venda, int numeroCupom) {
        String conteudoComprovante = prepararConteudoComprovanteRecebimento(venda, numeroCupom);

        byte[] conteudoBytes = conteudoComprovante.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(venda.getMatriz().getId());
        impressao.setNomeImpressora(venda.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);  // Salvar como byte[]
        impressao.setStatus(true); // Marca como pronto para impressão
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoComprovanteRecebimento(Venda venda, int numeroCupom) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            if (venda.getConsumoInterno()) {
                centralizarTexto(conteudo, "COMPROVANTE CONSUMO");
            } else if (venda.getRetirada()) {
                centralizarTexto(conteudo, "RECEBIMENTO RETIRADA");
            } else if (venda.getBalcao()) {
                centralizarTexto(conteudo, "RECEBIMENTO BALCAO");
            } else if (venda.getEntrega()) {
                centralizarTexto(conteudo, "RECEBIMENTO ENTREGA");
            } else if (venda.getMesa() != null) {
                centralizarTexto(conteudo, "RECEBIMENTO MESA");
            }
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3);
            if (venda.getMesa() != null) {
                ativarNegrito(conteudo);
                conteudo.append("Mesa: ").append(venda.getMesa()).append("\n");
                desativarNegrito(conteudo);
            } else if (venda.getBalcao() || venda.getRetirada() || venda.getEntrega()) {
                ativarNegrito(conteudo);
                conteudo.append("Cupom: ").append(numeroCupom).append("\n");
                desativarNegrito(conteudo);
            }

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(venda.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(venda.getDataEdicao())).append("\n");
            if (venda.getConsumoInterno()) {
                conteudo.append("Motivo: ").append(venda.getMotivoConsumo()).append("\n");
            }

            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append(String.format("%-26s %4s %7s %8s\n", "PRODUTO", "QTDE", "VAL.UN", "VAL.TOT"));
            conteudo.append("=".repeat(48)).append("\n");

            for (ProdutoVenda produtoVenda : venda.getProdutoVendas()) {
                String nomeProduto = produtoVenda.getProduto().getNome();
                if (nomeProduto.length() > 26) {
                    nomeProduto = nomeProduto.substring(0, 26);
                }

                int quantidade = produtoVenda.getQuantidade().intValue();
                BigDecimal valorUnitario = produtoVenda.getProduto().getValor();
                BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));
                List<Observacoes> observacoes = produtoVenda.getObservacoesProdutoVenda();

                conteudo.append(String.format("%-26s %4d %7.2f %8.2f\n", nomeProduto, quantidade, valorUnitario, valorTotal));

                if (observacoes != null && !observacoes.isEmpty()) {
                    for (Observacoes observacao1 : observacoes) {
                        BigDecimal valorObs = observacao1.getValor();
                        if (valorObs != null && valorObs.compareTo(BigDecimal.ZERO) > 0) {
                            adicionarTextoAlinhado(conteudo, ("    -" + observacao1.getObservacao()), String.format("%.2f", valorObs), 48);
                        }
                    }
                }
            }

            conteudo.append("=".repeat(48)).append("\n");
            conteudo.append("FORMAS DE PAGAMENTO").append("\n");

            if (venda.getVendaPagamento() != null) {
                VendaPagamento pagamento = venda.getVendaPagamento();
                if (pagamento.getDinheiro() != null && pagamento.getDinheiro().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "DINHEIRO", String.format("%.2f", pagamento.getDinheiro()), 48);
                }
                if (pagamento.getDebito() != null && pagamento.getDebito().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "CARTAO DE DEBITO", String.format("%.2f", pagamento.getDebito()), 48);
                }
                if (pagamento.getCredito() != null && pagamento.getCredito().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "CARTAO DE CREDITO", String.format("%.2f", pagamento.getCredito()), 48);
                }
                if (pagamento.getPix() != null && pagamento.getPix().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "PIX", String.format("%.2f", pagamento.getPix()), 48);
                }
            }

            conteudo.append("=".repeat(48)).append("\n");

            // Subtotal e total
            adicionarTextoAlinhado(conteudo, "Subtotal", String.format("%.2f", venda.getValorBruto()), 48);
            adicionarTextoAlinhado(conteudo, "Desconto", String.format("%.2f", venda.getDesconto() != null ? venda.getDesconto() : BigDecimal.ZERO), 48);
            adicionarTextoAlinhado(conteudo, "Servico", String.format("%.2f", venda.getValorServico() != null ? venda.getValorServico() : BigDecimal.ZERO), 48);
            adicionarTextoAlinhado(conteudo, "Total Recebido", String.format("%.2f", venda.getValorTotal()), 48);

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    ///////////// comprovante deletar venda
    public void processarImpressaoComprovanteDeletacaoVenda(Venda venda, Integer numeroCupom) {
        List<ProdutoVenda> produtosAgrupados = agruparProdutos(venda.getProdutoVendas());

        String conteudoComprovanteDeletar = prepararConteudoComprovanteDeletacaoVenda(venda, produtosAgrupados, numeroCupom);
        byte[] conteudoBytes = conteudoComprovanteDeletar.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(venda.getMatriz().getId());
        impressao.setNomeImpressora(venda.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoComprovanteDeletacaoVenda(Venda venda, List<ProdutoVenda> produtosAgrupados, int numeroCupom) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            ativarNegrito(conteudo);
            definirTamanhoFonte(conteudo, 4, 4);
            if (venda.getBalcao()) {
                centralizarTexto(conteudo, "CANCELAMENTO TOTAL BALCAO");
            } else if (venda.getRetirada()) {
                centralizarTexto(conteudo, "CANCELAMENTO TOTAL RETIRADA");
            } else if (venda.getEntrega()) {
                centralizarTexto(conteudo, "CANCELAMENTO TOTAL ENTREGA");
            } else if (venda.getMesa() != null) {
                centralizarTexto(conteudo, "CANCELAMENTO TOTAL MESA");
            }
            desativarNegrito(conteudo);

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3);

            if (venda.getMesa() != null) {
                ativarNegrito(conteudo);
                conteudo.append("Mesa: ").append(venda.getMesa()).append("\n");
                desativarNegrito(conteudo);
            } else if (venda.getBalcao() || venda.getRetirada() || venda.getEntrega()) {
                ativarNegrito(conteudo);
                conteudo.append("Cupom: ").append(numeroCupom).append("\n");
                desativarNegrito(conteudo);
            }

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(venda.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(venda.getDataEdicao())).append("\n");

            String motivo = venda.getMotivoDeletar();
            if (motivo == null || motivo.trim().isEmpty()) {
                motivo = "";
            }
            conteudo.append("Motivo: ").append(motivo).append("\n");
            conteudo.append("=".repeat(48)).append("\n");

            conteudo.append(String.format("%-26s %4s %7s %8s\n", "PRODUTO", "QTDE", "VAL.UN", "VAL.TOT"));
            conteudo.append("=".repeat(48)).append("\n");

            BigDecimal totalProdutos = BigDecimal.ZERO;

            for (ProdutoVenda produtoVenda : produtosAgrupados) {
                String nomeProduto = produtoVenda.getProduto().getNome();
                if (nomeProduto.length() > 26) {
                    nomeProduto = nomeProduto.substring(0, 26);
                }

                int quantidade = produtoVenda.getQuantidade().intValue();
                BigDecimal valorUnitario = produtoVenda.getProduto().getValor();
                BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));

                totalProdutos = totalProdutos.add(valorTotal);

                conteudo.append(String.format("%-26s %4d %7.2f %8.2f\n", nomeProduto, quantidade, valorUnitario, valorTotal));
            }

            conteudo.append("=".repeat(48)).append("\n");
            definirEspacamentoCaracteres(conteudo, 1);
            adicionarTextoAlinhado(conteudo, "Total De Produtos", String.format("%.2f", venda.getValorTotal()), 48);

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }


    ///////////// comprovante deletar produto
    public void processarImpressaoComprovanteProdutoDeletado(Venda venda, List<ProdutoVenda> produtosRemovidos, int numeroCupom) {
        String conteudoComprovanteDeletar = prepararConteudoComprovanteDeletacaoProduto(venda, produtosRemovidos, numeroCupom);
        byte[] conteudoBytes = conteudoComprovanteDeletar.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(venda.getMatriz().getId());
        impressao.setNomeImpressora(venda.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoComprovanteDeletacaoProduto(Venda venda, List<ProdutoVenda> produtosAgrupados, int numeroCupom) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            ativarNegrito(conteudo);
            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "CANCELAMENTO DE PRODUTO");
            desativarNegrito(conteudo);

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            if (venda.getBalcao()) {
                centralizarTexto(conteudo, "BALCAO");
            } else if (venda.getRetirada()) {
                centralizarTexto(conteudo, "RETIRADA");
            } else if (venda.getEntrega()) {
                centralizarTexto(conteudo, "ENTREGA");
            } else if (venda.getMesa() != null) {
                centralizarTexto(conteudo, "MESA");
            }

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3);

            if (venda.getMesa() != null) {
                ativarNegrito(conteudo);
                conteudo.append("Mesa: ").append(venda.getMesa()).append("\n");
                desativarNegrito(conteudo);
            } else if (venda.getBalcao() || venda.getRetirada() || venda.getEntrega()) {
                ativarNegrito(conteudo);
                conteudo.append("Cupom: ").append(numeroCupom).append("\n");
                desativarNegrito(conteudo);
            }

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(venda.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(venda.getDataEdicao())).append("\n");

            for (ProdutoVenda produtoVenda : produtosAgrupados) {
                String motivo = produtoVenda.getMotivoExclusao();
                if (motivo == null || motivo.trim().isEmpty()) {
                    motivo = "";
                }
                conteudo.append("Motivo: ").append(motivo).append("\n");
            }

            conteudo.append("=".repeat(48)).append("\n");
            conteudo.append(String.format("%-26s %4s %7s %8s\n", "PRODUTO", "QTDE", "VAL.UN", "VAL.TOT"));
            conteudo.append("=".repeat(48)).append("\n");

            BigDecimal totalProdutos = BigDecimal.ZERO;

            for (ProdutoVenda produtoVenda : produtosAgrupados) {
                String nomeProduto = produtoVenda.getProduto().getNome();
                if (nomeProduto.length() > 26) {
                    nomeProduto = nomeProduto.substring(0, 26);
                }

                int quantidade = produtoVenda.getQuantidade().intValue();
                BigDecimal valorUnitario = produtoVenda.getProduto().getValor();
                BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));

                totalProdutos = totalProdutos.add(valorTotal);

                conteudo.append(String.format("%-26s %4d %7.2f %8.2f\n", nomeProduto, quantidade, valorUnitario, valorTotal));
            }

            conteudo.append("=".repeat(48)).append("\n");
            adicionarTextoAlinhado(conteudo, "Total De Produtos", String.format("%.2f", totalProdutos), 48);

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    // comprovante e nota fiscal
    public void processarImpressaoComprovanteEnotaFiscal(Venda venda, Integer numeroCupom, Matriz matriz) {
        // String nomeImpressoraCaixa = venda.getNomeImpressora();

        if (venda.getNotaFiscal()) {
            if (venda.getImprimirNotaFiscal()) {
                //System.out.println("impremindo nota fiscal");
            }
            // Chama o service de nota fiscal para gerar a nota
//                notaFiscalService.gerarNotaFiscal(venda);

            // Aqui você pode gerar o XML da NF-e e assinar digitalmente
//                notaFiscalService.gerarXMLNotaFiscal(venda);
            processarImpressaoComprovanteRecebimento(venda, numeroCupom);

        } else {
            processarImpressaoComprovanteRecebimento(venda, numeroCupom);
        }

    }

    // conta
    public void processarImpressaoConta(Venda venda, Integer quantedade) {
        String conteudoConta = prepararConteudoConta(venda, quantedade);
        byte[] conteudoBytes = conteudoConta.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(venda.getMatriz().getId());
        impressao.setNomeImpressora(venda.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoConta(Venda venda, Integer quantedade) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "CONTA");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3);
            if (venda.getMesa() != null) {
                ativarNegrito(conteudo);
                conteudo.append("Mesa: ").append(venda.getMesa()).append("\n");
                desativarNegrito(conteudo);
            }
            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append(new SimpleDateFormat("dd/MM/yy HH:mm").format(new Timestamp(System.currentTimeMillis()))).append("\n");

            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append(String.format("%-26s %4s %7s %8s\n", "PRODUTO", "QTDE", "VAL.UN", "VAL.TOT"));
            conteudo.append("=".repeat(48)).append("\n");

            BigDecimal totalProdutos = BigDecimal.ZERO;

            for (ProdutoVenda produtoVenda : venda.getProdutoVendas()) {
                String nomeProduto = produtoVenda.getProduto().getNome();
                if (nomeProduto.length() > 26) {
                    nomeProduto = nomeProduto.substring(0, 26);
                }

                int quantidade = produtoVenda.getQuantidade().intValue();
                BigDecimal valorUnitario = produtoVenda.getProduto().getValor();
                BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));
                List<Observacoes> observacoes = produtoVenda.getObservacoesProdutoVenda();

                conteudo.append(String.format("%-26s %4d %7.2f %8.2f\n", nomeProduto, quantidade, valorUnitario.doubleValue(), valorTotal.doubleValue()));

                if (observacoes != null && !observacoes.isEmpty()) {
                    definirTamanhoFonte(conteudo, 1, 1); // Fonte menor para observações
                    for (Observacoes observacao1 : observacoes) {
                        BigDecimal valorObs = observacao1.getValor();
                        if (valorObs != null && valorObs.compareTo(BigDecimal.ZERO) > 0) {
                            adicionarTextoAlinhado(conteudo, "    -" + observacao1.getObservacao(), String.format("%.2f", valorObs), 48);
                        }
                    }
                }
            }

            conteudo.append("=".repeat(48)).append("\n");

            ativarNegrito(conteudo);
            adicionarTextoAlinhado(conteudo, "TOTAL PRODUTOS", String.format("%.2f", totalProdutos), 48);
            if (venda.getValorServico() != null && venda.getValorServico().compareTo(BigDecimal.ZERO) > 0) {
                adicionarTextoAlinhado(conteudo, "SERVICO", String.format("%.2f", venda.getValorServico()), 48);
                totalProdutos = totalProdutos.add(venda.getValorServico());
            }
            // Se quiser exibir desconto, descomente:
        /*
        if (venda.getDesconto() != null && venda.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
            adicionarTextoAlinhado(conteudo, "DESCONTO", "-" + venda.getDesconto()), 48);
            totalProdutos = totalProdutos.subtract(venda.getDesconto());
        }
        */
            adicionarTextoAlinhado(conteudo, "TOTAL A PAGAR", String.format("%.2f", totalProdutos), 48);
            desativarNegrito(conteudo);

            conteudo.append("\n");

            conteudo.append("Numero de pessoas: ").append(quantedade).append("\n");
            BigDecimal valorCada = totalProdutos.divide(BigDecimal.valueOf(quantedade), 2, java.math.RoundingMode.HALF_UP);
            conteudo.append("Valor por cada: R$ ").append(valorCada.setScale(2, java.math.RoundingMode.HALF_UP)).append("\n");
            conteudo.append("Primeiro Pedido: ").append(new SimpleDateFormat("HH:mm").format(venda.getDataVenda())).append(" hs").append("\n");

            Duration duration = Duration.between(venda.getDataVenda().toInstant(), new Timestamp(System.currentTimeMillis()).toInstant());
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds() % 60;
            conteudo.append("Tempo De Permanencia: ").append(String.format("%02d:%02d:%02d", hours, minutes, seconds)).append("\n");

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    // sangria e suprimento
    public void processarConteudoSangria(Sangria sangria) {
        String conteudoSangria = prepararConteudoSangria(sangria);

        byte[] conteudoBytes = conteudoSangria.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(sangria.getCaixa().getMatriz().getId());
        impressao.setNomeImpressora(sangria.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoSangria(Sangria sangria) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "SANGRIA");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3); // Fonte maior

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(sangria.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(sangria.getDataSangria())).append("\n");


            definirTamanhoFonte(conteudo, 1, 1);
            String motivo = sangria.getMotivo();
            if (motivo == null || motivo.trim().isEmpty()) {
                motivo = "";
            }
            conteudo.append("Motivo: ").append(motivo).append("\n\n\n");


            definirTamanhoFonte(conteudo, 3, 3);
            ativarNegrito(conteudo);
            conteudo.append("Valor: R$ ").append(sangria.getValor()).append("\n");
            desativarNegrito(conteudo);

            conteudo.append("\n\n");

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void processarConteudoSuprimento(Suprimento suprimento) {
        String conteudoSuprimento = prepararConteudoSuprimento(suprimento);
        byte[] conteudoBytes = conteudoSuprimento.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(suprimento.getCaixa().getMatriz().getId());
        impressao.setNomeImpressora(suprimento.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoSuprimento(Suprimento suprimento) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "SUPRIMENTO");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(suprimento.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(suprimento.getDataSuprimento())).append("\n");


            definirTamanhoFonte(conteudo, 1, 1);
            String motivo = suprimento.getMotivo();
            if (motivo == null || motivo.trim().isEmpty()) {
                motivo = "";
            }
            conteudo.append("Motivo: ").append(motivo).append("\n\n\n");


            definirTamanhoFonte(conteudo, 3, 3);
            ativarNegrito(conteudo);
            conteudo.append("Valor: R$ ").append(suprimento.getValor()).append("\n");
            desativarNegrito(conteudo);

            conteudo.append("\n\n");

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void processarConteudoGorjeta(Gorjeta gorjeta) {
        String conteudoGorjeta = prepararConteudoGorjeta(gorjeta);
        byte[] conteudoBytes = conteudoGorjeta.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(gorjeta.getCaixa().getMatriz().getId());
        impressao.setNomeImpressora(gorjeta.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoGorjeta(Gorjeta gorjeta) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "GORJETA");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(gorjeta.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(gorjeta.getDataGorjeta())).append("\n");

            definirTamanhoFonte(conteudo, 3, 3);
            ativarNegrito(conteudo);

            if (gorjeta.getDinheiro() != null && gorjeta.getDinheiro().compareTo(BigDecimal.ZERO) > 0) {
                conteudo.append("Dinheiro: R$ ").append(gorjeta.getDinheiro().setScale(2, java.math.RoundingMode.HALF_UP)).append("\n");
            }
            if (gorjeta.getDebito() != null && gorjeta.getDebito().compareTo(BigDecimal.ZERO) > 0) {
                conteudo.append("Débito: R$ ").append(gorjeta.getDebito().setScale(2, java.math.RoundingMode.HALF_UP)).append("\n");
            }
            if (gorjeta.getCredito() != null && gorjeta.getCredito().compareTo(BigDecimal.ZERO) > 0) {
                conteudo.append("Crédito: R$ ").append(gorjeta.getCredito().setScale(2, java.math.RoundingMode.HALF_UP)).append("\n");
            }
            if (gorjeta.getPix() != null && gorjeta.getPix().compareTo(BigDecimal.ZERO) > 0) {
                conteudo.append("Pix: R$ ").append(gorjeta.getPix().setScale(2, java.math.RoundingMode.HALF_UP)).append("\n");
            }

            desativarNegrito(conteudo);

            conteudo.append("\n\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");
            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void processarConteudoCaixaAbertura(Caixa caixa) {
        String conteudoCaixa = prepararConteudoCaixaAbertura(caixa);

        byte[] conteudoBytes = conteudoCaixa.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(caixa.getMatriz().getId());
        impressao.setNomeImpressora(caixa.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoCaixaAbertura(Caixa caixa) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0);
            conteudo.append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "ABERTURA DE CAIXA");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3); // Fonte maior

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Operador: ").append(caixa.getFuncionario().getNome()).append("  ").append(new SimpleDateFormat("dd/MM/yy HH:mm").format(caixa.getDataAbertura())).append("\n\n\n");


            definirTamanhoFonte(conteudo, 3, 3);
            ativarNegrito(conteudo);
            conteudo.append("Valor: R$ ").append(caixa.getValorAbertura()).append("\n");
            desativarNegrito(conteudo);

            conteudo.append("\n\n");

            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void processarConteudoCaixaConferencia(Caixa caixa) {
        String conteudoCaixa = prepararConteudoCaixaConferencia(caixa);
        byte[] conteudoBytes = conteudoCaixa.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(caixa.getMatriz().getId());
        impressao.setNomeImpressora(caixa.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoCaixaConferencia(Caixa caixa) {
        StringBuilder conteudo = new StringBuilder();

        try {
            BigDecimal saldoDinheiroOrginal = Optional.ofNullable(caixaRepository.findTotalDinheiroByCaixaId(caixa.getId())).orElse(BigDecimal.ZERO);
            BigDecimal saldoDebitoOrginal = Optional.ofNullable(caixaRepository.findTotalDebitoByCaixaId(caixa.getId())).orElse(BigDecimal.ZERO);
            BigDecimal saldoCreditoOrginal = Optional.ofNullable(caixaRepository.findTotalCreditoByCaixaId(caixa.getId())).orElse(BigDecimal.ZERO);
            BigDecimal saldoPixOrginal = Optional.ofNullable(caixaRepository.findTotalPixByCaixaId(caixa.getId())).orElse(BigDecimal.ZERO);

            BigDecimal totalSuprimentos = BigDecimal.ZERO;
            BigDecimal totalSangrias = BigDecimal.ZERO;
            BigDecimal totalGorjetas = BigDecimal.ZERO;
            BigDecimal totalDescontos = Optional.ofNullable(caixaRepository.findTotalDescontosByCaixaId(caixa.getId())).orElse(BigDecimal.ZERO);
            BigDecimal totalServicos = Optional.ofNullable(caixaRepository.findTotalServicosByCaixaId(caixa.getId())).orElse(BigDecimal.ZERO);


            BigDecimal saldoDinheiro = Optional.ofNullable(caixa.getSaldoDinheiro()).orElse(BigDecimal.ZERO);
            BigDecimal saldoDebito = Optional.ofNullable(caixa.getSaldoDebito()).orElse(BigDecimal.ZERO);
            BigDecimal saldoCredito = Optional.ofNullable(caixa.getSaldoCredito()).orElse(BigDecimal.ZERO);
            BigDecimal saldoPix = Optional.ofNullable(caixa.getSaldoPix()).orElse(BigDecimal.ZERO);
            BigDecimal valorAbertura = Optional.ofNullable(caixa.getValorAbertura()).orElse(BigDecimal.ZERO);

            conteudo.append((char) 27).append((char) 51).append((char) 0).append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");
            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "CONFERENCIA DE CAIXA");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Data De Abertura: ").append(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(caixa.getDataAbertura())).append("\n");
            if (caixa.getDataFechamento() != null) {
                conteudo.append("Data De Fechamento: ").append(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(caixa.getDataFechamento())).append("\n");
            }
            conteudo.append("Operador: ").append(caixa.getFuncionario().getNome()).append("\n");

            // Saldos informados no fechamento
            conteudo.append("=".repeat(48)).append("\n");
            definirTamanhoFonte(conteudo, 3, 3);
            centralizarTexto(conteudo, "SALDOS INFORMADOS");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");
            adicionarTextoAlinhado(conteudo, "DINHEIRO", String.format("%.2f", saldoDinheiro), 48);
            adicionarTextoAlinhado(conteudo, "CARTAO DEBITO", String.format("%.2f", saldoDebito), 48);
            adicionarTextoAlinhado(conteudo, "CARTAO CREDITO", String.format("%.2f", saldoCredito), 48);
            adicionarTextoAlinhado(conteudo, "PIX", String.format("%.2f", saldoPix), 48);
            conteudo.append(" ".repeat(38)).append("-".repeat(10)).append("\n");
            BigDecimal totalInformado = saldoDinheiro.add(saldoDebito).add(saldoCredito).add(saldoPix);
            adicionarTextoAlinhado(conteudo, "TOTAL INFORMADO", String.format("%.2f", totalInformado), 48);

            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 3, 3);
            centralizarTexto(conteudo, "ENTRADAS (VENDAS)");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            adicionarTextoAlinhado(conteudo, "DINHEIRO", String.format("%.2f", saldoDinheiroOrginal) + " +", 48);
            adicionarTextoAlinhado(conteudo, "CARTAO DEBITO", String.format("%.2f", saldoDebitoOrginal) + " +", 48);
            adicionarTextoAlinhado(conteudo, "CARTAO CREDITO", String.format("%.2f", saldoCreditoOrginal) + " +", 48);
            adicionarTextoAlinhado(conteudo, "PIX", String.format("%.2f", saldoPixOrginal), 48);

            conteudo.append(" ".repeat(38)).append("-".repeat(10)).append("\n");
            BigDecimal totalEntradas = saldoDinheiroOrginal.add(saldoDebitoOrginal).add(saldoCreditoOrginal).add(saldoPixOrginal);
            adicionarTextoAlinhado(conteudo, "TOTAL", String.format("%.2f", totalEntradas) + " =", 48);
            adicionarTextoAlinhado(conteudo, "Valor Abertura: R$ ", String.format("%.2f", valorAbertura) + " +", 48);
            conteudo.append(" ".repeat(38)).append("-".repeat(10)).append("\n");
            BigDecimal totalFinal = totalEntradas.add(valorAbertura);
            adicionarTextoAlinhado(conteudo, "Total Final", String.format("%.2f", totalFinal), 48);
            adicionarTextoAlinhado(conteudo, "SERVICO (INCLUSO NAS VENDAS):", String.format("%.2f", totalServicos), 48);
            conteudo.append("OBS: O servico ja esta incluso nos valores acima.");
            conteudo.append("\n");

            if (!caixa.getSuprimentos().isEmpty()) {
                definirTamanhoFonte(conteudo, 3, 3);
                centralizarTexto(conteudo, "SUPRIMENTOS");
                definirTamanhoFonte(conteudo, 1, 1);
                conteudo.append("=".repeat(48)).append("\n");

                for (Suprimento suprimento : caixa.getSuprimentos()) {
                    BigDecimal valor = suprimento.getValor();
                    String dataFormatada = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(suprimento.getDataSuprimento());
                    adicionarTextoAlinhado(conteudo, "Data: " + dataFormatada, String.format("%.2f", valor), 48);
                    conteudo.append("Motivo: ").append(suprimento.getMotivo()).append("\n\n");
                    totalSuprimentos = totalSuprimentos.add(valor);
                }

                conteudo.append(" ".repeat(38)).append("-".repeat(10)).append("\n");
                adicionarTextoAlinhado(conteudo, "Total", String.format("%.2f", totalSuprimentos), 48);
            }
            if (!caixa.getSangrias().isEmpty()) {
                definirTamanhoFonte(conteudo, 3, 3);
                centralizarTexto(conteudo, "SANGRIAS");
                definirTamanhoFonte(conteudo, 1, 1);
                conteudo.append("=".repeat(48)).append("\n");

                for (Sangria sangria : caixa.getSangrias()) {
                    BigDecimal valor = sangria.getValor();
                    String dataFormatada = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(sangria.getDataSangria());
                    adicionarTextoAlinhado(conteudo, "Data: " + dataFormatada, String.format("%.2f", valor), 48);
                    conteudo.append("Motivo: ").append(sangria.getMotivo()).append("\n\n");
                    totalSangrias = totalSangrias.add(valor);
                }

                conteudo.append(" ".repeat(38)).append("-".repeat(10)).append("\n");
                adicionarTextoAlinhado(conteudo, "Total", String.format("%.2f", totalSangrias), 48);
            }
            if (!caixa.getGorjetas().isEmpty()) {
                definirTamanhoFonte(conteudo, 3, 3);
                centralizarTexto(conteudo, "GORJETAS");
                definirTamanhoFonte(conteudo, 1, 1);
                conteudo.append("=".repeat(48)).append("\n");
                for (Gorjeta gorjeta : caixa.getGorjetas()) {
                    BigDecimal valor = Optional.ofNullable(gorjeta.getDinheiro()).orElse(BigDecimal.ZERO)
                            .add(Optional.ofNullable(gorjeta.getDebito()).orElse(BigDecimal.ZERO))
                            .add(Optional.ofNullable(gorjeta.getCredito()).orElse(BigDecimal.ZERO))
                            .add(Optional.ofNullable(gorjeta.getPix()).orElse(BigDecimal.ZERO));
                    String dataFormatada = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(gorjeta.getDataGorjeta());
                    adicionarTextoAlinhado(conteudo, "Data: " + dataFormatada, String.format("%.2f", valor), 48);
                    totalGorjetas = totalGorjetas.add(valor);
                }
                conteudo.append(" ".repeat(38)).append("-".repeat(10)).append("\n");
                adicionarTextoAlinhado(conteudo, "Total", String.format("%.2f", totalGorjetas), 48);
            }
            conteudo.append("\n");
            conteudo.append("=".repeat(48)).append("\n");
            definirTamanhoFonte(conteudo, 3, 3);
            centralizarTexto(conteudo, "TOTAL FINAL");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            adicionarTextoAlinhado(conteudo, "Valor Abertura", String.format("%.2f", valorAbertura) + " +", 48);
            adicionarTextoAlinhado(conteudo, "DINHEIRO", String.format("%.2f", saldoDinheiroOrginal) + " +", 48);
            adicionarTextoAlinhado(conteudo, "CARTAO DEBITO", String.format("%.2f", saldoDebitoOrginal) + " +", 48);
            adicionarTextoAlinhado(conteudo, "CARTAO CREDITO", String.format("%.2f", saldoCreditoOrginal) + " +", 48);
            adicionarTextoAlinhado(conteudo, "PIX", String.format("%.2f", saldoPixOrginal) + " +", 48);
            adicionarTextoAlinhado(conteudo, "SUPRIMENTOS", String.format("%.2f", totalSuprimentos) + " +", 48);
            adicionarTextoAlinhado(conteudo, "GORJETAS", String.format("%.2f", totalGorjetas) + " +", 48);
            adicionarTextoAlinhado(conteudo, "SANGRIAS", String.format("%.2f", totalSangrias) + " -", 48);
            adicionarTextoAlinhado(conteudo, "DESCONTOS", String.format("%.2f", totalDescontos) + " -", 48);

            conteudo.append(" ".repeat(38)).append("-".repeat(10)).append("\n");
            BigDecimal totalFinalFinal = totalEntradas.add(totalSuprimentos).add(valorAbertura).add(totalGorjetas).subtract(totalSangrias).subtract(totalDescontos);
            adicionarTextoAlinhado(conteudo, "Total Final", String.format("%.2f", totalFinalFinal), 48);
            adicionarTextoAlinhado(conteudo, "SERVICO (INCLUSO NAS VENDAS):", String.format("%.2f", totalServicos), 48);
            conteudo.append("OBS: O servico ja esta incluso nos valores acima.");
            conteudo.append("=".repeat(48)).append("\n");

            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public void processarImpressaoConferencia(Venda venda, int numeroCupom) {
        List<ProdutoVenda> produtosAgrupados = agruparProdutos(venda.getProdutoVendas());

        String conteudoConferencia = prepararConteudoConferencia(venda, produtosAgrupados, numeroCupom);
        byte[] conteudoBytes = conteudoConferencia.getBytes(StandardCharsets.UTF_8);

        Impressao impressao = new Impressao();
        impressao.setMatrizId(venda.getMatriz().getId());
        impressao.setNomeImpressora(venda.getNomeImpressora());
        impressao.setConteudoImpressao(conteudoBytes);
        impressao.setStatus(true);
        impressaoRepository.save(impressao);
    }

    public String prepararConteudoConferencia(Venda venda, List<ProdutoVenda> produtosAgrupados, int numeroCupom) {
        StringBuilder conteudo = new StringBuilder();

        try {
            conteudo.append((char) 27).append((char) 51).append((char) 0).append("\n");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            centralizarTexto(conteudo, "CONFERENCIA");
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            definirTamanhoFonte(conteudo, 4, 4);
            if (venda.getRetirada()) {
                centralizarTexto(conteudo, "RETIRADA");
            } else if (venda.getEntrega()) {
                centralizarTexto(conteudo, "ENTREGA");
            }
            definirTamanhoFonte(conteudo, 1, 1);
            conteudo.append("=".repeat(48)).append("\n");

            ativarNegrito(conteudo);
            definirTamanhoFonte(conteudo, 4, 4);
            conteudo.append("Cupom: ").append(numeroCupom).append("\n");
            desativarNegrito(conteudo);

            definirTamanhoFonte(conteudo, 1, 1);
            definirEspacamentoCaracteres(conteudo, 5);
            conteudo.append("Cliente: ").append(venda.getCliente().getNome()).append("\n");
            conteudo.append("Telefone: ").append(venda.getCliente().getCelular()).append("\n");
            if (venda.getEndereco() != null) {
                conteudo.append("Endereco: ").append(venda.getEndereco().getRua()).append(", ").append(venda.getEndereco().getNumero()).append("\n");
                conteudo.append("Bairro: ").append(venda.getEndereco().getBairro()).append("\n");
                conteudo.append("Cidade: ").append(venda.getEndereco().getCidade()).append(" ").append(venda.getEndereco().getEstado()).append("\n");
                conteudo.append("Cep: ").append(venda.getEndereco().getCep()).append("\n");
                if (venda.getEndereco().getComplemento() != null) {
                    conteudo.append("Comp: ").append(venda.getEndereco().getComplemento()).append("\n");
                }
                if (venda.getEndereco().getReferencia() != null) {
                    conteudo.append("Ref: ").append(venda.getEndereco().getReferencia()).append("\n\n");
                }
            }
            conteudo.append("=".repeat(48)).append("\n");

            conteudo.append(String.format("%-26s %4s %7s %8s\n", "PRODUTO", "QTDE", "VAL.UN", "VAL.TOT"));
            conteudo.append("=".repeat(48)).append("\n");

            BigDecimal valorTotalProdutos = BigDecimal.ZERO;
            for (ProdutoVenda produtoVenda : produtosAgrupados) {
                String nomeProduto = produtoVenda.getProduto().getNome();
                if (nomeProduto.length() > 26) {
                    nomeProduto = nomeProduto.substring(0, 26);
                }

                BigDecimal quantidade = produtoVenda.getQuantidade();
                BigDecimal valorUnitario = produtoVenda.getProduto().getValor();
                BigDecimal valorTotal = valorUnitario.multiply(quantidade);

                conteudo.append(String.format("%-26s %4d %7.2f %8.2f\n", nomeProduto, quantidade.intValue(), valorUnitario, valorTotal));
                valorTotalProdutos = valorTotalProdutos.add(valorTotal);
            }

            conteudo.append("=".repeat(48)).append("\n");

            definirEspacamentoCaracteres(conteudo, 1);
            adicionarTextoAlinhado(conteudo, "Total De Produtos", String.format("%.2f", valorTotalProdutos), 48);
            if (venda.getTaxaEntrega() != null) {
                adicionarTextoAlinhado(conteudo, "Taxa De Entrega", String.format("%.2f", venda.getTaxaEntrega()), 48);
            }
            conteudo.append("=".repeat(48)).append("\n");
            conteudo.append("FORMAS DE PAGAMENTO").append("\n");

            if (venda.getVendaPagamento() != null) {
                VendaPagamento pagamento = venda.getVendaPagamento();
                if (pagamento.getDinheiro() != null && pagamento.getDinheiro().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "DINHEIRO", String.format("%.2f", pagamento.getDinheiro()), 48);
                }
                if (pagamento.getDebito() != null && pagamento.getDebito().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "CARTAO DE DEBITO", String.format("%.2f", pagamento.getDebito()), 48);
                }
                if (pagamento.getCredito() != null && pagamento.getCredito().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "CARTAO DE CREDITO", String.format("%.2f", pagamento.getCredito()), 48);
                }
                if (pagamento.getPix() != null && pagamento.getPix().compareTo(BigDecimal.ZERO) > 0) {
                    adicionarTextoAlinhado(conteudo, "PIX", String.format("%.2f", pagamento.getPix()), 48);
                }
            }

            conteudo.append("=".repeat(48)).append("\n");
            adicionarTextoAlinhado(conteudo, "Subtotal", String.format("%.2f", venda.getValorTotal()), 48);
            adicionarTextoAlinhado(conteudo, "Desconto", String.format("%.2f", venda.getDesconto() != null ? venda.getDesconto() : BigDecimal.ZERO), 48);
            adicionarTextoAlinhado(conteudo, "Total Recebido", String.format("%.2f", venda.getValorTotal()), 48);
            conteudo.append("\n\n\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    // metedos gerais
    private String criarChaveAgrupamento(ProdutoVenda produtoVenda) {
        // Cria uma chave única baseada no produto suas observações e observação adicional
        StringBuilder chave = new StringBuilder();
        chave.append(produtoVenda.getProduto().getId());

        if (produtoVenda.getObservacoesProdutoVenda() != null) {
            produtoVenda.getObservacoesProdutoVenda().forEach(obs -> chave.append(obs.getId()));
        }

        if (produtoVenda.getObservacaoProdutoVenda() != null) {
            chave.append(produtoVenda.getObservacaoProdutoVenda());
        }

        return chave.toString();
    }

    public void definirEspacamentoCaracteres(StringBuilder conteudo, int espacos) {
        conteudo.append((char) 27).append((char) 32).append((char) espacos);
    }

    public void ativarNegrito(StringBuilder conteudo) {
        conteudo.append((char) 27).append((char) 69).append((char) 1);
    }

    public void desativarNegrito(StringBuilder conteudo) {
        conteudo.append((char) 27).append((char) 69).append((char) 0);
    }

    public void definirTamanhoFonte(StringBuilder conteudo, int multiplicadorLargura, int multiplicadorAltura) {
        multiplicadorLargura = Math.max(1, Math.min(multiplicadorLargura, 8));
        multiplicadorAltura = Math.max(1, Math.min(multiplicadorAltura, 8));

        int n = (multiplicadorAltura - 1) << 4 | (multiplicadorLargura - 1);
        conteudo.append((char) 27).append((char) 33).append((char) n);
    }

    private void centralizarTexto(StringBuilder conteudo, String texto) {
        // ESC a 1 centraliza o texto nas impressoras ESC/POS
        conteudo.append((char) 27).append((char) 97).append((char) 1); // Comando para centralizar o texto
        conteudo.append(texto).append("\n");
        // Voltar para o alinhamento padrão (à esquerda)
        conteudo.append((char) 27).append((char) 97).append((char) 0); // Comando para alinhar à esquerda
    }

    private void adicionarTextoAlinhado(StringBuilder conteudo, String textoEsquerda, String textoDireita, int larguraTotal) {
        int espacos = larguraTotal - textoEsquerda.length() - textoDireita.length();
        conteudo.append(textoEsquerda).append(" ".repeat(espacos)).append(textoDireita).append("\n");
    }

    private ProdutoVenda criarCopiaProdutoVenda(ProdutoVenda produtoVendaOriginal) {
        ProdutoVenda copia = new ProdutoVenda();

        // Clonar propriedades relevantes do produtoVenda
        copia.setProduto(produtoVendaOriginal.getProduto()); // Mesma referência ao Produto
        copia.setQuantidade(produtoVendaOriginal.getQuantidade()); // Copiar quantidade
        copia.setObservacoesProdutoVenda(new ArrayList<>(produtoVendaOriginal.getObservacoesProdutoVenda())); // Copiar observações
        copia.setObservacaoProdutoVenda(produtoVendaOriginal.getObservacaoProdutoVenda()); // Copiar observação adicional, se houver

        return copia;
    }
}