package Ecomerce.assmar.DTOService;

import Ecomerce.assmar.Entity.*;
import Ecomerce.assmar.Repository.DepositoRepository;
import Ecomerce.assmar.Repository.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TratarEstoqueDeposito {

    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private DepositoRepository depositoRepository;

    public TratarEstoqueDeposito(EstoqueRepository estoqueRepository,
                                 DepositoRepository depositoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.depositoRepository = depositoRepository;
    }

    public ResultadoDesconto processarProdutoVenda(
            ProdutoVenda produtoVenda,
            Matriz matriz,
            boolean descontarOuDevolucao,
            ResultadoDesconto resultadoMemoria
    ) {
        if (resultadoMemoria == null) {
            resultadoMemoria = new ResultadoDesconto(); // 🔧 evita NullPointerException
        }
        Produto produto = produtoVenda.getProduto();
        BigDecimal quantidade = produtoVenda.getQuantidade();

        //System.out.println("\n▶️ Processando produto: " + produto.getNome() + " | Quantidade: " + quantidade + " | Modo: " + (descontarOuDevolucao ? "DESCONTO" : "DEVOLUÇÃO"));

        List<ObservacaoMateria> materiasParaIgnorar = new ArrayList<>();
        List<ObservacaoProduto> produtosParaIgnorar = new ArrayList<>();
        List<ObservacaoMateria> materiasExtras = new ArrayList<>();
        List<ObservacaoProduto> produtosExtras = new ArrayList<>();

        if (produtoVenda.getObservacoesProdutoVenda() != null) {
            for (Observacoes obs : produtoVenda.getObservacoesProdutoVenda()) {
                //System.out.println("   📝 Observação: " + obs.getObservacao());
                if (obs.getValidarExestencia()) {
                    if (obs.getExtra()) {
                        if (obs.getObservacaoMaterias() != null)
                            materiasExtras.addAll(obs.getObservacaoMaterias());
                        if (obs.getObservacaoProdutos() != null)
                            produtosExtras.addAll(obs.getObservacaoProdutos());
                    } else {
                        if (obs.getObservacaoMaterias() != null)
                            materiasParaIgnorar.addAll(obs.getObservacaoMaterias());
                        if (obs.getObservacaoProdutos() != null)
                            produtosParaIgnorar.addAll(obs.getObservacaoProdutos());
                    }
                }
            }
        }

        ResultadoDesconto resultado = new ResultadoDesconto();

        //condição ? valor_se_verdadeiro : valor_se_falso;
        // se descontarOuDevolucao true desconta e se false devolve
        // ➕ TRATAR EXTRAS
        if (!materiasExtras.isEmpty() || !produtosExtras.isEmpty()) {
            //System.out.println("   ➕ Processando EXTRAS");
            resultado = descontarOuDevolucao
                    ? processarDescontoObservacoes(quantidade, matriz, materiasExtras, produtosExtras, resultadoMemoria)
                    : processarDevolverObservacoes(quantidade, matriz, materiasExtras, produtosExtras);
        }

        // ➕ TRATAR PRODUTO PRINCIPAL
        if (produto.getValidarExestencia()) {
            //System.out.println("   📦 Processando PRODUTO PRINCIPAL");
            ResultadoDesconto parcial = descontarOuDevolucao
                    ? processarDescontoProduto(produto, quantidade, matriz, materiasParaIgnorar, produtosParaIgnorar, resultadoMemoria)
                    : processarDevolverProduto(produto, quantidade, matriz, materiasParaIgnorar, produtosParaIgnorar);
            resultado.addAllDeposito(parcial.getDepositos());
            resultado.addAllEstoque(parcial.getEstoques());
        } else {
            //System.out.println("   ❎ Produto sem controle de estoque.");
        }

        return resultado;
    }

    private ResultadoDesconto processarDescontoObservacoes(
            BigDecimal quantidadeVendida,
            Matriz matriz,
            List<ObservacaoMateria> materiasExtras,
            List<ObservacaoProduto> produtosExtras,
            ResultadoDesconto resultadoMemoria
    ) {
        ResultadoDesconto resultado = new ResultadoDesconto();

        if (!materiasExtras.isEmpty()) {
            resultado.addAllDeposito(descontarDepositoExtras(materiasExtras, quantidadeVendida, matriz, resultadoMemoria));
        }

        if (!produtosExtras.isEmpty()) {
            resultado.addAllEstoque(descontarEstoqueExtras(produtosExtras, quantidadeVendida, matriz, resultadoMemoria));
        }

        return resultado;
    }

    private List<Deposito> descontarDepositoExtras(
            List<ObservacaoMateria> extras,
            BigDecimal qtdVenda,
            Matriz matriz,
            ResultadoDesconto resultadoMemoria
    ) {
        List<Deposito> lista = new ArrayList<>();

        for (ObservacaoMateria extra : extras) {
            Materia materia = extra.getMateria();
            BigDecimal total = extra.getQuantidadeGasto().multiply(qtdVenda);

            // 🔁 Primeiro usa a memória
            List<Deposito> memoria = resultadoMemoria.getDepositos().stream()
                    .filter(d -> d.getMateria().getId().equals(materia.getId()) && d.getMatriz().getId().equals(matriz.getId()))
                    .collect(Collectors.toList());

            for (Deposito d : memoria) {
                BigDecimal disponivel = d.getQuantidade().subtract(d.getQuantidadeVendido());
                BigDecimal usado = disponivel.min(total);
                d.setQuantidadeVendido(d.getQuantidadeVendido().add(usado));
                total = total.subtract(usado);
                lista.add(d);
                if (total.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            // 🏦 Depois usa o banco, se necessário
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal disponivelBanco = depositoRepository.findTotalQuantidadeDisponivelByMateriaAndMatriz(materia.getId(), matriz.getId());
                if (disponivelBanco == null || disponivelBanco.compareTo(total) < 0)
                    throw new RuntimeException("❌ Depósito insuficiente para extra: " + materia.getNome());

                List<Deposito> depositos = depositoRepository.findByMateriaAndMatrizIdAndAtivoTrueOrderByDataAsc(materia, matriz.getId());
                for (Deposito d : depositos) {
                    BigDecimal disponivel = d.getQuantidade().subtract(d.getQuantidadeVendido());
                    BigDecimal usado = disponivel.min(total);
                    d.setQuantidadeVendido(d.getQuantidadeVendido().add(usado));
                    total = total.subtract(usado);
                    if (d.getQuantidade().compareTo(d.getQuantidadeVendido()) == 0) {
                        d.setAtivo(false);
                        d.setDataDesativar(new Timestamp(System.currentTimeMillis()));
                    }
                    lista.add(d);
                    if (total.compareTo(BigDecimal.ZERO) <= 0) break;
                }
            }
        }

        return lista;
    }

    private List<Estoque> descontarEstoqueExtras(
            List<ObservacaoProduto> extras,
            BigDecimal qtdVenda,
            Matriz matriz,
            ResultadoDesconto resultadoMemoria
    ) {
        List<Estoque> lista = new ArrayList<>();

        for (ObservacaoProduto extra : extras) {
            Produto produto = extra.getProduto();
            BigDecimal total = extra.getQuantidadeGasto().multiply(qtdVenda);

            if (produto.getEstocavel()) {
                lista.addAll(descontarEstoque(List.of(produto), total, matriz, resultadoMemoria));
            } else {
                ResultadoDesconto sub = descontarEstoqueProdutoRecursivo(produto.getProdutoCompostos(), total, matriz, resultadoMemoria);
                lista.addAll(sub.getEstoques());
            }
        }

        return lista;
    }

    private ResultadoDesconto processarDescontoProduto(
            Produto produto,
            BigDecimal qtdVendida,
            Matriz matriz,
            List<ObservacaoMateria> materiasParaIgnorar,
            List<ObservacaoProduto> produtosParaIgnorar,
            ResultadoDesconto resultadoMemoria
    ) {
        ResultadoDesconto resultado = new ResultadoDesconto();
        //System.out.println("➡️ Iniciando desconto do produto principal: " + produto.getNome());

        if (produto.getEstocavel()) {
            //System.out.println("   📦 Produto estocável. Descontando do estoque.");
            resultado.addAllEstoque(descontarEstoque(List.of(produto), qtdVendida, matriz, resultadoMemoria));
        } else {
            //System.out.println("   🧮 Produto não estocável. Usando ignorados.");

            if (produto.getProdutoMaterias() != null) {
                List<ProdutoMateria> filtradas = materiasParaIgnorar.isEmpty()
                        ? produto.getProdutoMaterias()
                        : produto.getProdutoMaterias().stream()
                        .filter(pm -> materiasParaIgnorar.stream()
                                .noneMatch(om -> om.getMateria().getId().equals(pm.getMateria().getId())))
                        .collect(Collectors.toList());

                resultado.addAllDeposito(descontarDeposito(filtradas, qtdVendida, matriz, resultadoMemoria));
            }

            if (produto.getProdutoCompostos() != null) {
                List<ProdutoComposto> filtrados = produtosParaIgnorar.isEmpty()
                        ? produto.getProdutoCompostos()
                        : produto.getProdutoCompostos().stream()
                        .filter(pc -> produtosParaIgnorar.stream()
                                .noneMatch(op -> op.getProduto().getId().equals(pc.getProdutoComposto().getId())))
                        .collect(Collectors.toList());

                ResultadoDesconto sub = descontarEstoqueProdutoRecursivo(filtrados, qtdVendida, matriz, resultadoMemoria);
                resultado.addAllEstoque(sub.getEstoques());
                resultado.addAllDeposito(sub.getDepositos());
            }
        }

        return resultado;
    }

    public List<Deposito> descontarDeposito(List<ProdutoMateria> materias, BigDecimal qtdVenda, Matriz matriz, ResultadoDesconto resultadoMemoria) {
        List<Deposito> lista = new ArrayList<>();

        for (ProdutoMateria pm : materias) {
            Materia materia = pm.getMateria();
            BigDecimal total = pm.getQuantidadeGasto().multiply(qtdVenda);

            // 🔁 Primeiro usa da memória (devolvidos mas ainda com saldo)
            List<Deposito> memoria = resultadoMemoria.getDepositos().stream()
                    .filter(d -> d.getMateria().getId().equals(materia.getId()) && d.getMatriz().getId().equals(matriz.getId()))
                    .collect(Collectors.toList());

            for (Deposito d : memoria) {
                BigDecimal disponivel = d.getQuantidade().subtract(d.getQuantidadeVendido());
                BigDecimal usado = disponivel.min(total);
                d.setQuantidadeVendido(d.getQuantidadeVendido().add(usado));
                total = total.subtract(usado);
                lista.add(d);
                if (total.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            // 🏦 Depois, banco
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal disponivel = depositoRepository.findTotalQuantidadeDisponivelByMateriaAndMatriz(materia.getId(), matriz.getId());
                if (disponivel == null || disponivel.compareTo(total) < 0)
                    throw new RuntimeException("❌ Depósito insuficiente para: " + materia.getNome());

                List<Deposito> depositos = depositoRepository.findByMateriaAndMatrizIdAndAtivoTrueOrderByDataAsc(materia, matriz.getId());
                for (Deposito d : depositos) {
                    BigDecimal disponivelAtual = d.getQuantidade().subtract(d.getQuantidadeVendido());
                    BigDecimal usado = disponivelAtual.min(total);
                    d.setQuantidadeVendido(d.getQuantidadeVendido().add(usado));
                    total = total.subtract(usado);
                    if (d.getQuantidade().compareTo(d.getQuantidadeVendido()) == 0) {
                        d.setAtivo(false);
                        d.setDataDesativar(new Timestamp(System.currentTimeMillis()));
                    }
                    lista.add(d);
                    if (total.compareTo(BigDecimal.ZERO) <= 0) break;
                }
            }
        }

        return lista;
    }

    private List<Estoque> descontarEstoque(List<Produto> produtos, BigDecimal total, Matriz matriz, ResultadoDesconto resultadoMemoria) {
        List<Estoque> lista = new ArrayList<>();

        for (Produto produto : produtos) {
            BigDecimal restante = total;

            // 🔁 Primeiro usa da memória
            List<Estoque> memoria = resultadoMemoria.getEstoques().stream()
                    .filter(e -> e.getProduto().getId().equals(produto.getId()) && e.getMatriz().getId().equals(matriz.getId()))
                    .collect(Collectors.toList());

            for (Estoque e : memoria) {
                BigDecimal disponivel = e.getQuantidade().subtract(e.getQuantidadeVendido());
                BigDecimal usado = disponivel.min(restante);
                e.setQuantidadeVendido(e.getQuantidadeVendido().add(usado));
                restante = restante.subtract(usado);
                lista.add(e);
                if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            // 🏦 Depois, banco
            if (restante.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal disponivel = estoqueRepository.findTotalQuantidadeDisponivelByProdutoAndMatriz(produto.getId(), matriz.getId());
                if (disponivel == null || disponivel.compareTo(restante) < 0)
                    throw new RuntimeException("❌ Estoque insuficiente: " + produto.getNome());

                List<Estoque> estoques = estoqueRepository.findByProdutoAndMatrizIdAndAtivoTrueOrderByDataAsc(produto, matriz.getId());
                for (Estoque e : estoques) {
                    BigDecimal qtd = e.getQuantidade().subtract(e.getQuantidadeVendido());
                    BigDecimal descontar = qtd.min(restante);
                    e.setQuantidadeVendido(e.getQuantidadeVendido().add(descontar));
                    restante = restante.subtract(descontar);
                    if (e.getQuantidade().compareTo(e.getQuantidadeVendido()) == 0) {
                        e.setAtivo(false);
                        e.setDataDesativar(new Timestamp(System.currentTimeMillis()));
                    }
                    lista.add(e);
                    if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
                }
            }
        }

        return lista;
    }

    public ResultadoDesconto descontarEstoqueProdutoRecursivo(List<ProdutoComposto> compostos, BigDecimal qtdVenda, Matriz matriz, ResultadoDesconto resultadoMemoria) {
        ResultadoDesconto resultado = new ResultadoDesconto();

        for (ProdutoComposto pc : compostos) {
            Produto produto = pc.getProdutoComposto();
            BigDecimal total = pc.getQuantidadeGasto().multiply(qtdVenda);

            if (produto.getEstocavel()) {
                resultado.addAllEstoque(descontarEstoque(List.of(produto), total, matriz, resultadoMemoria));
            } else {
                if (produto.getProdutoMaterias() != null && !produto.getProdutoMaterias().isEmpty()) {
                    resultado.addAllDeposito(descontarDeposito(produto.getProdutoMaterias(), total, matriz, resultadoMemoria));
                }

                if (produto.getProdutoCompostos() != null && !produto.getProdutoCompostos().isEmpty()) {
                    ResultadoDesconto sub = descontarEstoqueProdutoRecursivo(produto.getProdutoCompostos(), total, matriz, resultadoMemoria);
                    resultado.addAllEstoque(sub.getEstoques());
                    resultado.addAllDeposito(sub.getDepositos());
                }
            }
        }

        return resultado;
    }

    private ResultadoDesconto processarDevolverObservacoes(BigDecimal quantidadeVendida, Matriz matriz, List<ObservacaoMateria> materiasExtras, List<ObservacaoProduto> produtosExtras) {
        ResultadoDesconto resultado = new ResultadoDesconto();

        if (!materiasExtras.isEmpty()) {
            resultado.addAllDeposito(devolverDepositoExtras(materiasExtras, quantidadeVendida, matriz));
        }

        if (!produtosExtras.isEmpty()) {
            resultado.addAllEstoque(devolverEstoqueExtras(produtosExtras, quantidadeVendida, matriz));
        }

        return resultado;
    }

    private ResultadoDesconto processarDevolverProduto(Produto produto, BigDecimal qtdVendida, Matriz matriz, List<ObservacaoMateria> materiasParaIgnorar, List<ObservacaoProduto> produtosParaIgnorar) {
        ResultadoDesconto resultado = new ResultadoDesconto();
        //System.out.println("➡️ Iniciando devolução do produto principal: " + produto.getNome());

        if (produto.getEstocavel()) {
            resultado.addAllEstoque(devolverEstoque(List.of(produto), qtdVendida, matriz));
        } else {
            if (produto.getProdutoMaterias() != null) {
                List<ProdutoMateria> filtradas = materiasParaIgnorar.isEmpty()
                        ? produto.getProdutoMaterias()
                        : produto.getProdutoMaterias().stream()
                        .filter(pm -> materiasParaIgnorar.stream().noneMatch(om -> om.getMateria().getId().equals(pm.getMateria().getId())))
                        .collect(Collectors.toList());

                resultado.addAllDeposito(devolverDeposito(filtradas, qtdVendida, matriz));
            }

            if (produto.getProdutoCompostos() != null) {
                List<ProdutoComposto> filtrados = produtosParaIgnorar.isEmpty()
                        ? produto.getProdutoCompostos()
                        : produto.getProdutoCompostos().stream()
                        .filter(pc -> produtosParaIgnorar.stream().noneMatch(op -> op.getProduto().getId().equals(pc.getProdutoComposto().getId())))
                        .collect(Collectors.toList());

                ResultadoDesconto sub = devolverEstoqueProdutoRecursivo(filtrados, qtdVendida, matriz);
                resultado.addAllEstoque(sub.getEstoques());
                resultado.addAllDeposito(sub.getDepositos());
            }
        }

        return resultado;
    }

    private List<Estoque> devolverEstoqueExtras(List<ObservacaoProduto> extras, BigDecimal qtdVenda, Matriz matriz) {
        List<Estoque> lista = new ArrayList<>();

        for (ObservacaoProduto extra : extras) {
            Produto produto = extra.getProduto();
            BigDecimal total = extra.getQuantidadeGasto().multiply(qtdVenda);

            if (produto.getEstocavel()) {
                lista.addAll(devolverEstoque(List.of(produto), total, matriz));
            } else {
                ResultadoDesconto sub = devolverEstoqueProdutoRecursivo(produto.getProdutoCompostos(), total, matriz);
                lista.addAll(sub.getEstoques());
            }
        }

        return lista;
    }

    private ResultadoDesconto devolverEstoqueProdutoRecursivo(List<ProdutoComposto> compostos, BigDecimal qtdVenda, Matriz matriz) {
        ResultadoDesconto resultado = new ResultadoDesconto();

        for (ProdutoComposto pc : compostos) {
            Produto produto = pc.getProdutoComposto();
            BigDecimal total = pc.getQuantidadeGasto().multiply(qtdVenda);

            if (produto.getEstocavel()) {
                resultado.addAllEstoque(devolverEstoque(List.of(produto), total, matriz));
            } else {
                if (produto.getProdutoMaterias() != null) {
                    resultado.addAllDeposito(devolverDeposito(produto.getProdutoMaterias(), total, matriz));
                }

                if (produto.getProdutoCompostos() != null) {
                    ResultadoDesconto sub = devolverEstoqueProdutoRecursivo(produto.getProdutoCompostos(), total, matriz);
                    resultado.addAllEstoque(sub.getEstoques());
                    resultado.addAllDeposito(sub.getDepositos());
                }
            }
        }

        return resultado;
    }

    private List<Estoque> devolverEstoque(List<Produto> produtos, BigDecimal total, Matriz matriz) {
        List<Estoque> lista = new ArrayList<>();

        for (Produto produto : produtos) {
            BigDecimal restante = total;

            // 1. Devolver para estoques ativos (FIFO)
            List<Estoque> estoquesAtivos = estoqueRepository.findByProdutoAndMatrizIdAndAtivoTrueOrderByDataAsc(produto, matriz.getId());
            for (Estoque e : estoquesAtivos) {
                BigDecimal vendido = e.getQuantidadeVendido();
                BigDecimal devolver = vendido.min(restante);

                e.setQuantidadeVendido(vendido.subtract(devolver));
                restante = restante.subtract(devolver);

                estoqueRepository.save(e);
                lista.add(e);

                if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            // 2. Se ainda houver, devolver para estoques desativados (LIFO)
            if (restante.compareTo(BigDecimal.ZERO) > 0) {
                List<Estoque> estoquesDesativados = estoqueRepository.findByProdutoAndMatrizIdAndAtivoFalseOrderByDataCadastrarDesc(produto, matriz.getId());

                for (Estoque e : estoquesDesativados) {
                    BigDecimal vendido = e.getQuantidadeVendido();
                    BigDecimal devolver = vendido.min(restante);

                    e.setQuantidadeVendido(vendido.subtract(devolver));
                    restante = restante.subtract(devolver);

                    if (e.getQuantidadeVendido().compareTo(e.getQuantidade()) < 0) {
                        e.setAtivo(true);
                        e.setDataDesativar(null);
                    }

                    estoqueRepository.save(e);
                    lista.add(e);

                    if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
                }
            }

            if (restante.compareTo(BigDecimal.ZERO) > 0)
                throw new RuntimeException("❌ Não foi possível devolver toda a quantidade para o estoque: " + produto.getNome());
        }

        return lista;
    }

    private List<Deposito> devolverDeposito(List<ProdutoMateria> materias, BigDecimal qtdVenda, Matriz matriz) {
        List<Deposito> lista = new ArrayList<>();

        for (ProdutoMateria pm : materias) {
            Materia materia = pm.getMateria();
            BigDecimal total = pm.getQuantidadeGasto().multiply(qtdVenda);

            // 1. Devolver para depósitos ativos (FIFO)
            List<Deposito> depositosAtivos = depositoRepository.findByMateriaAndMatrizIdAndAtivoTrueOrderByDataAsc(materia, matriz.getId());
            for (Deposito d : depositosAtivos) {
                BigDecimal vendido = d.getQuantidadeVendido();
                BigDecimal devolver = vendido.min(total);

                d.setQuantidadeVendido(vendido.subtract(devolver));
                total = total.subtract(devolver);

                depositoRepository.save(d);
                lista.add(d);

                if (total.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            // 2. Se ainda houver quantidade, devolver para depósitos desativados (LIFO)
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                List<Deposito> desativados = depositoRepository.findByMateriaAndMatrizIdAndAtivoFalseOrderByDataCadastrarDesc(materia, matriz.getId());
                for (Deposito d : desativados) {
                    BigDecimal vendido = d.getQuantidadeVendido();
                    BigDecimal devolver = vendido.min(total);

                    d.setQuantidadeVendido(vendido.subtract(devolver));
                    total = total.subtract(devolver);

                    if (d.getQuantidadeVendido().compareTo(d.getQuantidade()) < 0) {
                        d.setAtivo(true);
                        d.setDataDesativar(null);
                    }

                    depositoRepository.save(d);
                    lista.add(d);

                    if (total.compareTo(BigDecimal.ZERO) <= 0) break;
                }
            }

            if (total.compareTo(BigDecimal.ZERO) > 0)
                throw new RuntimeException("❌ Não foi possível devolver toda a quantidade para o depósito: " + materia.getNome());
        }

        return lista;
    }

    private List<Deposito> devolverDepositoExtras(List<ObservacaoMateria> extras, BigDecimal qtdVenda, Matriz matriz) {
        List<Deposito> lista = new ArrayList<>();

        for (ObservacaoMateria extra : extras) {
            Materia materia = extra.getMateria();
            BigDecimal total = extra.getQuantidadeGasto().multiply(qtdVenda);

            List<Deposito> depositosAtivos = depositoRepository.findByMateriaAndMatrizIdAndAtivoTrueOrderByDataAsc(materia, matriz.getId());
            for (Deposito d : depositosAtivos) {
                BigDecimal vendido = d.getQuantidadeVendido();
                BigDecimal devolver = vendido.min(total);

                d.setQuantidadeVendido(vendido.subtract(devolver));
                total = total.subtract(devolver);
                depositoRepository.save(d);
                lista.add(d);

                if (total.compareTo(BigDecimal.ZERO) <= 0) break;
            }

            if (total.compareTo(BigDecimal.ZERO) > 0) {
                List<Deposito> desativados = depositoRepository.findByMateriaAndMatrizIdAndAtivoFalseOrderByDataCadastrarDesc(materia, matriz.getId());
                for (Deposito d : desativados) {
                    BigDecimal vendido = d.getQuantidadeVendido();
                    BigDecimal devolver = vendido.min(total);

                    d.setQuantidadeVendido(vendido.subtract(devolver));
                    total = total.subtract(devolver);

                    if (d.getQuantidadeVendido().compareTo(d.getQuantidade()) < 0) {
                        d.setAtivo(true);
                        d.setDataDesativar(null);
                    }

                    depositoRepository.save(d);
                    lista.add(d);

                    if (total.compareTo(BigDecimal.ZERO) <= 0) break;
                }
            }

            if (total.compareTo(BigDecimal.ZERO) > 0)
                throw new RuntimeException("❌ Não foi possível devolver toda a quantidade extra para o depósito: " + materia.getNome());
        }

        return lista;
    }
    public void processarAlteracoesDeEstoque(ComparacaoVendaResultado comparacao, Venda vendaAtualizada, Matriz matriz) {
        ResultadoDesconto resultadoDevolucao = new ResultadoDesconto();
        for (ProdutoVenda pv : comparacao.getRemovidos()) {
            ResultadoDesconto parcial = processarProdutoVenda(pv, matriz, false, null);
            resultadoDevolucao.addAllDeposito(parcial.getDepositos());
            resultadoDevolucao.addAllEstoque(parcial.getEstoques());
            pv.setAtivo(false);
        }
        vendaAtualizada.getProdutoVendas().addAll(comparacao.getRemovidos());

        ResultadoDesconto resultadoDesconto = new ResultadoDesconto();
        for (ProdutoVenda pv : comparacao.getAdicionados()) {
            ResultadoDesconto parcial = processarProdutoVenda(pv, matriz, true, resultadoDevolucao);
            resultadoDesconto.addAllDeposito(parcial.getDepositos());
            resultadoDesconto.addAllEstoque(parcial.getEstoques());
        }

        if (!resultadoDevolucao.getDepositos().isEmpty()) {
            depositoRepository.saveAll(resultadoDevolucao.getDepositos());
        }
        if (!resultadoDevolucao.getEstoques().isEmpty()) {
            estoqueRepository.saveAll(resultadoDevolucao.getEstoques());
        }
        if (!resultadoDesconto.getDepositos().isEmpty()) {
            depositoRepository.saveAll(resultadoDesconto.getDepositos());
        }
        if (!resultadoDesconto.getEstoques().isEmpty()) {
            estoqueRepository.saveAll(resultadoDesconto.getEstoques());
        }
    }

    public ComparacaoVendaResultado compararVendas(Venda vendaOriginal, Venda vendaAtualizada) {
        ComparacaoVendaResultado comparacao = new ComparacaoVendaResultado();

        List<ProdutoVenda> originaisAtivos = vendaOriginal.getProdutoVendas().stream()
                .filter(ProdutoVenda::getAtivo)
                .collect(Collectors.toList());

        List<ProdutoVenda> atualizadosAtivos = vendaAtualizada.getProdutoVendas().stream()
                .filter(ProdutoVenda::getAtivo)
                .collect(Collectors.toList());

        Map<Long, ProdutoVenda> mapaOriginal = originaisAtivos.stream()
                .filter(pv -> pv.getId() != null)
                .collect(Collectors.toMap(ProdutoVenda::getId, pv -> pv));

        Map<Long, ProdutoVenda> mapaAtualizado = atualizadosAtivos.stream()
                .filter(pv -> pv.getId() != null)
                .collect(Collectors.toMap(ProdutoVenda::getId, pv -> pv));

        // 1. Identificar removidos
        for (ProdutoVenda original : originaisAtivos) {
            if (original.getId() == null || !mapaAtualizado.containsKey(original.getId())) {
                comparacao.getRemovidos().add(original);
            }
        }

        // 2. Identificar adicionados
        for (ProdutoVenda atualizado : atualizadosAtivos) {
            if (atualizado.getId() == null || !mapaOriginal.containsKey(atualizado.getId())) {
                comparacao.getAdicionados().add(atualizado);
            }
        }

        // 3. Comparar quantidades (só para os que existem em ambas)
        // 3. Comparar quantidades (só para os que existem em ambas)
        for (ProdutoVenda atualizado : atualizadosAtivos) {
            if (atualizado.getId() != null && mapaOriginal.containsKey(atualizado.getId())) {
                ProdutoVenda original = mapaOriginal.get(atualizado.getId());

                BigDecimal qtdAtual = atualizado.getQuantidade();
                BigDecimal qtdOriginal = original.getQuantidade();

                int comp = qtdAtual.compareTo(qtdOriginal);
                //System.out.println("🔍 Comparando produto ID " + atualizado.getId() + " | QtdAtual: " + qtdAtual + " | QtdOriginal: " + qtdOriginal + " | Resultado: " + comp);

                if (comp > 0) {
                    ProdutoVenda adicional = copiarProdutoVenda(atualizado);
                    adicional.setQuantidade(qtdAtual.subtract(qtdOriginal));
                    comparacao.getAdicionados().add(adicional);
                    //System.out.println("➕ Adicionado: " + adicional.getProduto().getNome() + " | Qtd: " + adicional.getQuantidade());
                } else if (comp < 0) {
                    ProdutoVenda remocao = copiarProdutoVenda(original);
                    remocao.setQuantidade(qtdOriginal.subtract(qtdAtual));
                    comparacao.getRemovidos().add(remocao);
                    //System.out.println("➖ Removido: " + remocao.getProduto().getNome() + " | Qtd: " + remocao.getQuantidade());
                }
            }
        }
        return comparacao;
    }

    private ProdutoVenda copiarProdutoVenda(ProdutoVenda pv) {
        ProdutoVenda copia = new ProdutoVenda();
        copia.setProduto(pv.getProduto());
        copia.setQuantidade(pv.getQuantidade());
        copia.setObservacoesProdutoVenda(pv.getObservacoesProdutoVenda());
        copia.setData(pv.getData());
        return copia;
    }

    public ResultadoDesconto processarProdutoCompostoEstoque(
            List<ProdutoComposto> compostos,
            BigDecimal quantidadeBase,
            Matriz matriz
    ) {
        //System.out.println("▶️ Iniciando processamento de produtos compostos...");
        ResultadoDesconto resultado = new ResultadoDesconto();

        for (ProdutoComposto pc : compostos) {
            Produto subproduto = pc.getProdutoComposto();
            BigDecimal quantidadeTotal = pc.getQuantidadeGasto().multiply(quantidadeBase);

            if (subproduto.getEstocavel()) {
                //System.out.println("📦 Subproduto estocável: " + subproduto.getNome());
                resultado.addAllEstoque(descontarEstoqueEstoque(subproduto, quantidadeTotal, matriz));
            } else {
                //System.out.println("🧪 Subproduto não estocável: " + subproduto.getNome());

                if (subproduto.getProdutoMaterias() != null && !subproduto.getProdutoMaterias().isEmpty()) {
                    resultado.addAllDeposito(descontarDepositoEstoque(subproduto, matriz));
                }

                if (subproduto.getProdutoCompostos() != null && !subproduto.getProdutoCompostos().isEmpty()) {
                    ResultadoDesconto sub = processarProdutoCompostoEstoque(
                            subproduto.getProdutoCompostos(), quantidadeTotal, matriz
                    );
                    resultado.addAllEstoque(sub.getEstoques());
                    resultado.addAllDeposito(sub.getDepositos());
                }
            }
        }

        //System.out.println("✅ Processamento de compostos concluído.");
        return resultado;
    }

    public List<Deposito> descontarDepositoEstoque(Produto produto, Matriz matriz) {
        //System.out.println("▶️ Iniciando desconto de depósito...");
        List<Deposito> paraSalvar = new ArrayList<>();

        for (ProdutoMateria pm : produto.getProdutoMaterias()) {
            Materia materia = pm.getMateria();
            BigDecimal qtdNecessaria = pm.getQuantidadeGasto();

            BigDecimal totalDisponivel = depositoRepository
                    .findTotalQuantidadeDisponivelByMateriaAndMatriz(materia.getId(), matriz.getId());

            if (totalDisponivel == null || totalDisponivel.compareTo(qtdNecessaria) < 0) {
                throw new RuntimeException("❌ Depósito insuficiente para: " + materia.getNome());
            }

            List<Deposito> depositos = depositoRepository.findByMateriaAndMatrizIdAndAtivoTrueOrderByDataAsc(materia, matriz.getId());
            for (Deposito d : depositos) {
                BigDecimal disponivel = d.getQuantidade().subtract(d.getQuantidadeVendido());
                BigDecimal usado = disponivel.min(qtdNecessaria);

                d.setQuantidadeVendido(d.getQuantidadeVendido().add(usado));
                qtdNecessaria = qtdNecessaria.subtract(usado);

                if (d.getQuantidade().compareTo(d.getQuantidadeVendido()) == 0) {
                    d.setAtivo(false);
                    d.setDataDesativar(new Timestamp(System.currentTimeMillis()));
                }

                paraSalvar.add(d);
                if (qtdNecessaria.compareTo(BigDecimal.ZERO) <= 0) break;
            }
        }

        //System.out.println("✅ Desconto de depósito concluído.");
        return paraSalvar;
    }

    public List<Estoque> descontarEstoqueEstoque(Produto produto, BigDecimal qtd, Matriz matriz) {
        //System.out.println("▶️ Iniciando desconto de estoque para produto: " + produto.getNome());

        List<Estoque> paraSalvar = new ArrayList<>();
        BigDecimal qtdNecessaria = qtd;

        // Busca total disponível com query otimizada
        BigDecimal totalDisponivel = estoqueRepository
                .findTotalQuantidadeDisponivelByProdutoAndMatriz(produto.getId(), matriz.getId());

        //System.out.println("🔎 Total disponível (via query): " + totalDisponivel + " | Necessário: " + qtdNecessaria);

        if (totalDisponivel == null || totalDisponivel.compareTo(qtdNecessaria) < 0) {
            throw new RuntimeException("❌ Estoque insuficiente para o produto: " + produto.getNome());
        }

        List<Estoque> estoques = estoqueRepository
                .findByProdutoAndMatrizIdAndAtivoTrueOrderByDataAsc(produto, matriz.getId());

        for (Estoque e : estoques) {
            BigDecimal disponivel = e.getQuantidade().subtract(e.getQuantidadeVendido());
            BigDecimal usado = disponivel.min(qtdNecessaria);

            e.setQuantidadeVendido(e.getQuantidadeVendido().add(usado));
            qtdNecessaria = qtdNecessaria.subtract(usado);

            //System.out.println("   ➖ Usado: " + usado + " | Estoque ID: " + e.getId());

            if (e.getQuantidade().compareTo(e.getQuantidadeVendido()) == 0) {
                e.setAtivo(false);
                e.setDataDesativar(new Timestamp(System.currentTimeMillis()));
                //System.out.println("   ❌ Estoque desativado (ID: " + e.getId() + ")");
            }

            paraSalvar.add(e);
            if (qtdNecessaria.compareTo(BigDecimal.ZERO) <= 0) break;
        }

        //System.out.println("✅ Desconto finalizado com sucesso.");
        return paraSalvar;
    }
}