package chaminaTech.Service;

import chaminaTech.DTO.*;
import chaminaTech.DTOService.*;
import chaminaTech.Entity.*;
import chaminaTech.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private MatrizRepository matrizRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private DepositoRepository depositoRepository;
    @Autowired
    private GestaoCaixaRepository gestaoCaixaRepository;
    @Autowired
    private ProcessarImpressaoService processarImpressaoService;
    @Autowired
    private TratarEstoqueDeposito tratarEstoqueDeposito;
    @Autowired
    private VendaSocketService vendaSocketService;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private AuditoriaService auditoriaService;

    private void notificarVenda(Venda venda, String acao) {
        VendaNotificacaoDTO notificacao = new VendaNotificacaoDTO();
        notificacao.setVenda(entityToDTO.vendaToDTO(venda));
        notificacao.setAcao(acao);

        if (venda.getMesa() != null) {
            notificacao.setTipo("mesa");
        } else if (venda.getRetirada()) {
            notificacao.setTipo("retirada");
        } else if (venda.getEntrega()) {
            notificacao.setTipo("entrega");
        } else {
            notificacao.setTipo("balcao");
        }

        vendaSocketService.notificarAtualizacao(venda.getMatriz().getId(), notificacao);
    }

    public VendaDTO buscarMesaAtivaByMatrizId(Integer mesa, Long matrizId) {
        Optional<Venda> vendaOptional = vendaRepository.buscarMesaAtivaByMatrizId(mesa, matrizId);

        return vendaOptional.map(venda -> {
            venda.setProdutoVendas(venda.getProdutoVendas().stream().filter(ProdutoVenda::getAtivo).collect(Collectors.toList()));
            return entityToDTO.vendaToDTO(venda);
        }).orElse(null);
    }

    public List<Map<String, Object>> buscarNumeroMesasByMatrizId(Long matrizId) {
        List<Object[]> resultados = vendaRepository.buscarNumeroMesasByMatrizId(matrizId);

        List<Map<String, Object>> mesas = new ArrayList<>();
        for (Object[] resultado : resultados) {
            Map<String, Object> mesaInfo = new HashMap<>();
            mesaInfo.put("numero", resultado[0]);  // Número da mesa (Integer)
            mesaInfo.put("statusEmAberto", resultado[1]);  // Se está em uso (Boolean)
            mesaInfo.put("statusEmPagamento", resultado[2]);  // Se está aguardando pagamento (Boolean)
            mesas.add(mesaInfo);
        }

        return mesas;
    }

    public BigDecimal buscarTotalVendaPorMatriz(Long matrizId, String tipoVenda) {
        return vendaRepository.buscarTotalVendaPorMatriz(matrizId, tipoVenda);
    }

    public VendaDTO findVendaById(Long id) {
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Venda não encontrado!"));
        return entityToDTO.vendaToDTO(venda);
    }

    @Transactional
    public MensagemDTO cadastrarVenda(VendaDTO vendaDTO) {
        PermissaoUtil.validarOuLancar("cadastrarVenda");
        Venda venda = dtoToEntity.DTOToVenda(vendaDTO);

        Matriz matriz = matrizRepository.findById(venda.getMatriz().getId()).orElseThrow(() -> new EntityNotFoundException("Matriz não encontrado!"));

        Timestamp agora = new Timestamp(System.currentTimeMillis());
        venda.setDataVenda(agora);
        venda.setDataEdicao(agora);

        if (venda.getVendaPagamento() != null) {
            venda.getVendaPagamento().setVenda(venda);
            calcularPercentuaisPorFormaPagamento(venda);
        }

        if (venda.getBalcao()) {
            venda.setAtivo(false);
        }

        if (venda.getCaixa() == null && venda.getBalcao()) {
            throw new IllegalStateException("❌ Caixa indefinida para venda no balcão.");
        }

        ResultadoDesconto resultadoGeral = new ResultadoDesconto(); // Armazena todos os estoques e depósitos a salvar no final

        if (venda.getProdutoVendas() != null && !venda.getProdutoVendas().isEmpty()) {
            for (ProdutoVenda pv : venda.getProdutoVendas()) {
                pv.setVenda(venda);
                pv.setData(agora);
                ResultadoDesconto parcial = tratarEstoqueDeposito.processarProdutoVenda(pv, matriz, true, null);
                resultadoGeral.addAllEstoque(parcial.getEstoques());
                resultadoGeral.addAllDeposito(parcial.getDepositos());
            }
        }

        // 💾 SALVA TODAS AS ALTERAÇÕES DEPOIS DE PROCESSAR TUDO
        if (!resultadoGeral.getDepositos().isEmpty()) {
            depositoRepository.saveAll(resultadoGeral.getDepositos());
        }

        if (!resultadoGeral.getEstoques().isEmpty()) {
            estoqueRepository.saveAll(resultadoGeral.getEstoques());
        }

        vendaRepository.save(venda);
        notificarVenda(venda, "alterar");

        // CUPOM E IMPRESSÃO
        Integer novoCupom = null;
        if ((venda.getRetirada() || venda.getEntrega() || venda.getBalcao()) && venda.getMesa() == null) {
            Optional<GestaoCaixa> ultimoCupomOpt = gestaoCaixaRepository.findTopByMatrizIdOrderByIdDesc(venda.getMatriz().getId());

            if (ultimoCupomOpt.isPresent()) {
                GestaoCaixa ultimoCupom = ultimoCupomOpt.get();

                Integer numeroAnterior = ultimoCupom.getCupom() != null ? ultimoCupom.getCupom() : 0;
                novoCupom = numeroAnterior + 1;

                if (ultimoCupom.getAtivo() == null) {
                    gestaoCaixaRepository.delete(ultimoCupom);
                }

            } else {
                novoCupom = 1;
            }
            GestaoCaixa gestaoCaixa = new GestaoCaixa();
            gestaoCaixa.setMatriz(venda.getMatriz());
            gestaoCaixa.setVenda(venda);
            gestaoCaixa.setCupom(novoCupom);
            if ((venda.getBalcao()) && !venda.getRetirada() && !venda.getEntrega()) {
                gestaoCaixa.setAtivo(false);
            }
            gestaoCaixaRepository.save(gestaoCaixa);

            if (venda.getNomeImpressora() != null) {
                if (venda.getImprimirCadastrar()) {
                    processarImpressaoService.processarImpressaoProdutos(venda, venda.getProdutoVendas(), novoCupom, false);
                }
                if ((venda.getRetirada() && matriz.getConfiguracaoImpressao().getImprimirConferenciaRetirada()) || (venda.getEntrega() && matriz.getConfiguracaoImpressao().getImprimirConferenciaEntrega())) {
                    processarImpressaoService.processarImpressaoConferencia(venda, novoCupom);
                }
            }
        }
        String descricao;
        if (Boolean.TRUE.equals(venda.getBalcao())) {
            descricao = "Cadastrou venda de balcão com cupom nº " + novoCupom;
        } else if (Boolean.TRUE.equals(venda.getEntrega())) {
            descricao = "Cadastrou entrega com cupom nº " + novoCupom;
        } else if (Boolean.TRUE.equals(venda.getRetirada())) {
            descricao = "Cadastrou retirada com cupom nº " + novoCupom;
        } else {
            descricao = "Cadastrou venda na mesa nº " + venda.getMesa();
        }
        if (Boolean.TRUE.equals(venda.getConsumoInterno())) {
            descricao += " (Consumo Interno)";
        }

        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "VENDA",
                descricao,
                PermissaoUtil.getUsuarioLogado().getNome(),
                venda.getMatriz().getId()
        );
        if (venda.getRetirada()) {
            return new MensagemDTO("Retirada salva com sucesso!", HttpStatus.CREATED);
        } else if (venda.getEntrega()) {
            return new MensagemDTO("Entrega salva com sucesso!", HttpStatus.CREATED);
        } else {
            if (venda.getConsumoInterno() && venda.getNomeImpressora() != null && matriz.getConfiguracaoImpressao().getImprimirComprovanteConsumo()) {
                processarImpressaoService.processarImpressaoComprovanteRecebimento(venda, novoCupom);
            } else if ((venda.getBalcao() && venda.getNomeImpressora() != null && !venda.getRetirada() && !venda.getEntrega()) && matriz.getConfiguracaoImpressao().getImprimirComprovanteRecebementoBalcao()) {
                processarImpressaoService.processarImpressaoComprovanteRecebimento(venda, novoCupom);
            }
            return new MensagemDTO("Venda realizada com sucesso!", HttpStatus.CREATED);
        }
    }

    @Transactional
    public MensagemDTO editarVenda(Long id, VendaDTO vendaDTO, String chaveUnicoRecebida) {
        PermissaoUtil.validarOuLancar("cadastrarVenda");

        Timestamp agora = new Timestamp(System.currentTimeMillis());
        Venda vendaOriginal = vendaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Venda não encontrada!"));

        if (!vendaDTO.getStatusEmPagamento() && !Objects.equals(vendaOriginal.getChaveUnico(), chaveUnicoRecebida)) {
            throw new IllegalStateException("A venda foi liberada por outro usuário. Suas alterações foram descartadas.");
        }

        Venda vendaAtualizada = dtoToEntity.DTOToVenda(vendaDTO);
        Matriz matriz = vendaAtualizada.getMatriz();
        if (vendaAtualizada.getDeletado()) {
            vendaAtualizada.setDeletado(false);
            vendaAtualizada.setDataEdicao(vendaAtualizada.getDataVenda());
        } else {
            vendaAtualizada.setDataEdicao(agora);
        }

        if (vendaAtualizada.getProdutoVendas() != null) {
            for (ProdutoVenda pv : vendaAtualizada.getProdutoVendas()) {
                pv.setVenda(vendaAtualizada);
                if (pv.getId() == null) {
                    pv.setData(agora);
                }
            }
        }
        if (vendaAtualizada.getVendaPagamento() != null) {
            vendaAtualizada.getVendaPagamento().setVenda(vendaAtualizada);
            calcularPercentuaisPorFormaPagamento(vendaAtualizada);
        }

        ComparacaoVendaResultado comparacao = tratarEstoqueDeposito.compararVendas(vendaOriginal, vendaAtualizada);
        tratarEstoqueDeposito.processarAlteracoesDeEstoque(comparacao, vendaAtualizada, matriz);

        Integer cupomExistente = 0;
        GestaoCaixa gestaoCaixa = null;
        // se a venda e tipo mesa e tem pagamento e tem caixa entao finalizada cria um cupom desativado e desativa a venda
        if (vendaAtualizada.getMesa() != null && vendaAtualizada.getVendaPagamento() != null) {
            if (!vendaAtualizada.getConsumoInterno()) {
                if (vendaAtualizada.getCaixa() == null) throw new IllegalStateException("Caixa indefinida");
            }
            Optional<GestaoCaixa> ultimoCupomOpt = gestaoCaixaRepository.findTopByMatrizIdOrderByIdDesc(vendaAtualizada.getMatriz().getId());

            if (ultimoCupomOpt.isPresent()) {
                GestaoCaixa ultimoCupom = ultimoCupomOpt.get();

                Integer numeroAnterior = ultimoCupom.getCupom() != null ? ultimoCupom.getCupom() : 0;
                cupomExistente = numeroAnterior + 1;

                if (ultimoCupom.getAtivo() == null) {
                    gestaoCaixaRepository.delete(ultimoCupom);
                }

            } else {
                cupomExistente = 1;
            }
            gestaoCaixa = new GestaoCaixa();
            gestaoCaixa.setMatriz(vendaAtualizada.getMatriz());
            gestaoCaixa.setVenda(vendaAtualizada);
            gestaoCaixa.setCupom(cupomExistente);
            gestaoCaixa.setAtivo(false);
            gestaoCaixaRepository.save(gestaoCaixa);
            vendaAtualizada.setAtivo(false);
        }
        // se e tipo retirada ou entrega e tem caixa entao finalizada desativa a venda e o cupom
        else if ((vendaAtualizada.getRetirada() || vendaAtualizada.getEntrega()) && vendaAtualizada.getMesa() == null && vendaAtualizada.getCaixa() != null) {
            gestaoCaixa = gestaoCaixaRepository.findByVendaId(vendaAtualizada.getId()).orElse(null);
            if (gestaoCaixa != null) {
                cupomExistente = gestaoCaixa.getCupom();
                gestaoCaixa.setAtivo(false);
                gestaoCaixaRepository.save(gestaoCaixa);
            }
            vendaAtualizada.setAtivo(false);
        }
        // se e pra impremir
        if (vendaAtualizada.getNomeImpressora() != null) {
            Map<String, List<ProdutoVenda>> alteracoes = processarImpressaoService.verificarAlteracaoProdutos(vendaOriginal, vendaAtualizada);
            List<ProdutoVenda> produtosRemovidos = alteracoes.get("removidos");
            List<ProdutoVenda> produtosCadastrados = alteracoes.get("cadastrados");

            if (!produtosCadastrados.isEmpty() && vendaAtualizada.getImprimirCadastrar()) {
                processarImpressaoService.processarImpressaoProdutos(vendaAtualizada, produtosCadastrados, cupomExistente, false);
            }

            if (!produtosRemovidos.isEmpty()) {
                if (vendaAtualizada.getImprimirDeletar()) {
                    processarImpressaoService.processarImpressaoProdutos(vendaAtualizada, produtosRemovidos, cupomExistente, true);
                }
                if (matriz.getConfiguracaoImpressao().getImprimirComprovanteDeletarProduto()) {
                    processarImpressaoService.processarImpressaoComprovanteProdutoDeletado(vendaAtualizada, produtosRemovidos, cupomExistente);
                }
            }
            if (vendaAtualizada.getConsumoInterno() != null && vendaAtualizada.getVendaPagamento() != null && matriz.getConfiguracaoImpressao().getImprimirComprovanteConsumo() ||
                    vendaAtualizada.getRetirada() && vendaAtualizada.getCaixa() != null && matriz.getConfiguracaoImpressao().getImprimirComprovanteRecebementoRetirada() ||
                    vendaAtualizada.getEntrega() && vendaAtualizada.getCaixa() != null && matriz.getConfiguracaoImpressao().getImprimirComprovanteRecebementoEntrega() ||
                    vendaAtualizada.getMesa() != null && vendaAtualizada.getVendaPagamento() != null && matriz.getConfiguracaoImpressao().getImprimirComprovanteRecebementoMesa()) {
                processarImpressaoService.processarImpressaoComprovanteRecebimento(vendaAtualizada, cupomExistente);
            }
        }
        if (vendaAtualizada.getConsumoInterno() && vendaAtualizada.getVendaPagamento() != null) {
            vendaAtualizada.setAtivo(false);
        }

        vendaRepository.save(vendaAtualizada);
        notificarVenda(vendaAtualizada, "alterar");

        int adicionados = comparacao.getAdicionados().size();
        int removidos = comparacao.getRemovidos().size();

        String tipoVenda = (vendaAtualizada.getMesa() != null) ? "mesa nº " + vendaAtualizada.getMesa()
                : (vendaAtualizada.getEntrega() ? "entrega (cupom nº " + cupomExistente + ")"
                : "retirada (cupom nº " + cupomExistente + ")");

        if (Boolean.TRUE.equals(vendaAtualizada.getConsumoInterno())) {
            tipoVenda += " (Consumo Interno)";
        }

        StringBuilder descricao = new StringBuilder();
        if (vendaAtualizada.getVendaPagamento() != null && Boolean.TRUE.equals(vendaAtualizada.getConsumoInterno())) {
            descricao.append("Finalizou a venda ").append(tipoVenda);
        } else if (vendaAtualizada.getVendaPagamento() != null && vendaAtualizada.getCaixa() != null) {
            descricao.append("Finalizou a venda ").append(tipoVenda);
        } else {
            descricao.append("Editou a venda ").append(tipoVenda);
            if (adicionados > 0) {
                descricao.append(", adicionou ").append(adicionados).append(" produto(s)");
            }
            if (removidos > 0) {
                descricao.append(", removeu ").append(removidos).append(" produto(s)");
            }
        }

        auditoriaService.salvarAuditoria(
                "EDITAR",
                "VENDA",
                descricao.toString(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                vendaAtualizada.getMatriz().getId()
        );
        return new MensagemDTO("Venda salva com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarVenda(Long id, VendaDTO vendaDTO) {
        PermissaoUtil.validarOuLancar("deletarVenda");
        Venda vendaAtualizada = dtoToEntity.DTOToVenda(vendaDTO);

        if (vendaAtualizada.getVendaPagamento() != null) {
            vendaAtualizada.getVendaPagamento().setVenda(vendaAtualizada);
        }

        List<ProdutoVenda> produtosRemovidosParaImpressao = new ArrayList<>();

        ResultadoDesconto resultadoGeral = new ResultadoDesconto();
        if (vendaAtualizada.getProdutoVendas() != null && !vendaAtualizada.getProdutoVendas().isEmpty()) {
            for (ProdutoVenda pv : vendaAtualizada.getProdutoVendas()) {
                pv.setVenda(vendaAtualizada);
                ResultadoDesconto parcial = tratarEstoqueDeposito.processarProdutoVenda(pv, vendaAtualizada.getMatriz(), false, null);
                resultadoGeral.addAllEstoque(parcial.getEstoques());
                resultadoGeral.addAllDeposito(parcial.getDepositos());

                if (vendaAtualizada.getImprimirDeletar()) {
                    produtosRemovidosParaImpressao.add(pv);
                }
            }

        }
        if (!resultadoGeral.getDepositos().isEmpty()) {
            depositoRepository.saveAll(resultadoGeral.getDepositos());
        }

        if (!resultadoGeral.getEstoques().isEmpty()) {
            estoqueRepository.saveAll(resultadoGeral.getEstoques());
        }
        Integer cupomExistente = 0;
        GestaoCaixa gestaoCaixa = gestaoCaixaRepository.findByVendaId(vendaAtualizada.getId()).orElse(null);
        if (gestaoCaixa != null) {
            // Já existe cupom pra essa venda
            cupomExistente = gestaoCaixa.getCupom();
            gestaoCaixa.setAtivo(false);
            gestaoCaixaRepository.save(gestaoCaixa);
        } else if (vendaAtualizada.getMesa() != null) {

            Optional<GestaoCaixa> ultimoCupomOpt = gestaoCaixaRepository.findTopByMatrizIdOrderByIdDesc(vendaAtualizada.getMatriz().getId());

            if (ultimoCupomOpt.isPresent()) {
                GestaoCaixa ultimoCupom = ultimoCupomOpt.get();

                Integer numeroAnterior = ultimoCupom.getCupom() != null ? ultimoCupom.getCupom() : 0;
                cupomExistente = numeroAnterior + 1;

                if (ultimoCupom.getAtivo() == null) {
                    gestaoCaixaRepository.delete(ultimoCupom);
                }

            } else {
                cupomExistente = 1;
            }
            gestaoCaixa = new GestaoCaixa();
            gestaoCaixa.setMatriz(vendaAtualizada.getMatriz());
            gestaoCaixa.setVenda(vendaAtualizada);
            gestaoCaixa.setCupom(cupomExistente);
            gestaoCaixa.setAtivo(false);
            gestaoCaixaRepository.save(gestaoCaixa);
        }
        if (vendaAtualizada.getNomeImpressora() != null) {
            if (!produtosRemovidosParaImpressao.isEmpty() && vendaAtualizada.getImprimirDeletar()) {
                processarImpressaoService.processarImpressaoProdutos(vendaAtualizada, produtosRemovidosParaImpressao, cupomExistente, true);
            }
            if (vendaAtualizada.getMatriz().getConfiguracaoImpressao().getImprimirComprovanteDeletarVenda()) {
                processarImpressaoService.processarImpressaoComprovanteDeletacaoVenda(vendaAtualizada, cupomExistente);
            }
        }

        vendaAtualizada.setAtivo(false);
        vendaAtualizada.setDeletado(true);
        vendaRepository.save(vendaAtualizada);
        notificarVenda(vendaAtualizada, "alterar");

        String descricao;

        if (vendaAtualizada.getMesa() != null) {
            descricao = "Deletou venda da mesa nº " + vendaAtualizada.getMesa();
        } else {
            descricao = "Deletou venda com cupom nº " + cupomExistente;
        }

        String tipoVenda = (vendaAtualizada.getMesa() != null)
                ? "mesa nº " + vendaAtualizada.getMesa()
                : (vendaAtualizada.getEntrega()
                ? "entrega (cupom nº " + cupomExistente + ")"
                : "retirada (cupom nº " + cupomExistente + ")");

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "VENDA",
                "Deletou a venda do tipo " + tipoVenda,
                PermissaoUtil.getUsuarioLogado().getNome(),
                vendaAtualizada.getMatriz().getId()
        );
        return new MensagemDTO("Venda deletada com sucesso!", HttpStatus.CREATED);
    }

    @Transactional
    public MensagemDTO transferirProdutos(TransferenciaDTO transferenciaDTO) {
        PermissaoUtil.validarOuLancar("transferirVenda");

        VendaDTO vendaDestinoDTO = transferenciaDTO.getVendaDestino();
        VendaDTO vendaOriginalDTO = transferenciaDTO.getVendaOriginal();
        int totalTransferido = 0;

        if (vendaDestinoDTO.getProdutoVendas() != null) {
            List<ProdutoVendaDTO> produtosDestinoAtuais = vendaDestinoDTO.getProdutoVendas();
            for (ProdutoVendaDTO produto : produtosDestinoAtuais) {
                if (produto.getId() == null) {
                    produto.setOrigemTransferenciaNumero(vendaOriginalDTO.getMesa());
                    totalTransferido++;
                }
            }
        }
        Venda vendaDestinoEntity = dtoToEntity.DTOToVenda(vendaDestinoDTO);
        Venda vendaOriginalEntity = dtoToEntity.DTOToVenda(vendaOriginalDTO);
        boolean originalSemProdutos = vendaOriginalDTO.getProdutoVendas() == null || vendaOriginalDTO.getProdutoVendas().stream().noneMatch(ProdutoVendaDTO::getAtivo);


        if (originalSemProdutos) {
            // ✅ Caso 1: Venda original sem produtos
            try {
                deletarMesaSimples(vendaOriginalEntity);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao deletar a venda original antes da transferência", e);
            }

            try {
                editarMesaSimples(vendaDestinoEntity);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao editar a venda destino após a transferência", e);
            }
        } else {
            // ✅ Caso 2: Venda original tem produtos
            try {
                editarMesaSimples(vendaOriginalEntity);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao editar a venda original antes da transferência", e);
            }

            try {
                editarMesaSimples(vendaDestinoEntity);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao editar a venda destino após a transferência", e);
            }

        }
        notificarVenda(vendaDestinoEntity, "alterar");
        auditoriaService.salvarAuditoria(
                "TRANSFERIR",
                "VENDA",
                "Transferiu " + totalTransferido + " produto(s) da mesa nº " +
                        vendaOriginalEntity.getMesa() + " para a mesa nº " + vendaDestinoEntity.getMesa(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                vendaDestinoEntity.getMatriz().getId()
        );
        return new MensagemDTO("Produtos transferidos para a mesa destino!", HttpStatus.OK);
    }

    @Transactional
    public MensagemDTO pagamentoParcial(ParcialDTO parcialDTO) {
        PermissaoUtil.validarOuLancar("cadastrarVenda");

        VendaDTO vendaOriginalDTO = parcialDTO.getVendaOriginal();
        VendaDTO vendaParcialDTO = parcialDTO.getVendaParcial();
        String chaveUnico = parcialDTO.getChaveUnico();

        editarVenda(vendaOriginalDTO.getId(), vendaOriginalDTO, chaveUnico);
        cadastrarVenda(vendaParcialDTO);

        auditoriaService.salvarAuditoria(
                "PAGAMENTO_PARCIAL",
                "VENDA",
                "Realizou pagamento parcial da mesa nº " + vendaOriginalDTO.getMesa(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                vendaOriginalDTO.getMatriz().getId()
        );

        return new MensagemDTO("Pagamento parcial realizado com sucesso!", HttpStatus.CREATED);
    }

    @Transactional
    public void editarMesaSimples(Venda venda) {
        Timestamp agora = new Timestamp(System.currentTimeMillis());
        if (venda.getProdutoVendas() != null) {
            for (ProdutoVenda pv : venda.getProdutoVendas()) {
                pv.setVenda(venda);
                if (pv.getId() == null) {
                    pv.setData(agora);
                }
            }
        }
        venda.setStatusEmAberto(false);
        venda.setDeletado(false);
        venda.setDataEdicao(agora);
        vendaRepository.save(venda);
    }

    @Transactional
    public void deletarMesaSimples(Venda venda) {
        Optional<GestaoCaixa> ultimoCupomOpt = gestaoCaixaRepository.findTopByMatrizIdOrderByIdDesc(venda.getMatriz().getId());

        Integer cupomExistente = 0;
        if (ultimoCupomOpt.isPresent()) {
            GestaoCaixa ultimoCupom = ultimoCupomOpt.get();

            Integer numeroAnterior = ultimoCupom.getCupom() != null ? ultimoCupom.getCupom() : 0;
            cupomExistente = numeroAnterior + 1;

            if (ultimoCupom.getAtivo() == null) {
                gestaoCaixaRepository.delete(ultimoCupom);
            }

        } else {
            cupomExistente = 1;
        }
        GestaoCaixa gestaoCaixa = new GestaoCaixa();
        gestaoCaixa.setMatriz(venda.getMatriz());
        gestaoCaixa.setVenda(venda);
        gestaoCaixa.setCupom(cupomExistente);
        gestaoCaixa.setAtivo(false);
        gestaoCaixaRepository.save(gestaoCaixa);
        venda.setAtivo(false);
        venda.setDeletado(true);
        vendaRepository.save(venda);
    }

    @Transactional
    public VendaDTO salvarMesaApenasExistir(VendaDTO vendaDTO) {
        Timestamp agora = new Timestamp(System.currentTimeMillis());
        Venda venda = dtoToEntity.DTOToVenda(vendaDTO);
        venda.setDataVenda(agora);
        venda.setDataEdicao(agora);
        venda.setStatusEmAberto(true);

        String chaveUnico = new Timestamp(System.currentTimeMillis()) + "-" + UUID.randomUUID();
        venda.setChaveUnico(chaveUnico);

        Venda vendaSalva = vendaRepository.save(venda);
        notificarVenda(venda, "alterar");
        return entityToDTO.vendaToDTO(vendaSalva);
    }

    @Transactional
    public MensagemDTO marcarVendaComoEmUso(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId).orElseThrow(() -> new EntityNotFoundException("Venda não encontrada!"));

        if (Boolean.TRUE.equals(venda.getStatusEmAberto())) {
            throw new IllegalStateException("Esta mesa já está sendo editada por outro usuário!");
        }

        String chaveUnico = new Timestamp(System.currentTimeMillis()) + "-" + UUID.randomUUID();
        venda.setChaveUnico(chaveUnico);

        venda.setStatusEmAberto(true);
        vendaRepository.save(venda);
        notificarVenda(venda, "alterar");
        return new MensagemDTO("Mesa marcada em uso com sucesso!", HttpStatus.CREATED);
    }

    @Transactional
    public MensagemDTO marcarVendaComoEmPagamento(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId).orElseThrow(() -> new EntityNotFoundException("Venda não encontrada!"));

        venda.setChaveUnico(null);
        venda.setStatusEmPagamento(true);
        venda.setStatusEmAberto(false);
        vendaRepository.save(venda);
        notificarVenda(venda, "alterar");
        return new MensagemDTO("Mesa marcada em pagamento com sucesso!", HttpStatus.CREATED);
    }

    @Transactional
    public MensagemDTO liberarVenda(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId).orElseThrow(() -> new EntityNotFoundException("Venda não encontrada!"));
        // Remove a chave única
        venda.setChaveUnico(null);
        venda.setStatusEmPagamento(false);
        venda.setStatusEmAberto(false);
        vendaRepository.save(venda);
        notificarVenda(venda, "alterar");
        return new MensagemDTO("Mesa liberada com sucesso!", HttpStatus.CREATED);
    }

    @Transactional
    public MensagemDTO liberarVendaPorNumero(Integer numero, Long matrizId, String tipo) {
        PermissaoUtil.validarOuLancar("liberarVenda");
        Venda venda;
        String mensagem;

        if ("mesa".equalsIgnoreCase(tipo)) {
            venda = vendaRepository.buscarMesaAtivaByMatrizId(numero, matrizId).orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada!"));
            mensagem = "Mesa liberada com sucesso!";
        } else if ("retirada".equalsIgnoreCase(tipo)) {
            venda = gestaoCaixaRepository.findByCupomAndAtivoAndRetiradaAndMatrizId(numero, matrizId).map(GestaoCaixa::getVenda).orElseThrow(() -> new EntityNotFoundException("Retirada não encontrada!"));
            mensagem = "Retirada liberada com sucesso!";
        } else if ("entrega".equalsIgnoreCase(tipo)) {
            venda = gestaoCaixaRepository.findByCupomAndAtivoAndEntregaAndMatrizId(numero, matrizId).map(GestaoCaixa::getVenda).orElseThrow(() -> new EntityNotFoundException("Entrega não encontrada!"));
            mensagem = "Entrega liberada com sucesso!";
        } else {
            throw new IllegalArgumentException("Tipo inválido! Use 'mesa', 'retirada' ou 'entrega'.");
        }

        venda.setChaveUnico(null);
        venda.setStatusEmPagamento(false);
        venda.setStatusEmAberto(false);
        vendaRepository.save(venda);
        notificarVenda(venda, "liberar");
        String descricao = tipo.equalsIgnoreCase("mesa")
                ? "Liberou a venda da mesa nº " + numero
                : "Liberou a venda do tipo " + tipo.toLowerCase() + " (cupom nº " + numero + ")";

        auditoriaService.salvarAuditoria(
                "LIBERAR",
                "VENDA",
                descricao,
                PermissaoUtil.getUsuarioLogado().getNome(),
                venda.getMatriz().getId()
        );
        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    @Transactional
    public void deletarMesa(Long id) {
        vendaRepository.deleteById(id);
    }

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void verificarVendasAtrasadasPorMatriz() {
        List<Matriz> matrizes = matrizRepository.findAllMatrizes();
        Timestamp agora = new Timestamp(System.currentTimeMillis());

        for (Matriz matriz : matrizes) {
            List<Venda> vendas = vendaRepository.buscarVendasComTempoEntregaPorMatriz(matriz.getId());

            for (Venda venda : vendas) {
                long limite = venda.getDataVenda().getTime() + (venda.getTempoEstimado() * 60L * 1000L);

                if (agora.getTime() > limite) {

                    VendaNotificacaoDTO notificacao = new VendaNotificacaoDTO();
                    notificacao.setVenda(entityToDTO.vendaToDTO(venda));
                    notificacao.setTipo(venda.getEntrega() ? "entrega" : "retirada");
                    notificacao.setAcao("atrasada");
                    vendaSocketService.notificarAtualizacao(matriz.getId(), notificacao);
                }
            }
        }
    }

    private void calcularPercentuaisPorFormaPagamento(Venda venda) {
        VendaPagamento pagamento = venda.getVendaPagamento();
        if (pagamento == null) return;

        BigDecimal valorServico = Optional.ofNullable(venda.getValorServico()).orElse(BigDecimal.ZERO);
        BigDecimal valorDesconto = Optional.ofNullable(venda.getDesconto()).orElse(BigDecimal.ZERO);

        BigDecimal dinheiro = Optional.ofNullable(pagamento.getDinheiro()).orElse(BigDecimal.ZERO);
        BigDecimal credito = Optional.ofNullable(pagamento.getCredito()).orElse(BigDecimal.ZERO);
        BigDecimal debito = Optional.ofNullable(pagamento.getDebito()).orElse(BigDecimal.ZERO);
        BigDecimal pix = Optional.ofNullable(pagamento.getPix()).orElse(BigDecimal.ZERO);

        BigDecimal totalPago = dinheiro.add(credito).add(debito).add(pix);
        if (totalPago.compareTo(BigDecimal.ZERO) == 0) return;

        // Distribuir proporcionalmente com base no total pago
        if (dinheiro.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = dinheiro.divide(totalPago, 6, RoundingMode.HALF_UP);
            pagamento.setServicoDinheiro(valorServico.multiply(pct));
            pagamento.setDescontoDinheiro(valorDesconto.multiply(pct));
        }

        if (credito.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = credito.divide(totalPago, 6, RoundingMode.HALF_UP);
            pagamento.setServicoCredito(valorServico.multiply(pct));
            pagamento.setDescontoCredito(valorDesconto.multiply(pct));
        }

        if (debito.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = debito.divide(totalPago, 6, RoundingMode.HALF_UP);
            pagamento.setServicoDebito(valorServico.multiply(pct));
            pagamento.setDescontoDebito(valorDesconto.multiply(pct));
        }

        if (pix.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = pix.divide(totalPago, 6, RoundingMode.HALF_UP);
            pagamento.setServicoPix(valorServico.multiply(pct));
            pagamento.setDescontoPix(valorDesconto.multiply(pct));
        }
    }

}