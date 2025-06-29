package chaminaTech.DTOService;

import chaminaTech.DTO.*;
import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoEntregaDTO;
import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoImpressaoDTO;
import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoRetiradaDTO;
import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoTaxaServicoDTO;
import chaminaTech.Entity.*;
import chaminaTech.Entity.Configuracao.ConfiguracaoEntrega;
import chaminaTech.Entity.Configuracao.ConfiguracaoImpressao;
import chaminaTech.Entity.Configuracao.ConfiguracaoRetirada;
import chaminaTech.Entity.Configuracao.ConfiguracaoTaxaServico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOToEntity {
    @PersistenceContext
    private EntityManager entityManager;

    public Matriz DTOToMatriz(MatrizDTO matrizDTO) {
        Matriz novoMatriz = new Matriz();

        novoMatriz.setId(matrizDTO.getId());
        novoMatriz.setAtivo(matrizDTO.getAtivo());
        novoMatriz.setDeletado(matrizDTO.getDeletado());
        novoMatriz.setNome(removerCaracteresESubirParaMaiusculo(matrizDTO.getNome()));
        novoMatriz.setCnpj(matrizDTO.getCnpj());
        novoMatriz.setUsername(matrizDTO.getUsername());
        novoMatriz.setCelular(matrizDTO.getCelular());
        novoMatriz.setEmail(matrizDTO.getEmail());
        novoMatriz.setPassword(matrizDTO.getPassword());
        novoMatriz.setRole(matrizDTO.getRole());
        novoMatriz.setEstado(matrizDTO.getEstado());
        novoMatriz.setCidade(matrizDTO.getCidade());
        novoMatriz.setBairro(matrizDTO.getBairro());
        novoMatriz.setCep(matrizDTO.getCep());
        novoMatriz.setRua(matrizDTO.getRua());
        novoMatriz.setNumero(matrizDTO.getNumero());
        novoMatriz.setLatitude(matrizDTO.getLatitude());
        novoMatriz.setLongitude(matrizDTO.getLongitude());
        novoMatriz.setLimiteFuncionarios(matrizDTO.getLimiteFuncionarios());

        if (matrizDTO.getPermissao() != null) {
            Permissao permissao = DTOToPermissao(matrizDTO.getPermissao());

            novoMatriz.setPermissao(permissao);
        }

        if (matrizDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(matrizDTO.getMatriz().getId());
            matriz.setMatriz(matriz);
        }

        return novoMatriz;
    }

    public ConfiguracaoTaxaServico DTOToConfiguracaoTaxaServico(ConfiguracaoTaxaServicoDTO configuracaoTaxaServicoDTO) {
        ConfiguracaoTaxaServico configuracaoTaxaServico = new ConfiguracaoTaxaServico();

        configuracaoTaxaServico.setId(configuracaoTaxaServicoDTO.getId());
        configuracaoTaxaServico.setAplicar(configuracaoTaxaServicoDTO.getAplicar());
        configuracaoTaxaServico.setPercentual(configuracaoTaxaServicoDTO.getPercentual());
        configuracaoTaxaServico.setValorFixo(configuracaoTaxaServicoDTO.getValorFixo());
        configuracaoTaxaServico.setTipo(configuracaoTaxaServicoDTO.getTipo());

        if (configuracaoTaxaServicoDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(configuracaoTaxaServicoDTO.getMatriz().getId());
            configuracaoTaxaServico.setMatriz(matriz);
        }
        return configuracaoTaxaServico;
    }

    public ConfiguracaoImpressao DTOToConfiguracaoImpressao(ConfiguracaoImpressaoDTO configuracaoImpressaoDTO) {
        ConfiguracaoImpressao configuracaoImpressao = new ConfiguracaoImpressao();

        configuracaoImpressao.setId(configuracaoImpressaoDTO.getId());
        configuracaoImpressao.setUsarImpressora(configuracaoImpressaoDTO.getUsarImpressora());
        configuracaoImpressao.setImprimirComprovanteRecebementoBalcao(configuracaoImpressaoDTO.getImprimirComprovanteRecebementoBalcao());
        configuracaoImpressao.setImprimirComprovanteRecebementoEntrega(configuracaoImpressaoDTO.getImprimirComprovanteRecebementoEntrega());
        configuracaoImpressao.setImprimirComprovanteRecebementoMesa(configuracaoImpressaoDTO.getImprimirComprovanteRecebementoMesa());
        configuracaoImpressao.setImprimirComprovanteRecebementoRetirada(configuracaoImpressaoDTO.getImprimirComprovanteRecebementoRetirada());
        configuracaoImpressao.setImprimirNotaFiscal(configuracaoImpressaoDTO.getImprimirNotaFiscal());
        configuracaoImpressao.setImprimirCadastrar(configuracaoImpressaoDTO.getImprimirCadastrar());
        configuracaoImpressao.setImprimirDeletar(configuracaoImpressaoDTO.getImprimirDeletar());
        configuracaoImpressao.setImprimirComprovanteDeletarVenda(configuracaoImpressaoDTO.getImprimirComprovanteDeletarVenda());
        configuracaoImpressao.setImprimirComprovanteDeletarProduto(configuracaoImpressaoDTO.getImprimirComprovanteDeletarProduto());
        configuracaoImpressao.setImprimirConferenciaEntrega(configuracaoImpressaoDTO.getImprimirConferenciaEntrega());
        configuracaoImpressao.setImprimirConferenciaRetirada(configuracaoImpressaoDTO.getImprimirConferenciaRetirada());
        configuracaoImpressao.setImprimirAberturaCaixa(configuracaoImpressaoDTO.getImprimirAberturaCaixa());
        configuracaoImpressao.setImprimirConferenciaCaixa(configuracaoImpressaoDTO.getImprimirConferenciaCaixa());
        configuracaoImpressao.setImprimirSangria(configuracaoImpressaoDTO.getImprimirSangria());
        configuracaoImpressao.setImprimirSuprimento(configuracaoImpressaoDTO.getImprimirSuprimento());
        configuracaoImpressao.setImprimirGorjeta(configuracaoImpressaoDTO.getImprimirGorjeta());
        configuracaoImpressao.setMostarMotivoDeletarVenda(configuracaoImpressaoDTO.getMostarMotivoDeletarVenda());
        configuracaoImpressao.setMostarMotivoDeletarProduto(configuracaoImpressaoDTO.getMostarMotivoDeletarProduto());
        configuracaoImpressao.setImprimirComprovanteConsumo(configuracaoImpressaoDTO.getImprimirComprovanteConsumo());

        if (configuracaoImpressaoDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(configuracaoImpressaoDTO.getMatriz().getId());
            configuracaoImpressao.setMatriz(matriz);
        }

        List<Impressora> listaImpressorasDTO = new ArrayList<>();
        if (configuracaoImpressaoDTO.getImpressoras() != null)
            for (int i = 0; i < configuracaoImpressaoDTO.getImpressoras().size(); i++) {
                listaImpressorasDTO.add(DTOToImpressora(configuracaoImpressaoDTO.getImpressoras().get(i)));
            }
        configuracaoImpressao.setImpressoras(listaImpressorasDTO);

        List<Identificador> listaIdentificadoresDTO = new ArrayList<>();
        if (configuracaoImpressaoDTO.getIdentificador() != null)
            for (int i = 0; i < configuracaoImpressaoDTO.getIdentificador().size(); i++) {
                listaIdentificadoresDTO.add(DTOToIdentificador(configuracaoImpressaoDTO.getIdentificador().get(i)));
            }
        configuracaoImpressao.setIdentificador(listaIdentificadoresDTO);

        return configuracaoImpressao;
    }

    public ConfiguracaoRetirada DTOToConfiguracaoRetirada(ConfiguracaoRetiradaDTO configuracaoRetiradaDTO) {
        ConfiguracaoRetirada configuracaoRetirada = new ConfiguracaoRetirada();

        configuracaoRetirada.setId(configuracaoRetiradaDTO.getId());
        configuracaoRetirada.setTempoEstimadoRetidara(configuracaoRetiradaDTO.getTempoEstimadoRetidara());

        if (configuracaoRetiradaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(configuracaoRetiradaDTO.getMatriz().getId());
            configuracaoRetirada.setMatriz(matriz);
        }
        return configuracaoRetirada;
    }

    public ConfiguracaoEntrega DTOToConfiguracaoEntrega(ConfiguracaoEntregaDTO configuracaoEntregaDTO) {
        ConfiguracaoEntrega configuracaoEntrega = new ConfiguracaoEntrega();

        configuracaoEntrega.setId(configuracaoEntregaDTO.getId());
        configuracaoEntrega.setCalcular(configuracaoEntregaDTO.getCalcular());

        if (configuracaoEntregaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(configuracaoEntregaDTO.getMatriz().getId());
            configuracaoEntrega.setMatriz(matriz);
        }

        List<TaxaEntregaKm> listaTaxasEntregasDTO = new ArrayList<>();
        if (configuracaoEntregaDTO.getTaxasEntregaKm() != null)
            for (int i = 0; i < configuracaoEntregaDTO.getTaxasEntregaKm().size(); i++) {
                listaTaxasEntregasDTO.add(DTOToTaxaEntregaKm(configuracaoEntregaDTO.getTaxasEntregaKm().get(i)));
            }
        configuracaoEntrega.setTaxasEntregaKm(listaTaxasEntregasDTO);

        return configuracaoEntrega;
    }

    public TaxaEntregaKm DTOToTaxaEntregaKm(TaxaEntregaKmDTO taxaEntregaKmDTO) {
        TaxaEntregaKm taxaEntregaKm = new TaxaEntregaKm();

        taxaEntregaKm.setId(taxaEntregaKmDTO.getId());
        taxaEntregaKm.setKm(taxaEntregaKmDTO.getKm());
        taxaEntregaKm.setValor(taxaEntregaKmDTO.getValor());
        taxaEntregaKm.setTempo(taxaEntregaKmDTO.getTempo());

        if (taxaEntregaKmDTO.getConfiguracaoEntrega() != null) {
            ConfiguracaoEntrega configuracaoEntrega = new ConfiguracaoEntrega();
            configuracaoEntrega.setId(taxaEntregaKmDTO.getConfiguracaoEntrega().getId());
            taxaEntregaKm.setConfiguracaoEntrega(configuracaoEntrega);
        }

        return taxaEntregaKm;
    }

    public Caixa DTOToCaixa(CaixaDTO caixaDTO) {
        Caixa novaCaixa = new Caixa();

        novaCaixa.setId(caixaDTO.getId());
        novaCaixa.setAtivo(caixaDTO.getAtivo());
        novaCaixa.setDeletado(caixaDTO.getDeletado());
        novaCaixa.setValorAbertura(caixaDTO.getValorAbertura());
        novaCaixa.setSaldoDinheiro(caixaDTO.getSaldoDinheiro());
        novaCaixa.setSaldoDebito(caixaDTO.getSaldoDebito());
        novaCaixa.setSaldoCredito(caixaDTO.getSaldoCredito());
        novaCaixa.setSaldoPix(caixaDTO.getSaldoPix());
        novaCaixa.setDataAbertura(caixaDTO.getDataAbertura());
        novaCaixa.setDataFechamento(caixaDTO.getDataFechamento());
        novaCaixa.setSaldo(caixaDTO.getSaldo());
        novaCaixa.setNomeImpressora(caixaDTO.getNomeImpressora());

        if (caixaDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(caixaDTO.getFuncionario().getId());
            novaCaixa.setFuncionario(funcionario);
        }


        if (caixaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(caixaDTO.getMatriz().getId());
            novaCaixa.setMatriz(matriz);
        }

        List<Venda> listaVendasDTO = new ArrayList<>();
        if (caixaDTO.getVendas() != null)
            for (int i = 0; i < caixaDTO.getVendas().size(); i++) {
                listaVendasDTO.add(DTOToVendaCaixa(novaCaixa, caixaDTO.getVendas().get(i)));
            }
        novaCaixa.setVendas(listaVendasDTO);

        List<Sangria> listaSangriasDTO = new ArrayList<>();
        if (caixaDTO.getSangrias() != null)
            for (int i = 0; i < caixaDTO.getSangrias().size(); i++) {
                listaSangriasDTO.add(DTOToSangria(caixaDTO.getSangrias().get(i)));
            }
        novaCaixa.setSangrias(listaSangriasDTO);

        List<Suprimento> listaSuprimentosDTO = new ArrayList<>();
        if (caixaDTO.getSuprimentos() != null)
            for (int i = 0; i < caixaDTO.getSuprimentos().size(); i++) {
                listaSuprimentosDTO.add(DTOToSuprimento(caixaDTO.getSuprimentos().get(i)));
            }
        novaCaixa.setSuprimentos(listaSuprimentosDTO);

        return novaCaixa;
    }

    public Caixa DTOToCaixaFuncionario(Funcionario novo, CaixaDTO caixaDTO) {
        Caixa novaCaixa = new Caixa();

        novaCaixa.setId(caixaDTO.getId());
        novaCaixa.setAtivo(caixaDTO.getAtivo());
        novaCaixa.setDeletado(caixaDTO.getDeletado());
        novaCaixa.setValorAbertura(caixaDTO.getValorAbertura());
        novaCaixa.setSaldoDinheiro(caixaDTO.getSaldoDinheiro());
        novaCaixa.setSaldoDebito(caixaDTO.getSaldoDebito());
        novaCaixa.setSaldoCredito(caixaDTO.getSaldoCredito());
        novaCaixa.setSaldoPix(caixaDTO.getSaldoPix());
        novaCaixa.setDataAbertura(caixaDTO.getDataAbertura());
        novaCaixa.setDataFechamento(caixaDTO.getDataFechamento());
        novaCaixa.setSaldo(caixaDTO.getSaldo());
        novaCaixa.setNomeImpressora(caixaDTO.getNomeImpressora());


        if (caixaDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(caixaDTO.getFuncionario().getId());
            novaCaixa.setFuncionario(funcionario);
        }


        if (caixaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(caixaDTO.getMatriz().getId());
            novaCaixa.setMatriz(matriz);
        }

        List<Venda> listaVendasDTO = new ArrayList<>();
        if (caixaDTO.getVendas() != null)
            for (int i = 0; i < caixaDTO.getVendas().size(); i++) {
                listaVendasDTO.add(DTOToVendaCaixa(novaCaixa, caixaDTO.getVendas().get(i)));
            }
        novaCaixa.setVendas(listaVendasDTO);

        List<Sangria> listaSangriasDTO = new ArrayList<>();
        if (caixaDTO.getSangrias() != null)
            for (int i = 0; i < caixaDTO.getSangrias().size(); i++) {
                listaSangriasDTO.add(DTOToSangria(caixaDTO.getSangrias().get(i)));
            }
        novaCaixa.setSangrias(listaSangriasDTO);

        List<Suprimento> listaSuprimentosDTO = new ArrayList<>();
        if (caixaDTO.getSuprimentos() != null)
            for (int i = 0; i < caixaDTO.getSuprimentos().size(); i++) {
                listaSuprimentosDTO.add(DTOToSuprimento(caixaDTO.getSuprimentos().get(i)));
            }
        novaCaixa.setSuprimentos(listaSuprimentosDTO);

        List<Gorjeta> listaGorjetasDTO = new ArrayList<>();
        if (caixaDTO.getGorjetas() != null)
            for (int i = 0; i < caixaDTO.getGorjetas().size(); i++) {
                listaGorjetasDTO.add(DTOToGorjeta(caixaDTO.getGorjetas().get(i)));
            }
        novaCaixa.setGorjetas(listaGorjetasDTO);

        return novaCaixa;
    }

    public Suprimento DTOToSuprimento(SuprimentoDTO suprimentoDTO) {
        Suprimento novaSuprimento = new Suprimento();

        novaSuprimento.setId(suprimentoDTO.getId());
        novaSuprimento.setAtivo(suprimentoDTO.getAtivo());
        novaSuprimento.setDataSuprimento(suprimentoDTO.getDataSuprimento());
        novaSuprimento.setMotivo(suprimentoDTO.getMotivo());
        novaSuprimento.setValor(suprimentoDTO.getValor());
        novaSuprimento.setNomeImpressora(suprimentoDTO.getNomeImpressora());

        if (suprimentoDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(suprimentoDTO.getFuncionario().getId());
            funcionario.setNome(suprimentoDTO.getFuncionario().getNome());
            novaSuprimento.setFuncionario(funcionario);
        }

        if (suprimentoDTO.getCaixa() != null) {
            Caixa caixa = new Caixa();
            caixa.setId(suprimentoDTO.getCaixa().getId());
            caixa.setValorAbertura(suprimentoDTO.getCaixa().getValorAbertura());
            if (suprimentoDTO.getCaixa().getMatriz() != null) {
                Matriz matriz = new Matriz();
                matriz.setId(suprimentoDTO.getCaixa().getMatriz().getId());
                caixa.setMatriz(matriz);
            }
            novaSuprimento.setCaixa(caixa);
        }

        return novaSuprimento;
    }

    public Gorjeta DTOToGorjeta(GorjetaDTO gorjetaDTO) {
        Gorjeta novaGorjeta = new Gorjeta();

        novaGorjeta.setId(gorjetaDTO.getId());
        novaGorjeta.setAtivo(gorjetaDTO.getAtivo());
        novaGorjeta.setDataGorjeta(gorjetaDTO.getDataGorjeta());
        novaGorjeta.setDinheiro(gorjetaDTO.getDinheiro());
        novaGorjeta.setDebito(gorjetaDTO.getDebito());
        novaGorjeta.setCredito(gorjetaDTO.getCredito());
        novaGorjeta.setPix(gorjetaDTO.getPix());
        novaGorjeta.setNomeImpressora(gorjetaDTO.getNomeImpressora());

        if (gorjetaDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(gorjetaDTO.getFuncionario().getId());
            funcionario.setNome(gorjetaDTO.getFuncionario().getNome());
            novaGorjeta.setFuncionario(funcionario);
        }

        if (gorjetaDTO.getCaixa() != null) {
            Caixa caixa = new Caixa();
            caixa.setId(gorjetaDTO.getCaixa().getId());
            caixa.setValorAbertura(gorjetaDTO.getCaixa().getValorAbertura());
            if (gorjetaDTO.getCaixa().getMatriz() != null) {
                Matriz matriz = new Matriz();
                matriz.setId(gorjetaDTO.getCaixa().getMatriz().getId());
                caixa.setMatriz(matriz);
            }
            novaGorjeta.setCaixa(caixa);
        }

        return novaGorjeta;
    }

    public Sangria DTOToSangria(SangriaDTO sangriaDTO) {
        Sangria novaSangria = new Sangria();

        novaSangria.setId(sangriaDTO.getId());
        novaSangria.setAtivo(sangriaDTO.getAtivo());
        novaSangria.setDataSangria(sangriaDTO.getDataSangria());
        novaSangria.setMotivo(sangriaDTO.getMotivo());
        novaSangria.setValor(sangriaDTO.getValor());
        novaSangria.setNomeImpressora(sangriaDTO.getNomeImpressora());
        novaSangria.setTipo(sangriaDTO.getTipo());
        novaSangria.setNomeFuncionario(sangriaDTO.getNomeFuncionario());

        if (sangriaDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(sangriaDTO.getFuncionario().getId());
            funcionario.setNome(sangriaDTO.getFuncionario().getNome());
            novaSangria.setFuncionario(funcionario);
        }
        if (sangriaDTO.getCaixa() != null) {
            Caixa caixa = new Caixa();
            caixa.setId(sangriaDTO.getCaixa().getId());
            caixa.setValorAbertura(sangriaDTO.getCaixa().getValorAbertura());
            if (sangriaDTO.getCaixa().getMatriz() != null) {
                Matriz matriz = new Matriz();
                matriz.setId(sangriaDTO.getCaixa().getMatriz().getId());
                caixa.setMatriz(matriz);
            }
            novaSangria.setCaixa(caixa);
        }

        return novaSangria;
    }

    public Venda DTOToVendaCaixa(Caixa novaCaixa, VendaDTO vendaDTO) {
        Venda novaVenda = new Venda();

        novaVenda.setId(vendaDTO.getId());
        novaVenda.setAtivo(vendaDTO.getAtivo());
        novaVenda.setRetirada(vendaDTO.getRetirada());
        novaVenda.setEntrega(vendaDTO.getEntrega());
        novaVenda.setBalcao(vendaDTO.getBalcao());
        novaVenda.setConsumoInterno(vendaDTO.getConsumoInterno());
        novaVenda.setMotivoConsumo(vendaDTO.getMotivoConsumo());
        novaVenda.setDeletado(vendaDTO.getDeletado());
        novaVenda.setChaveUnico(vendaDTO.getChaveUnico());
        novaVenda.setImprimirDeletar(vendaDTO.getImprimirDeletar());
        novaVenda.setImprimirCadastrar(vendaDTO.getImprimirCadastrar());
        novaVenda.setImprimirNotaFiscal(vendaDTO.getImprimirNotaFiscal());
        novaVenda.setNotaFiscal(vendaDTO.getNotaFiscal());
        novaVenda.setStatusEmAberto(vendaDTO.getStatusEmAberto());
        novaVenda.setStatusEmPagamento(vendaDTO.getStatusEmPagamento());
        novaVenda.setValorTotal(vendaDTO.getValorTotal());
        novaVenda.setDataVenda(vendaDTO.getDataVenda());
        novaVenda.setDataEdicao(vendaDTO.getDataEdicao());
        novaVenda.setMesa(vendaDTO.getMesa());
        novaVenda.setMotivoDeletar(vendaDTO.getMotivoDeletar());
        novaVenda.setNomeImpressora(vendaDTO.getNomeImpressora());
        novaVenda.setTaxaEntrega(vendaDTO.getTaxaEntrega());
        novaVenda.setTempoEstimado(vendaDTO.getTempoEstimado());
        novaVenda.setValorServico(vendaDTO.getValorServico());
        novaVenda.setValorBruto(vendaDTO.getValorBruto());
        novaVenda.setDesconto(vendaDTO.getDesconto());
        novaVenda.setMotivoDesconto(vendaDTO.getMotivoDesconto());


        if (vendaDTO.getMesa() != null) {
            novaVenda.setMesa(vendaDTO.getMesa());
        }

        if (vendaDTO.getCliente() != null) {
            Cliente cliente = new Cliente();
            cliente.setId(vendaDTO.getCliente().getId());
            novaVenda.setCliente(cliente);
        }

        if (vendaDTO.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setId(vendaDTO.getEndereco().getId());
            novaVenda.setEndereco(endereco);
        }

        if (vendaDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(vendaDTO.getFuncionario().getId());
            funcionario.setNome(vendaDTO.getFuncionario().getNome());
            novaVenda.setFuncionario(funcionario);
        }

        if (vendaDTO.getCaixa() != null) {
            Caixa caixa = new Caixa();
            caixa.setId(vendaDTO.getCaixa().getId());
            novaVenda.setCaixa(caixa);
        }

        if (vendaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(vendaDTO.getMatriz().getId());
            novaVenda.setMatriz(matriz);
        }

        List<ProdutoVenda> listaProdutoVendas = new ArrayList<>();
        if (vendaDTO.getProdutoVendas() != null) {
            for (int i = 0; i < vendaDTO.getProdutoVendas().size(); i++) {
                listaProdutoVendas.add(DTOToProdutoVenda(novaVenda, vendaDTO.getProdutoVendas().get(i)));
            }
        }
        novaVenda.setProdutoVendas(listaProdutoVendas);


        if (vendaDTO.getVendaPagamento() != null) {
            VendaPagamento vendaPagamento = new VendaPagamento();

            vendaPagamento.setId(vendaDTO.getVendaPagamento().getId());
            vendaPagamento.setDinheiro(vendaDTO.getVendaPagamento().getDinheiro());
            vendaPagamento.setPix(vendaDTO.getVendaPagamento().getPix());
            vendaPagamento.setDebito(vendaDTO.getVendaPagamento().getDebito());
            vendaPagamento.setCredito(vendaDTO.getVendaPagamento().getCredito());
            vendaPagamento.setConsumoInterno(vendaDTO.getVendaPagamento().getConsumoInterno());

            vendaPagamento.setDescontoDinheiro(vendaDTO.getVendaPagamento().getDescontoDinheiro());
            vendaPagamento.setDescontoCredito(vendaDTO.getVendaPagamento().getDescontoCredito());
            vendaPagamento.setDescontoDebito(vendaDTO.getVendaPagamento().getDescontoDebito());
            vendaPagamento.setDescontoPix(vendaDTO.getVendaPagamento().getDescontoPix());

            vendaPagamento.setServicoDinheiro(vendaDTO.getVendaPagamento().getServicoDinheiro());
            vendaPagamento.setServicoCredito(vendaDTO.getVendaPagamento().getServicoCredito());
            vendaPagamento.setServicoDebito(vendaDTO.getVendaPagamento().getServicoDebito());
            vendaPagamento.setServicoPix(vendaDTO.getVendaPagamento().getServicoPix());

            novaVenda.setVendaPagamento(vendaPagamento);
        }

        return novaVenda;
    }

    public Venda DTOToVenda(VendaDTO vendaDTO) {
        Venda novaVenda = new Venda();

        novaVenda.setId(vendaDTO.getId());
        novaVenda.setAtivo(vendaDTO.getAtivo());
        novaVenda.setRetirada(vendaDTO.getRetirada());
        novaVenda.setEntrega(vendaDTO.getEntrega());
        novaVenda.setBalcao(vendaDTO.getBalcao());
        novaVenda.setConsumoInterno(vendaDTO.getConsumoInterno());
        novaVenda.setMotivoConsumo(vendaDTO.getMotivoConsumo());
        novaVenda.setDeletado(vendaDTO.getDeletado());
        novaVenda.setChaveUnico(vendaDTO.getChaveUnico());
        novaVenda.setImprimirDeletar(vendaDTO.getImprimirDeletar());
        novaVenda.setImprimirCadastrar(vendaDTO.getImprimirCadastrar());
        novaVenda.setImprimirNotaFiscal(vendaDTO.getImprimirNotaFiscal());
        novaVenda.setNotaFiscal(vendaDTO.getNotaFiscal());
        novaVenda.setStatusEmAberto(vendaDTO.getStatusEmAberto());
        novaVenda.setStatusEmPagamento(vendaDTO.getStatusEmPagamento());
        novaVenda.setValorTotal(vendaDTO.getValorTotal());
        novaVenda.setDataVenda(vendaDTO.getDataVenda());
        novaVenda.setDataEdicao(vendaDTO.getDataEdicao());
        novaVenda.setMesa(vendaDTO.getMesa());
        novaVenda.setMotivoDeletar(vendaDTO.getMotivoDeletar());
        novaVenda.setNomeImpressora(vendaDTO.getNomeImpressora());
        novaVenda.setTaxaEntrega(vendaDTO.getTaxaEntrega());
        novaVenda.setTempoEstimado(vendaDTO.getTempoEstimado());
        novaVenda.setValorServico(vendaDTO.getValorServico());
        novaVenda.setValorBruto(vendaDTO.getValorBruto());
        novaVenda.setDesconto(vendaDTO.getDesconto());
        novaVenda.setMotivoDesconto(vendaDTO.getMotivoDesconto());


        if (vendaDTO.getMesa() != null) {
            novaVenda.setMesa(vendaDTO.getMesa());
        }

        if (vendaDTO.getCliente() != null) {
            Cliente cliente = new Cliente();
            cliente.setId(vendaDTO.getCliente().getId());
            cliente.setNome(vendaDTO.getCliente().getNome());
            cliente.setCelular(vendaDTO.getCliente().getCelular());
            cliente.setCpf(vendaDTO.getCliente().getCpf());
            novaVenda.setCliente(cliente);
        }

        if (vendaDTO.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setId(vendaDTO.getEndereco().getId());
            endereco.setRua(vendaDTO.getEndereco().getRua());
            endereco.setNumero(vendaDTO.getEndereco().getNumero());
            endereco.setBairro(vendaDTO.getEndereco().getBairro());
            endereco.setCidade(vendaDTO.getEndereco().getCidade());
            endereco.setEstado(vendaDTO.getEndereco().getEstado());
            endereco.setCep(vendaDTO.getEndereco().getCep());
            endereco.setReferencia(vendaDTO.getEndereco().getReferencia());
            endereco.setComplemento(vendaDTO.getEndereco().getComplemento());
            endereco.setLatitude(vendaDTO.getEndereco().getLatitude());
            endereco.setLongitude(vendaDTO.getEndereco().getLongitude());
            novaVenda.setEndereco(endereco);
        }

        if (vendaDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(vendaDTO.getFuncionario().getId());
            funcionario.setNome(vendaDTO.getFuncionario().getNome());
            novaVenda.setFuncionario(funcionario);
        }

        if (vendaDTO.getCaixa() != null) {
            Caixa caixa = new Caixa();
            caixa.setId(vendaDTO.getCaixa().getId());
            novaVenda.setCaixa(caixa);
        }

        if (vendaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(vendaDTO.getMatriz().getId());

            matriz.setConfiguracaoImpressao(
                    DTOToConfiguracaoImpressao(vendaDTO.getMatriz().getConfiguracaoImpressao())
            );

            matriz.setConfiguracaoEntrega(
                    DTOToConfiguracaoEntrega(vendaDTO.getMatriz().getConfiguracaoEntrega())
            );

            matriz.setConfiguracaoRetirada(
                    DTOToConfiguracaoRetirada(vendaDTO.getMatriz().getConfiguracaoRetirada())
            );

            matriz.setConfiguracaoTaxaServico(
                    DTOToConfiguracaoTaxaServico(vendaDTO.getMatriz().getConfiguracaoTaxaServico())
            );

            novaVenda.setMatriz(matriz);
        }

        List<ProdutoVenda> listaProdutoVendas = new ArrayList<>();
        if (vendaDTO.getProdutoVendas() != null) {
            for (int i = 0; i < vendaDTO.getProdutoVendas().size(); i++) {
                listaProdutoVendas.add(DTOToProdutoVenda(novaVenda, vendaDTO.getProdutoVendas().get(i)));
            }
        }
        novaVenda.setProdutoVendas(listaProdutoVendas);


        if (vendaDTO.getVendaPagamento() != null) {
            VendaPagamento vendaPagamento = new VendaPagamento();

            vendaPagamento.setId(vendaDTO.getVendaPagamento().getId());
            vendaPagamento.setDinheiro(vendaDTO.getVendaPagamento().getDinheiro());
            vendaPagamento.setPix(vendaDTO.getVendaPagamento().getPix());
            vendaPagamento.setDebito(vendaDTO.getVendaPagamento().getDebito());
            vendaPagamento.setCredito(vendaDTO.getVendaPagamento().getCredito());
            vendaPagamento.setConsumoInterno(vendaDTO.getVendaPagamento().getConsumoInterno());

            vendaPagamento.setDescontoDinheiro(vendaDTO.getVendaPagamento().getDescontoDinheiro());
            vendaPagamento.setDescontoCredito(vendaDTO.getVendaPagamento().getDescontoCredito());
            vendaPagamento.setDescontoDebito(vendaDTO.getVendaPagamento().getDescontoDebito());
            vendaPagamento.setDescontoPix(vendaDTO.getVendaPagamento().getDescontoPix());

            vendaPagamento.setServicoDinheiro(vendaDTO.getVendaPagamento().getServicoDinheiro());
            vendaPagamento.setServicoCredito(vendaDTO.getVendaPagamento().getServicoCredito());
            vendaPagamento.setServicoDebito(vendaDTO.getVendaPagamento().getServicoDebito());
            vendaPagamento.setServicoPix(vendaDTO.getVendaPagamento().getServicoPix());

            if (vendaPagamento.getVenda() != null) {
                Venda venda = new Venda();
                venda.setId(vendaDTO.getVendaPagamento().getId());
                novaVenda.getVendaPagamento().setVenda(venda);
            }

            novaVenda.setVendaPagamento(vendaPagamento);
        }

        return novaVenda;
    }

    public ProdutoVenda DTOToProdutoVenda(Venda novaVenda, ProdutoVendaDTO produtoVendaDTO) {
        ProdutoVenda novoProdutoVenda = new ProdutoVenda();

        novoProdutoVenda.setId(produtoVendaDTO.getId());
        novoProdutoVenda.setAtivo(produtoVendaDTO.getAtivo());
        novoProdutoVenda.setQuantidade(produtoVendaDTO.getQuantidade());
        novoProdutoVenda.setValor(produtoVendaDTO.getValor());
        novoProdutoVenda.setData(produtoVendaDTO.getData());

        novoProdutoVenda.setObservacaoProdutoVenda(produtoVendaDTO.getObservacaoProdutoVenda());
        novoProdutoVenda.setMotivoExclusao(produtoVendaDTO.getMotivoExclusao());
        novoProdutoVenda.setOrigemTransferenciaNumero(produtoVendaDTO.getOrigemTransferenciaNumero());

        novoProdutoVenda.setProduto(DTOToProduto(produtoVendaDTO.getProduto()));
        novoProdutoVenda.setVenda(novaVenda);

        if (produtoVendaDTO.getFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(produtoVendaDTO.getFuncionario().getId());
            funcionario.setNome(produtoVendaDTO.getFuncionario().getNome());
            novoProdutoVenda.setFuncionario(funcionario);
        }

        if (produtoVendaDTO.getObservacoesProdutoVenda() != null && !produtoVendaDTO.getObservacoesProdutoVenda().isEmpty()) {
            List<Observacoes> listaObservacoes = new ArrayList<>();
            for (ObservacoesDTO observacoesDTO : produtoVendaDTO.getObservacoesProdutoVenda()) {
                Observacoes observacao = entityManager.find(Observacoes.class, observacoesDTO.getId());
                if (observacao == null) {
                    observacao = new Observacoes();
                    observacao.setId(observacoesDTO.getId());
                    observacao.setObservacao(observacoesDTO.getObservacao());
                }
                listaObservacoes.add(observacao);
            }
            novoProdutoVenda.setObservacoesProdutoVenda(listaObservacoes);
        } else {
            novoProdutoVenda.setObservacoesProdutoVenda(new ArrayList<>());
        }
        return novoProdutoVenda;
    }

    public List<ProdutoVenda> DTOToProdutoVendaList(Venda novaVenda, List<ProdutoVendaDTO> produtoVendaDTOList) {
        if (produtoVendaDTOList == null || produtoVendaDTOList.isEmpty()) {
            return new ArrayList<>();
        }

        return produtoVendaDTOList.stream()
                .map(produtoVendaDTO -> DTOToProdutoVenda(novaVenda, produtoVendaDTO))
                .collect(Collectors.toList());
    }

    public Produto DTOToProduto(ProdutoDTO produtoDTO) {
        Produto novoProduto = new Produto();

        novoProduto.setId(produtoDTO.getId());
        novoProduto.setAtivo(produtoDTO.getAtivo());
        novoProduto.setDeletado(produtoDTO.getDeletado());
        novoProduto.setCardapio(produtoDTO.getCardapio());
        novoProduto.setNome(removerCaracteresESubirParaMaiusculo(produtoDTO.getNome()));
        novoProduto.setTipo(produtoDTO.getTipo());
        novoProduto.setValor(produtoDTO.getValor());
        novoProduto.setCodigo(produtoDTO.getCodigo());
        novoProduto.setValidarExestencia(produtoDTO.getValidarExestencia());
        novoProduto.setEstocavel(produtoDTO.getEstocavel());
        if (produtoDTO.getDeveImprimir() != null) {
            novoProduto.setDeveImprimir(produtoDTO.getDeveImprimir());
        }

        if (produtoDTO.getImpressoras() != null) {
            List<Impressora> impressoras = produtoDTO.getImpressoras().stream()
                    .map(this::DTOToImpressora)
                    .collect(Collectors.toList());
            novoProduto.setImpressoras(impressoras);
        } else {
            novoProduto.setImpressoras(null);
        }

        List<ProdutoMateria> listaProdutosMaterias = produtoDTO.getProdutoMaterias() != null ?
                produtoDTO.getProdutoMaterias().stream()
                        .map(materiaDTO -> DTOToProdutoMateria(novoProduto, materiaDTO))
                        .collect(Collectors.toList()) : new ArrayList<>();
        novoProduto.setProdutoMaterias(listaProdutosMaterias);

        List<ProdutoComposto> listaProdutoCompostos = produtoDTO.getProdutoCompostos() != null ?
                produtoDTO.getProdutoCompostos().stream()
                        .map(compostoDTO -> DTOToProdutoComposto(novoProduto, compostoDTO))
                        .collect(Collectors.toList()) : new ArrayList<>();
        novoProduto.setProdutoCompostos(listaProdutoCompostos);


        if (produtoDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(produtoDTO.getMatriz().getId());
            novoProduto.setMatriz(matriz);
        }

        if (produtoDTO.getCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(produtoDTO.getCategoria().getId());
            categoria.setAtivo(produtoDTO.getCategoria().getAtivo());
            novoProduto.setCategoria(categoria);
        }

        return novoProduto;
    }

    public ProdutoComposto DTOToProdutoComposto(Produto novoProduto, ProdutoCompostoDTO produtoCompostoDTO) {
        ProdutoComposto novoProdutoComposto = new ProdutoComposto();

        novoProdutoComposto.setId(produtoCompostoDTO.getId());
        novoProdutoComposto.setAtivo(produtoCompostoDTO.getAtivo());
        novoProdutoComposto.setQuantidadeGasto(produtoCompostoDTO.getQuantidadeGasto());

        if (produtoCompostoDTO.getProdutoComposto() != null) {
            Produto produtoComposto = new Produto();
            produtoComposto.setId(produtoCompostoDTO.getProdutoComposto().getId());
            produtoComposto.setNome(produtoCompostoDTO.getProdutoComposto().getNome());
            produtoComposto.setTipo(produtoCompostoDTO.getProdutoComposto().getTipo());
            produtoComposto.setValidarExestencia(produtoCompostoDTO.getProdutoComposto().getValidarExestencia());
            produtoComposto.setEstocavel(produtoCompostoDTO.getProdutoComposto().getEstocavel());

            List<ProdutoMateria> listaMaterias = produtoCompostoDTO.getProdutoComposto().getProdutoMaterias() != null ?
                    produtoCompostoDTO.getProdutoComposto().getProdutoMaterias().stream()
                            .map(materiaDTO -> DTOToProdutoMateria(produtoComposto, materiaDTO))
                            .collect(Collectors.toList()) : new ArrayList<>();
            produtoComposto.setProdutoMaterias(listaMaterias);

            List<ProdutoComposto> listaProdutoCompostos = produtoCompostoDTO.getProdutoComposto().getProdutoCompostos() != null ?
                    produtoCompostoDTO.getProdutoComposto().getProdutoCompostos().stream()
                            .map(compostoDTO -> DTOToProdutoComposto(produtoComposto, compostoDTO))
                            .collect(Collectors.toList()) : new ArrayList<>();
            produtoComposto.setProdutoCompostos(listaProdutoCompostos);

            if (produtoCompostoDTO.getProdutoComposto().getId() != null &&
                    !produtoCompostoDTO.getProdutoComposto().getId().equals(novoProduto.getId())) {
                novoProdutoComposto.setProdutoComposto(produtoComposto);
            }
        }
        novoProdutoComposto.setProduto(novoProduto);
        return novoProdutoComposto;
    }

    public ProdutoMateria DTOToProdutoMateria(Produto novoProduto, ProdutoMateriaDTO produtoMateriaDTO) {
        ProdutoMateria novoProdutoMateria = new ProdutoMateria();

        novoProdutoMateria.setId(produtoMateriaDTO.getId());
        novoProdutoMateria.setAtivo(produtoMateriaDTO.getAtivo());
        novoProdutoMateria.setQuantidadeGasto(produtoMateriaDTO.getQuantidadeGasto());

        if (produtoMateriaDTO.getProduto() != null) {
            Produto produto = new Produto();
            produto.setId(produtoMateriaDTO.getProduto().getId());
            novoProdutoMateria.setProduto(produto);
        }

        if (produtoMateriaDTO.getMateria() != null) {
            Materia materia = new Materia();
            materia.setId(produtoMateriaDTO.getMateria().getId());
            materia.setNome(produtoMateriaDTO.getMateria().getNome());
            novoProdutoMateria.setMateria(materia);
        }

        return novoProdutoMateria;
    }

    public Categoria DTOToCategoria(CategoriaDTO categoriaDTO) {
        Categoria novoCategoria = new Categoria();

        novoCategoria.setId(categoriaDTO.getId());
        novoCategoria.setAtivo(categoriaDTO.getAtivo());
        novoCategoria.setDeletado(categoriaDTO.getDeletado());
        novoCategoria.setNome(removerCaracteresESubirParaMaiusculo(categoriaDTO.getNome()));
        novoCategoria.setObsObrigatotio(categoriaDTO.getObsObrigatotio());
        novoCategoria.setMaxObs(categoriaDTO.getMaxObs());

        if (categoriaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(categoriaDTO.getMatriz().getId());
            novoCategoria.setMatriz(matriz);
        }

        List<Observacoes> listaObservasoes = new ArrayList<>();
        if (categoriaDTO.getObservacoesCategoria() != null)
            for (int i = 0; i < categoriaDTO.getObservacoesCategoria().size(); i++) {
                listaObservasoes.add(DTOToObservacoes(novoCategoria, categoriaDTO.getObservacoesCategoria().get(i)));
            }
        novoCategoria.setObservacoesCategoria(listaObservasoes);

        return novoCategoria;
    }

    public Observacoes DTOToObservacoes(Categoria novoCategoria, ObservacoesDTO observacoesDTO) {
        Observacoes novaObservacoes = new Observacoes();

        novaObservacoes.setId(observacoesDTO.getId());
        novaObservacoes.setAtivo(observacoesDTO.getAtivo());
        novaObservacoes.setObservacao(removerCaracteresESubirParaMaiusculo(observacoesDTO.getObservacao()));
        novaObservacoes.setValidarExestencia(observacoesDTO.getValidarExestencia());
        novaObservacoes.setExtra(observacoesDTO.getExtra());
        if (observacoesDTO.getValor() != null) {
            novaObservacoes.setValor(observacoesDTO.getValor());
        }

        Categoria categoria = new Categoria();
        if (observacoesDTO.getCategoria() != null) {
            categoria.setId(observacoesDTO.getCategoria().getId());
            novaObservacoes.setCategoria(categoria);
        }

        List<ObservacaoMateria> listaObservacaoMaterias = observacoesDTO.getObservacaoMaterias() != null ?
                observacoesDTO.getObservacaoMaterias().stream()
                        .map(materiaDTO -> DTOToObservacaoMateria(novaObservacoes, materiaDTO))
                        .collect(Collectors.toList()) : new ArrayList<>();
        novaObservacoes.setObservacaoMaterias(listaObservacaoMaterias);

        List<ObservacaoProduto> listaObservacaoProdutos = observacoesDTO.getObservacaoProdutos() != null ?
                observacoesDTO.getObservacaoProdutos().stream()
                        .map(compostoDTO -> DTOToObservacaoProduto(novaObservacoes, compostoDTO))
                        .collect(Collectors.toList()) : new ArrayList<>();
        novaObservacoes.setObservacaoProdutos(listaObservacaoProdutos);

        return novaObservacoes;
    }

    public ObservacaoMateria DTOToObservacaoMateria(Observacoes novaObservacoes, ObservacaoMateriaDTO observacaoMateriaDTO) {
        ObservacaoMateria novoObservacaoMateria = new ObservacaoMateria();

        novoObservacaoMateria.setId(observacaoMateriaDTO.getId());
        novoObservacaoMateria.setAtivo(observacaoMateriaDTO.getAtivo());
        novoObservacaoMateria.setQuantidadeGasto(observacaoMateriaDTO.getQuantidadeGasto());

        if (observacaoMateriaDTO.getObservacoes() != null) {
            Observacoes observacoes = new Observacoes();
            observacoes.setId(observacaoMateriaDTO.getObservacoes().getId());
            novoObservacaoMateria.setObservacoes(observacoes);
        }

        if (observacaoMateriaDTO.getMateria() != null) {
            Materia materia = new Materia();
            materia.setId(observacaoMateriaDTO.getMateria().getId());
            materia.setNome(observacaoMateriaDTO.getMateria().getNome());
            novoObservacaoMateria.setMateria(materia);
        }

        return novoObservacaoMateria;
    }

    public ObservacaoProduto DTOToObservacaoProduto(Observacoes novaObservacoes, ObservacaoProdutoDTO observacaoProdutoDTO) {
        ObservacaoProduto novoObservacaoProduto = new ObservacaoProduto();

        novoObservacaoProduto.setId(observacaoProdutoDTO.getId());
        novoObservacaoProduto.setAtivo(observacaoProdutoDTO.getAtivo());
        novoObservacaoProduto.setQuantidadeGasto(observacaoProdutoDTO.getQuantidadeGasto());

        if (observacaoProdutoDTO.getObservacoes() != null) {
            Observacoes observacoes = new Observacoes();
            observacoes.setId(observacaoProdutoDTO.getObservacoes().getId());
            novoObservacaoProduto.setObservacoes(observacoes);
        }

        if (observacaoProdutoDTO.getProduto() != null) {
            Produto produto = new Produto();
            produto.setId(observacaoProdutoDTO.getProduto().getId());
            produto.setNome(observacaoProdutoDTO.getProduto().getNome());
            novoObservacaoProduto.setProduto(produto);
        }

        return novoObservacaoProduto;
    }

    public Impressora DTOToImpressora(ImpressoraDTO impressoraDTO) {
        Impressora impressora = new Impressora();

        impressora.setId(impressoraDTO.getId());
        impressora.setNomeImpressora(impressoraDTO.getNomeImpressora());
        impressora.setApelidoImpressora(impressoraDTO.getApelidoImpressora());

        if (impressoraDTO.getConfiguracaoImpressao() != null) {
            ConfiguracaoImpressao configuracaoImpressao = new ConfiguracaoImpressao();
            configuracaoImpressao.setId(impressoraDTO.getConfiguracaoImpressao().getId());
            impressora.setConfiguracaoImpressao(configuracaoImpressao);
        }

        return impressora;
    }

    public Identificador DTOToIdentificador(IdentificadorDTO identificadorDTO) {
        Identificador identificador = new Identificador();

        identificador.setId(identificadorDTO.getId());
        identificador.setImpressoraNome(identificadorDTO.getImpressoraNome());
        identificador.setIdentificadorNome(identificadorDTO.getIdentificadorNome());

        if (identificadorDTO.getConfiguracaoImpressao() != null) {
            ConfiguracaoImpressao configuracaoImpressao = new ConfiguracaoImpressao();
            configuracaoImpressao.setId(identificadorDTO.getConfiguracaoImpressao().getId());
            identificador.setConfiguracaoImpressao(configuracaoImpressao);
        }

        return identificador;
    }

    public Funcionario DTOToFuncionario(FuncionarioDTO funcionarioDTO) {
        Funcionario novoFuncionario = new Funcionario();

        novoFuncionario.setId(funcionarioDTO.getId());
        novoFuncionario.setAtivo(funcionarioDTO.getAtivo());
        novoFuncionario.setDeletado(funcionarioDTO.getDeletado());
        novoFuncionario.setNome(removerCaracteresESubirParaMaiusculo(funcionarioDTO.getNome()));
        novoFuncionario.setSalario(funcionarioDTO.getSalario());
        novoFuncionario.setUsername(funcionarioDTO.getUsername());
        novoFuncionario.setCelular(funcionarioDTO.getCelular());
        novoFuncionario.setEmail(funcionarioDTO.getEmail());
        novoFuncionario.setPassword(funcionarioDTO.getPassword());
        novoFuncionario.setRole(funcionarioDTO.getRole());
        novoFuncionario.setPreferenciaImpressaoProdutoNovo(funcionarioDTO.getPreferenciaImpressaoProdutoNovo());
        novoFuncionario.setPreferenciaImpressaoProdutoDeletado(funcionarioDTO.getPreferenciaImpressaoProdutoDeletado());

        Matriz matriz = new Matriz();
        if (funcionarioDTO.getMatriz() != null) {
            matriz.setId(funcionarioDTO.getMatriz().getId());
            matriz.setLimiteFuncionarios(funcionarioDTO.getMatriz().getLimiteFuncionarios());
            novoFuncionario.setMatriz(matriz);
        }

        List<Caixa> listaCaixasDTO = new ArrayList<>();
        if (funcionarioDTO.getCaixas() != null)
            for (int i = 0; i < funcionarioDTO.getCaixas().size(); i++) {
                listaCaixasDTO.add(DTOToCaixaFuncionario(novoFuncionario, funcionarioDTO.getCaixas().get(i)));
            }
        novoFuncionario.setCaixas(listaCaixasDTO);

        if (funcionarioDTO.getPermissao() != null) {
            Permissao permissao = DTOToPermissao(funcionarioDTO.getPermissao()); // Utilizando o método DTOToPermissao

            novoFuncionario.setPermissao(permissao);
        }
        return novoFuncionario;
    }

    public Permissao DTOToPermissao(PermissaoDTO permissaoDTO) {
        Permissao permissao = new Permissao();

        permissao.setId(permissaoDTO.getId());
        permissao.setNome(removerCaracteresESubirParaMaiusculo(permissaoDTO.getNome()));

        permissao.setVenda(permissaoDTO.getVenda());
        permissao.setTransferirVenda(permissaoDTO.getTransferirVenda());
        permissao.setLiberarVenda(permissaoDTO.getLiberarVenda());
        permissao.setCadastrarVenda(permissaoDTO.getCadastrarVenda());
        permissao.setDeletarVenda(permissaoDTO.getDeletarVenda());
        permissao.setHistoricoVenda(permissaoDTO.getHistoricoVenda());
        permissao.setImprimir(permissaoDTO.getImprimir());
        permissao.setVendaBalcao(permissaoDTO.getVendaBalcao());
        permissao.setVendaMesa(permissaoDTO.getVendaMesa());
        permissao.setVendaEntrega(permissaoDTO.getVendaEntrega());
        permissao.setVendaRetirada(permissaoDTO.getVendaRetirada());
        permissao.setEditarProdutoVenda(permissaoDTO.getEditarProdutoVenda());
        permissao.setDeletarProdutoVenda(permissaoDTO.getDeletarProdutoVenda());

        permissao.setCaixa(permissaoDTO.getCaixa());
        permissao.setEditarCaixa(permissaoDTO.getEditarCaixa());
        permissao.setDeletarCaixa(permissaoDTO.getDeletarCaixa());
        permissao.setHistoricoCaixa(permissaoDTO.getHistoricoCaixa());


        permissao.setCadastrarSangria(permissaoDTO.getCadastrarSangria());
        permissao.setEditarSangria(permissaoDTO.getEditarSangria());
        permissao.setDeletarSangria(permissaoDTO.getDeletarSangria());

        permissao.setCadastrarSuprimento(permissaoDTO.getCadastrarSuprimento());
        permissao.setEditarSuprimento(permissaoDTO.getEditarSuprimento());
        permissao.setDeletarSuprimento(permissaoDTO.getDeletarSuprimento());

        permissao.setCadastrarGorjeta(permissaoDTO.getCadastrarGorjeta());
        permissao.setEditarGorjeta(permissaoDTO.getEditarGorjeta());
        permissao.setDeletarGorjeta(permissaoDTO.getDeletarGorjeta());

        permissao.setCategoria(permissaoDTO.getCategoria());
        permissao.setCadastrarCategoria(permissaoDTO.getCadastrarCategoria());
        permissao.setEditarCategoria(permissaoDTO.getEditarCategoria());
        permissao.setDeletarCategoria(permissaoDTO.getDeletarCategoria());

        permissao.setCliente(permissaoDTO.getCliente());
        permissao.setCadastrarCliente(permissaoDTO.getCadastrarCliente());
        permissao.setEditarCliente(permissaoDTO.getEditarCliente());
        permissao.setDeletarCliente(permissaoDTO.getDeletarCliente());

        permissao.setEstoque(permissaoDTO.getEstoque());
        permissao.setCadastrarEstoque(permissaoDTO.getCadastrarEstoque());
        permissao.setEditarEstoque(permissaoDTO.getEditarEstoque());

        permissao.setDeposito(permissaoDTO.getDeposito());
        permissao.setCadastrarDeposito(permissaoDTO.getCadastrarDeposito());
        permissao.setEditarDeposito(permissaoDTO.getEditarDeposito());

        permissao.setFuncionario(permissaoDTO.getFuncionario());
        permissao.setCadastrarFuncionario(permissaoDTO.getCadastrarFuncionario());
        permissao.setEditarFuncionario(permissaoDTO.getEditarFuncionario());
        permissao.setDeletarFuncionario(permissaoDTO.getDeletarFuncionario());

        permissao.setPermissao(permissaoDTO.getPermissao());
        permissao.setCadastrarPermissao(permissaoDTO.getCadastrarPermissao());
        permissao.setEditarPermissao(permissaoDTO.getEditarPermissao());
        permissao.setDeletarPermissao(permissaoDTO.getDeletarPermissao());

        permissao.setMateria(permissaoDTO.getMateria());
        permissao.setCadastrarMateria(permissaoDTO.getCadastrarMateria());
        permissao.setEditarMateria(permissaoDTO.getEditarMateria());
        permissao.setDeletarMateria(permissaoDTO.getDeletarMateria());

        permissao.setFilho(permissaoDTO.getFilho());
        permissao.setCadastrarFilho(permissaoDTO.getCadastrarFilho());
        permissao.setEditarFilho(permissaoDTO.getEditarFilho());
        permissao.setDeletarFilho(permissaoDTO.getDeletarFilho());

        permissao.setMatrizPermissao(permissaoDTO.getMatrizPermissao());
        permissao.setCadastrarMatriz(permissaoDTO.getCadastrarMatriz());
        permissao.setEditarMatriz(permissaoDTO.getEditarMatriz());

        permissao.setProduto(permissaoDTO.getProduto());
        permissao.setCadastrarProduto(permissaoDTO.getCadastrarProduto());
        permissao.setEditarProduto(permissaoDTO.getEditarProduto());
        permissao.setDeletarProduto(permissaoDTO.getDeletarProduto());

        permissao.setEditarConfiguracoes(permissaoDTO.getEditarConfiguracoes());
        permissao.setAuditoria(permissaoDTO.getAuditoria());

        permissao.setRelatorio(permissaoDTO.getRelatorio());
        permissao.setCadastrarRelatorio(permissaoDTO.getCadastrarRelatorio());
        permissao.setEditarRelatorio(permissaoDTO.getEditarRelatorio());
        permissao.setDeletarRelatorio(permissaoDTO.getDeletarRelatorio());

        // Conversão de Matriz (do Permissao)
        Usuario usuario = new Usuario();
        if (permissaoDTO.getUsuario() != null) {
            usuario.setId(permissaoDTO.getUsuario().getId());
            permissao.setUsuario(usuario);
        }

        return permissao;
    }

    public Estoque DTOToEstoque(EstoqueDTO estoqueDTO) {
        Estoque novoEstoque = new Estoque();

        novoEstoque.setId(estoqueDTO.getId());
        novoEstoque.setAtivo(estoqueDTO.getAtivo());
        novoEstoque.setDeletado(estoqueDTO.getDeletado());
        novoEstoque.setQuantidade(estoqueDTO.getQuantidade());
        novoEstoque.setQuantidadeVendido(estoqueDTO.getQuantidadeVendido());
        novoEstoque.setValorTotal(estoqueDTO.getValorTotal());
        novoEstoque.setDataCadastrar(estoqueDTO.getDataCadastrar());
        novoEstoque.setDataDesativar(estoqueDTO.getDataDesativar());

        novoEstoque.setProduto(DTOToProduto(estoqueDTO.getProduto()));

        Matriz matriz = new Matriz();
        if (estoqueDTO.getMatriz() != null) {
            matriz.setId(estoqueDTO.getMatriz().getId());
            novoEstoque.setMatriz(matriz);
        }

        return novoEstoque;
    }

    public EstoqueDescartar DTOToEstoqueDescartar(EstoqueDescartarDTO estoqueDescartarDTO) {
        EstoqueDescartar novoEstoqueDescartar = new EstoqueDescartar();

        novoEstoqueDescartar.setId(estoqueDescartarDTO.getId());
        novoEstoqueDescartar.setQuantidade(estoqueDescartarDTO.getQuantidade());
        novoEstoqueDescartar.setQuantidade(estoqueDescartarDTO.getQuantidade());
        novoEstoqueDescartar.setDataDescartar(estoqueDescartarDTO.getDataDescartar());
        novoEstoqueDescartar.setMotivo(estoqueDescartarDTO.getMotivo());

        novoEstoqueDescartar.setProduto(DTOToProduto(estoqueDescartarDTO.getProduto()));

        Matriz matriz = new Matriz();
        if (estoqueDescartarDTO.getMatriz() != null) {
            matriz.setId(estoqueDescartarDTO.getMatriz().getId());
            novoEstoqueDescartar.setMatriz(matriz);
        }

        return novoEstoqueDescartar;
    }

    public Deposito DTOToDeposito(DepositoDTO depositoDTO) {
        Deposito novoDeposito = new Deposito();

        novoDeposito.setId(depositoDTO.getId());
        novoDeposito.setAtivo(depositoDTO.getAtivo());
        novoDeposito.setDeletado(depositoDTO.getDeletado());
        novoDeposito.setQuantidade(depositoDTO.getQuantidade());
        novoDeposito.setQuantidadeVendido(depositoDTO.getQuantidadeVendido());
        novoDeposito.setValorTotal(depositoDTO.getValorTotal());
        novoDeposito.setDataCadastrar(depositoDTO.getDataCadastrar());
        novoDeposito.setDataDesativar(depositoDTO.getDataDesativar());

        if (depositoDTO.getMateria() != null) {
            Materia materia = new Materia();
            materia.setId(depositoDTO.getMateria().getId());
            materia.setNome(depositoDTO.getMateria().getNome());
            novoDeposito.setMateria(materia);
        }

        if (depositoDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(depositoDTO.getMatriz().getId());
            novoDeposito.setMatriz(matriz);
        }

        return novoDeposito;
    }

    public DepositoDescartar DTOToDepositoDescartar(DepositoDescartarDTO depositoDescartarDTO) {
        DepositoDescartar depositoDescartar = new DepositoDescartar();

        depositoDescartar.setId(depositoDescartarDTO.getId());
        depositoDescartar.setQuantidade(depositoDescartarDTO.getQuantidade());
        depositoDescartar.setDataDescartar(depositoDescartarDTO.getDataDescartar());
        depositoDescartar.setMotivo(depositoDescartarDTO.getMotivo());


        if (depositoDescartarDTO.getMateria() != null) {
            Materia materia = new Materia();
            materia.setId(depositoDescartarDTO.getMateria().getId());
            materia.setNome(depositoDescartarDTO.getMateria().getNome());
            depositoDescartar.setMateria(materia);
        }

        if (depositoDescartarDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(depositoDescartarDTO.getMatriz().getId());
            depositoDescartar.setMatriz(matriz);
        }

        return depositoDescartar;
    }

    public Materia DTOToMateria(MateriaDTO materiaDTO) {
        Materia materia = new Materia();

        materia.setId(materiaDTO.getId());
        materia.setAtivo(materiaDTO.getAtivo());
        materia.setDeletado(materiaDTO.getDeletado());
        materia.setNome(removerCaracteresESubirParaMaiusculo(materiaDTO.getNome()));

        if (materiaDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(materiaDTO.getMatriz().getId());
            materia.setMatriz(matriz);
        }

        return materia;
    }

    public Cliente DTOToCliente(ClienteDTO clienteDTO) {
        Cliente novaCliente = new Cliente();

        novaCliente.setId(clienteDTO.getId());
        novaCliente.setAtivo(clienteDTO.getAtivo());
        novaCliente.setNome(removerCaracteresESubirParaMaiusculo(clienteDTO.getNome()));
        novaCliente.setCpf(clienteDTO.getCpf());
        novaCliente.setCelular(clienteDTO.getCelular());

        List<Endereco> listaEnderecos = new ArrayList<>();
        if (clienteDTO.getEnderecos() != null)
            for (int i = 0; i < clienteDTO.getEnderecos().size(); i++) {
                listaEnderecos.add(DTOToEndereco(novaCliente, clienteDTO.getEnderecos().get(i)));
            }
        novaCliente.setEnderecos(listaEnderecos);

        if (clienteDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(clienteDTO.getMatriz().getId());
            novaCliente.setMatriz(matriz);
        }

        return novaCliente;
    }

    public Endereco DTOToEndereco(Cliente novoCliente, EnderecoDTO enderecoDTO) {
        Endereco novoEndereco = new Endereco();

        novoEndereco.setId(enderecoDTO.getId());
        novoEndereco.setAtivo(enderecoDTO.getAtivo());
        novoEndereco.setEstado(removerCaracteresEspeciais(enderecoDTO.getEstado()));
        novoEndereco.setCidade(removerCaracteresEspeciais(enderecoDTO.getCidade()));
        novoEndereco.setBairro(removerCaracteresEspeciais(enderecoDTO.getBairro()));
        novoEndereco.setCep(enderecoDTO.getCep());
        novoEndereco.setRua(removerCaracteresEspeciais(enderecoDTO.getRua()));
        novoEndereco.setNumero(enderecoDTO.getNumero());
        novoEndereco.setComplemento(removerCaracteresEspeciais(enderecoDTO.getComplemento()));
        novoEndereco.setReferencia(removerCaracteresEspeciais(enderecoDTO.getReferencia()));
        novoEndereco.setLatitude(enderecoDTO.getLatitude());
        novoEndereco.setLongitude(enderecoDTO.getLongitude());

        return novoEndereco;
    }

    public String removerCaracteresEspeciais(String texto) {
        if (texto == null) {
            return null;
        }
        // Remove caracteres especiais (exceto letras, números e espaços)
        texto = texto.replaceAll("[~^`´.,!?;:()\\[\\]{}<>\"'=+\\-_]", "");

        // Substitui as letras com acento por suas versões sem acento
        texto = texto.replaceAll("[áàâãäå]", "a");
        texto = texto.replaceAll("[éèêë]", "e");
        texto = texto.replaceAll("[íìîï]", "i");
        texto = texto.replaceAll("[óòôõö]", "o");
        texto = texto.replaceAll("[úùûü]", "u");
        texto = texto.replaceAll("[ç]", "c");
        texto = texto.replaceAll("[ÁÀÂÃÄÅ]", "A");
        texto = texto.replaceAll("[ÉÈÊË]", "E");
        texto = texto.replaceAll("[ÍÌÎÏ]", "I");
        texto = texto.replaceAll("[ÓÒÔÕÖ]", "O");
        texto = texto.replaceAll("[ÚÙÛÜ]", "U");
        texto = texto.replaceAll("[Ç]", "C");

        return texto;
    }

    public String removerCaracteresESubirParaMaiusculo(String texto) {
        if (texto == null) {
            return null;
        }

        // Remove caracteres especiais
        texto = texto.replaceAll("[~^`´.,!?;:()\\[\\]{}<>\"'=+\\-_]", "");

        // Substitui as letras com acento por suas versões sem acento
        texto = texto.replaceAll("[ÁÀÂÃÄÅáàâãäå]", "A");
        texto = texto.replaceAll("[ÉÈÊËéèêë]", "E");
        texto = texto.replaceAll("[ÍÌÎÏíìîï]", "I");
        texto = texto.replaceAll("[ÓÒÔÕÖóòôõö]", "O");
        texto = texto.replaceAll("[ÚÙÛÜúùûü]", "U");
        texto = texto.replaceAll("[Çç]", "C");

        // Converte o texto para maiúsculo
        texto = texto.toUpperCase();

        return texto;
    }

    public Admin DTOToAdmin(AdminDTO adminDTO) {
        Admin novoAdmin = new Admin();

        novoAdmin.setId(adminDTO.getId());
        novoAdmin.setAtivo(adminDTO.getAtivo());
        novoAdmin.setDeletado(adminDTO.getDeletado());
        novoAdmin.setNome(removerCaracteresESubirParaMaiusculo(adminDTO.getNome()));
        novoAdmin.setCnpj(adminDTO.getCnpj());
        novoAdmin.setUsername(adminDTO.getUsername());
        novoAdmin.setCelular(adminDTO.getCelular());
        novoAdmin.setEmail(adminDTO.getEmail());
        novoAdmin.setPassword(adminDTO.getPassword());
        novoAdmin.setRole(adminDTO.getRole());
        novoAdmin.setChaveApiCoordenades(adminDTO.getChaveApiCoordenades());

        if (adminDTO.getPermissao() != null) {
            Permissao permissao = DTOToPermissao(adminDTO.getPermissao());
            novoAdmin.setPermissao(permissao);
        }

        return novoAdmin;
    }

    public AdminFuncionario DTOToAdminFuncionario(AdminFuncionarioDTO adminFuncionarioDTO) {
        AdminFuncionario novoAdminFuncionario = new AdminFuncionario();

        novoAdminFuncionario.setId(adminFuncionarioDTO.getId());
        novoAdminFuncionario.setAtivo(adminFuncionarioDTO.getAtivo());
        novoAdminFuncionario.setDeletado(adminFuncionarioDTO.getDeletado());
        novoAdminFuncionario.setNome(removerCaracteresESubirParaMaiusculo(adminFuncionarioDTO.getNome()));
        novoAdminFuncionario.setUsername(adminFuncionarioDTO.getUsername());
        novoAdminFuncionario.setCelular(adminFuncionarioDTO.getCelular());
        novoAdminFuncionario.setEmail(adminFuncionarioDTO.getEmail());
        novoAdminFuncionario.setPassword(adminFuncionarioDTO.getPassword());
        novoAdminFuncionario.setRole(adminFuncionarioDTO.getRole());

        if (adminFuncionarioDTO.getAdmin() != null) {
            Admin admin = new Admin();
            admin.setId(adminFuncionarioDTO.getAdmin().getId());
            novoAdminFuncionario.setAdmin(admin);
        }

        if (adminFuncionarioDTO.getPermissao() != null) {
            Permissao permissao = DTOToPermissao(adminFuncionarioDTO.getPermissao()); // Utilizando o método DTOToPermissao

            novoAdminFuncionario.setPermissao(permissao);
        }
        return novoAdminFuncionario;
    }

    public Relatorio DTOToRelatorio(RelatorioDTO relatorioDTO) {
        Relatorio novoRelatorio = new Relatorio();

        novoRelatorio.setId(relatorioDTO.getId());
        novoRelatorio.setNome(relatorioDTO.getNome());
        if (relatorioDTO.getMatriz() != null) {
            Matriz matriz = new Matriz();
            matriz.setId(relatorioDTO.getMatriz().getId());
            novoRelatorio.setMatriz(matriz);
        }
        novoRelatorio.setTipoConsulta(relatorioDTO.getTipoConsulta());
        novoRelatorio.setDeletado(relatorioDTO.getDeletado());
        novoRelatorio.setFuncionarioId(relatorioDTO.getFuncionarioId());
        novoRelatorio.setTiposVenda(relatorioDTO.getTiposVenda());
        novoRelatorio.setDataInicio(relatorioDTO.getDataInicio());
        novoRelatorio.setDataFim(relatorioDTO.getDataFim());
        novoRelatorio.setTaxaEntrega(relatorioDTO.getTaxaEntrega());
        novoRelatorio.setTaxaServico(relatorioDTO.getTaxaServico());
        novoRelatorio.setDesconto(relatorioDTO.getDesconto());
        novoRelatorio.setFormasPagamento(relatorioDTO.getFormasPagamento());
        novoRelatorio.setOrdenacao(relatorioDTO.getOrdenacao());
        novoRelatorio.setPagina(relatorioDTO.getPagina());
        novoRelatorio.setTamanho(relatorioDTO.getTamanho());

        return novoRelatorio;
    }
}