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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityToDTO {
    public UsuarioDTO usuarioToDTO(Usuario usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();

        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setAtivo(usuario.getAtivo());
        usuarioDTO.setDeletado(usuario.getDeletado());
        usuarioDTO.setNome(usuario.getNome());
        usuarioDTO.setCnpj(usuario.getCnpj());
        usuarioDTO.setUsername(usuario.getUsername());
        usuarioDTO.setCelular(usuario.getCelular());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setRole(usuario.getRole());

        if (usuario.getPermissao() != null) {
            PermissaoDTO permissaoDTO = permissaoToDTO(usuario.getPermissao());

            usuarioDTO.setPermissao(permissaoDTO);
        }

        return usuarioDTO;
    }

    public MatrizDTO matrizToDTO(Matriz matriz) {
        MatrizDTO matrizDTO = new MatrizDTO();

        matrizDTO.setId(matriz.getId());
        matrizDTO.setAtivo(matriz.getAtivo());
        matrizDTO.setDeletado(matriz.getDeletado());
        matrizDTO.setNome(matriz.getNome());
        matrizDTO.setCnpj(matriz.getCnpj());
        matrizDTO.setUsername(matriz.getUsername());
        matrizDTO.setCelular(matriz.getCelular());
        matrizDTO.setEmail(matriz.getEmail());
        matrizDTO.setRole(matriz.getRole());
        matrizDTO.setEstado(matriz.getEstado());
        matrizDTO.setCidade(matriz.getCidade());
        matrizDTO.setBairro(matriz.getBairro());
        matrizDTO.setCep(matriz.getCep());
        matrizDTO.setRua(matriz.getRua());
        matrizDTO.setNumero(matriz.getNumero());
        matrizDTO.setLatitude(matriz.getLatitude());
        matrizDTO.setLongitude(matriz.getLongitude());
        matrizDTO.setLimiteFuncionarios(matriz.getLimiteFuncionarios());

        if (matriz.getConfiguracaoEntrega() != null) {
            ConfiguracaoEntregaDTO configuracaoEntregaDTO = configuracaoEntregaToDTO(matriz.getConfiguracaoEntrega());
            matrizDTO.setConfiguracaoEntrega(configuracaoEntregaDTO);
        }

        if (matriz.getConfiguracaoRetirada() != null) {
            ConfiguracaoRetiradaDTO configuracaoRetiradaDTO = configuracaoRetiradaToDTO(matriz.getConfiguracaoRetirada());
            matrizDTO.setConfiguracaoRetirada(configuracaoRetiradaDTO);
        }

        if (matriz.getConfiguracaoImpressao() != null) {
            ConfiguracaoImpressaoDTO configuracaoImpressaoDTO = configuracaoImpressaoToDTO(matriz.getConfiguracaoImpressao());
            matrizDTO.setConfiguracaoImpressao(configuracaoImpressaoDTO);
        }

        if (matriz.getConfiguracaoTaxaServico() != null) {
            ConfiguracaoTaxaServicoDTO configuracaoTaxaServicoDTO = configuracaoTaxaServicioToDTO(matriz.getConfiguracaoTaxaServico());
            matrizDTO.setConfiguracaoTaxaServicio(configuracaoTaxaServicoDTO);
        }

        if (matriz.getMatriz() != null) {
            MatrizDTO filhoDTO = new MatrizDTO();
            filhoDTO.setId(matriz.getMatriz().getId());
            matrizDTO.setMatriz(filhoDTO.getMatriz());
        }

        if (matriz.getPermissao() != null) {
            PermissaoDTO permissaoDTO = permissaoToDTO(matriz.getPermissao());
            matrizDTO.setPermissao(permissaoDTO);
        }

        return matrizDTO;
    }

    public ConfiguracaoTaxaServicoDTO configuracaoTaxaServicioToDTO(ConfiguracaoTaxaServico configuracaoTaxaServico) {
        ConfiguracaoTaxaServicoDTO configuracaoTaxaServicoDTO = new ConfiguracaoTaxaServicoDTO();

        configuracaoTaxaServicoDTO.setId(configuracaoTaxaServico.getId());
        configuracaoTaxaServicoDTO.setAplicar(configuracaoTaxaServico.getAplicar());
        configuracaoTaxaServicoDTO.setPercentual(configuracaoTaxaServico.getPercentual());
        configuracaoTaxaServicoDTO.setValorFixo(configuracaoTaxaServico.getValorFixo());
        configuracaoTaxaServicoDTO.setTipo(configuracaoTaxaServico.getTipo());

        if (configuracaoTaxaServico.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(configuracaoTaxaServico.getMatriz().getId());
            configuracaoTaxaServicoDTO.setMatriz(matrizDTO);
        }
        return configuracaoTaxaServicoDTO;
    }

    public ConfiguracaoImpressaoDTO configuracaoImpressaoToDTO(ConfiguracaoImpressao configuracaoImpressao) {
        ConfiguracaoImpressaoDTO configuracaoImpressaoDTO = new ConfiguracaoImpressaoDTO();

        configuracaoImpressaoDTO.setId(configuracaoImpressao.getId());
        configuracaoImpressaoDTO.setUsarImpressora(configuracaoImpressao.getUsarImpressora());
        configuracaoImpressaoDTO.setImprimirComprovanteRecebementoBalcao(configuracaoImpressao.getImprimirComprovanteRecebementoBalcao());
        configuracaoImpressaoDTO.setImprimirComprovanteRecebementoEntrega(configuracaoImpressao.getImprimirComprovanteRecebementoEntrega());
        configuracaoImpressaoDTO.setImprimirComprovanteRecebementoMesa(configuracaoImpressao.getImprimirComprovanteRecebementoMesa());
        configuracaoImpressaoDTO.setImprimirComprovanteRecebementoRetirada(configuracaoImpressao.getImprimirComprovanteRecebementoRetirada());
        configuracaoImpressaoDTO.setImprimirNotaFiscal(configuracaoImpressao.getImprimirNotaFiscal());
        configuracaoImpressaoDTO.setImprimirCadastrar(configuracaoImpressao.getImprimirCadastrar());
        configuracaoImpressaoDTO.setImprimirDeletar(configuracaoImpressao.getImprimirDeletar());
        configuracaoImpressaoDTO.setImprimirComprovanteDeletarVenda(configuracaoImpressao.getImprimirComprovanteDeletarVenda());
        configuracaoImpressaoDTO.setImprimirComprovanteDeletarProduto(configuracaoImpressao.getImprimirComprovanteDeletarProduto());
        configuracaoImpressaoDTO.setImprimirConferenciaEntrega(configuracaoImpressao.getImprimirConferenciaEntrega());
        configuracaoImpressaoDTO.setImprimirConferenciaRetirada(configuracaoImpressao.getImprimirConferenciaRetirada());
        configuracaoImpressaoDTO.setImprimirAberturaCaixa(configuracaoImpressao.getImprimirAberturaCaixa());
        configuracaoImpressaoDTO.setImprimirConferenciaCaixa(configuracaoImpressao.getImprimirConferenciaCaixa());
        configuracaoImpressaoDTO.setImprimirSangria(configuracaoImpressao.getImprimirSangria());
        configuracaoImpressaoDTO.setImprimirSuprimento(configuracaoImpressao.getImprimirSuprimento());
        configuracaoImpressaoDTO.setImprimirGorjeta(configuracaoImpressao.getImprimirGorjeta());
        configuracaoImpressaoDTO.setMostarMotivoDeletarVenda(configuracaoImpressao.getMostarMotivoDeletarVenda());
        configuracaoImpressaoDTO.setMostarMotivoDeletarProduto(configuracaoImpressao.getMostarMotivoDeletarProduto());

        if (configuracaoImpressao.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(configuracaoImpressao.getMatriz().getId());
            configuracaoImpressaoDTO.setMatriz(matrizDTO);
        }

        List<ImpressoraDTO> listaImpressoras = new ArrayList<>();
        if (configuracaoImpressao.getImpressoras() != null)
            for (int i = 0; i < configuracaoImpressao.getImpressoras().size(); i++) {
                listaImpressoras.add(impressoraToDTO(configuracaoImpressao.getImpressoras().get(i)));
            }
        configuracaoImpressaoDTO.setImpressoras(listaImpressoras);

        List<IdentificadorDTO> listaIdentificadores = new ArrayList<>();
        if (configuracaoImpressao.getIdentificador() != null)
            for (int i = 0; i < configuracaoImpressao.getIdentificador().size(); i++) {
                listaIdentificadores.add(identificadorToDTO(configuracaoImpressao.getIdentificador().get(i)));
            }
        configuracaoImpressaoDTO.setIdentificador(listaIdentificadores);

        return configuracaoImpressaoDTO;
    }

    public ConfiguracaoRetiradaDTO configuracaoRetiradaToDTO(ConfiguracaoRetirada configuracaoRetirada) {
        ConfiguracaoRetiradaDTO configuracaoRetiradaDTO = new ConfiguracaoRetiradaDTO();

        configuracaoRetiradaDTO.setId(configuracaoRetirada.getId());
        configuracaoRetiradaDTO.setTempoEstimadoRetidara(configuracaoRetirada.getTempoEstimadoRetidara());

        if (configuracaoRetirada.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(configuracaoRetirada.getMatriz().getId());
            configuracaoRetiradaDTO.setMatriz(matrizDTO);
        }
        return configuracaoRetiradaDTO;
    }

    public ConfiguracaoEntregaDTO configuracaoEntregaToDTO(ConfiguracaoEntrega configuracaoEntrega) {
        ConfiguracaoEntregaDTO configuracaoEntregaDTO = new ConfiguracaoEntregaDTO();

        configuracaoEntregaDTO.setId(configuracaoEntrega.getId());
        configuracaoEntregaDTO.setCalcular(configuracaoEntrega.getCalcular());

        if (configuracaoEntrega.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(configuracaoEntrega.getMatriz().getId());
            configuracaoEntregaDTO.setMatriz(matrizDTO);
        }

        List<TaxaEntregaKmDTO> listaTaxasEntregas = new ArrayList<>();
        if (configuracaoEntrega.getTaxasEntregaKm() != null)
            for (int i = 0; i < configuracaoEntrega.getTaxasEntregaKm().size(); i++) {
                listaTaxasEntregas.add(taxaEntregaKmToDTO(configuracaoEntrega.getTaxasEntregaKm().get(i)));
            }
        configuracaoEntregaDTO.setTaxasEntregaKm(listaTaxasEntregas);

        return configuracaoEntregaDTO;
    }

    public TaxaEntregaKmDTO taxaEntregaKmToDTO(TaxaEntregaKm taxaEntregaKm) {
        TaxaEntregaKmDTO taxaEntregaKmDTO = new TaxaEntregaKmDTO();

        taxaEntregaKmDTO.setId(taxaEntregaKm.getId());
        taxaEntregaKmDTO.setKm(taxaEntregaKm.getKm());
        taxaEntregaKmDTO.setValor(taxaEntregaKm.getValor());
        taxaEntregaKmDTO.setTempo(taxaEntregaKm.getTempo());

        if (taxaEntregaKm.getConfiguracaoEntrega() != null) {
            ConfiguracaoEntregaDTO configuracaoEntregaDTO = new ConfiguracaoEntregaDTO();
            configuracaoEntregaDTO.setId(taxaEntregaKm.getConfiguracaoEntrega().getId());
            taxaEntregaKmDTO.setConfiguracaoEntrega(configuracaoEntregaDTO);
        }

        return taxaEntregaKmDTO;
    }

    public GestaoCaixaDTO gestaoCaixaToDTO(GestaoCaixa gestaoCaixa) {
        GestaoCaixaDTO gestaoCaixaDTO = new GestaoCaixaDTO();

        gestaoCaixaDTO.setId(gestaoCaixa.getId());
        gestaoCaixaDTO.setAtivo(gestaoCaixa.getAtivo());
        gestaoCaixaDTO.setCupom(gestaoCaixa.getCupom());

        if (gestaoCaixa.getVenda() != null) {
            gestaoCaixaDTO.setVenda(vendaToDTO(gestaoCaixa.getVenda()));
        }

        if (gestaoCaixa.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(gestaoCaixa.getMatriz().getId());
            gestaoCaixaDTO.setMatriz(matrizDTO);
        }

        return gestaoCaixaDTO;
    }

    public ImpressoraDTO impressoraToDTO(Impressora impressora) {
        ImpressoraDTO impressoraDTO = new ImpressoraDTO();

        impressoraDTO.setId(impressora.getId());
        impressoraDTO.setApelidoImpressora(impressora.getApelidoImpressora());
        impressoraDTO.setNomeImpressora(impressora.getNomeImpressora());

        if (impressora.getConfiguracaoImpressao() != null) {
            ConfiguracaoImpressaoDTO configuracaoImpressaoDTO = new ConfiguracaoImpressaoDTO();
            configuracaoImpressaoDTO.setId(impressora.getConfiguracaoImpressao().getId());
            impressoraDTO.setConfiguracaoImpressao(configuracaoImpressaoDTO);
        }

        return impressoraDTO;
    }

    public IdentificadorDTO identificadorToDTO(Identificador identificador) {
        IdentificadorDTO identificadorDTO = new IdentificadorDTO();

        identificadorDTO.setId(identificador.getId());
        identificadorDTO.setImpressoraNome(identificador.getImpressoraNome());
        identificadorDTO.setIdentificadorNome(identificador.getIdentificadorNome());

        if (identificador.getConfiguracaoImpressao() != null) {
            ConfiguracaoImpressaoDTO configuracaoImpressaoDTO = new ConfiguracaoImpressaoDTO();
            configuracaoImpressaoDTO.setId(identificador.getConfiguracaoImpressao().getId());
            identificadorDTO.setConfiguracaoImpressao(configuracaoImpressaoDTO);
        }

        return identificadorDTO;
    }

    public FuncionarioDTO funcionarioToDTO(Funcionario funcionario) {
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();

        funcionarioDTO.setId(funcionario.getId());
        funcionarioDTO.setAtivo(funcionario.getAtivo());
        funcionarioDTO.setDeletado(funcionario.getDeletado());
        funcionarioDTO.setNome(funcionario.getNome());
        funcionarioDTO.setSalario(funcionario.getSalario());
        funcionarioDTO.setUsername(funcionario.getUsername());
        funcionarioDTO.setCelular(funcionario.getCelular());
        funcionarioDTO.setEmail(funcionario.getEmail());
        funcionarioDTO.setRole(funcionario.getRole());
        funcionarioDTO.setPreferenciaImpressaoProdutoNovo(funcionario.getPreferenciaImpressaoProdutoNovo());
        funcionarioDTO.setPreferenciaImpressaoProdutoDeletado(funcionario.getPreferenciaImpressaoProdutoDeletado());

        MatrizDTO matrizDTO = new MatrizDTO();
        if (funcionario.getMatriz() != null) {
            matrizDTO.setId(funcionario.getMatriz().getId());
            funcionarioDTO.setMatriz(matrizDTO);
        }

        List<CaixaDTO> listaCaixas = new ArrayList<>();
        if (funcionario.getCaixas() != null)
            for (int i = 0; i < funcionario.getCaixas().size(); i++) {
                listaCaixas.add(caixaToDTO(funcionario.getCaixas().get(i)));
            }
        funcionarioDTO.setCaixas(listaCaixas);

        if (funcionario.getPermissao() != null) {
            PermissaoDTO permissaoDTO = permissaoToDTO(funcionario.getPermissao());

            funcionarioDTO.setPermissao(permissaoDTO);
        }

        return funcionarioDTO;
    }

    public PermissaoDTO permissaoToDTO(Permissao permissao) {
        PermissaoDTO permissaoDTO = new PermissaoDTO();

        permissaoDTO.setId(permissao.getId());
        permissaoDTO.setNome(permissao.getNome());

        permissaoDTO.setVenda(permissao.getVenda());
        permissaoDTO.setTransferirVenda(permissao.getTransferirVenda());
        permissaoDTO.setLiberarVenda(permissao.getLiberarVenda());
        permissaoDTO.setCadastrarVenda(permissao.getCadastrarVenda());
        permissaoDTO.setDeletarVenda(permissao.getDeletarVenda());
        permissaoDTO.setHistoricoVenda(permissao.getHistoricoVenda());
        permissaoDTO.setImprimir(permissao.getImprimir());
        permissaoDTO.setVendaBalcao(permissao.getVendaBalcao());
        permissaoDTO.setVendaMesa(permissao.getVendaMesa());
        permissaoDTO.setVendaEntrega(permissao.getVendaEntrega());
        permissaoDTO.setVendaRetirada(permissao.getVendaRetirada());
        permissaoDTO.setEditarProdutoVenda(permissao.getEditarProdutoVenda());
        permissaoDTO.setDeletarProdutoVenda(permissao.getDeletarProdutoVenda());

        permissaoDTO.setCaixa(permissao.getCaixa());
        permissaoDTO.setEditarCaixa(permissao.getEditarCaixa());
        permissaoDTO.setDeletarCaixa(permissao.getDeletarCaixa());
        permissaoDTO.setHistoricoCaixa(permissao.getHistoricoCaixa());

        permissaoDTO.setCadastrarSangria(permissao.getCadastrarSangria());
        permissaoDTO.setEditarSangria(permissao.getEditarSangria());
        permissaoDTO.setDeletarSangria(permissao.getDeletarSangria());

        permissaoDTO.setCadastrarSuprimento(permissao.getCadastrarSuprimento());
        permissaoDTO.setEditarSuprimento(permissao.getEditarSuprimento());
        permissaoDTO.setDeletarSuprimento(permissao.getDeletarSuprimento());

        permissaoDTO.setCadastrarGorjeta(permissao.getCadastrarGorjeta());
        permissaoDTO.setEditarGorjeta(permissao.getEditarGorjeta());
        permissaoDTO.setDeletarGorjeta(permissao.getDeletarGorjeta());

        permissaoDTO.setCategoria(permissao.getCategoria());
        permissaoDTO.setCadastrarCategoria(permissao.getCadastrarCategoria());
        permissaoDTO.setEditarCategoria(permissao.getEditarCategoria());
        permissaoDTO.setDeletarCategoria(permissao.getDeletarCategoria());

        permissaoDTO.setCliente(permissao.getCliente());
        permissaoDTO.setCadastrarCliente(permissao.getCadastrarCliente());
        permissaoDTO.setEditarCliente(permissao.getEditarCliente());
        permissaoDTO.setDeletarCliente(permissao.getDeletarCliente());

        permissaoDTO.setEstoque(permissao.getEstoque());
        permissaoDTO.setCadastrarEstoque(permissao.getCadastrarEstoque());
        permissaoDTO.setEditarEstoque(permissao.getEditarEstoque());

        permissaoDTO.setDeposito(permissao.getDeposito());
        permissaoDTO.setCadastrarDeposito(permissao.getCadastrarDeposito());
        permissaoDTO.setEditarDeposito(permissao.getEditarDeposito());

        permissaoDTO.setFuncionario(permissao.getFuncionario());
        permissaoDTO.setCadastrarFuncionario(permissao.getCadastrarFuncionario());
        permissaoDTO.setEditarFuncionario(permissao.getEditarFuncionario());
        permissaoDTO.setDeletarFuncionario(permissao.getDeletarFuncionario());

        permissaoDTO.setPermissao(permissao.getPermissao());
        permissaoDTO.setCadastrarPermissao(permissao.getCadastrarPermissao());
        permissaoDTO.setEditarPermissao(permissao.getEditarPermissao());
        permissaoDTO.setDeletarPermissao(permissao.getDeletarPermissao());

        permissaoDTO.setMateria(permissao.getMateria());
        permissaoDTO.setCadastrarMateria(permissao.getCadastrarMateria());
        permissaoDTO.setEditarMateria(permissao.getEditarMateria());
        permissaoDTO.setDeletarMateria(permissao.getDeletarMateria());

        permissaoDTO.setFilho(permissao.getFilho());
        permissaoDTO.setCadastrarFilho(permissao.getCadastrarFilho());
        permissaoDTO.setEditarFilho(permissao.getEditarFilho());
        permissaoDTO.setDeletarFilho(permissao.getDeletarFilho());

        permissaoDTO.setMatrizPermissao(permissao.getMatrizPermissao());
        permissaoDTO.setCadastrarMatriz(permissao.getCadastrarMatriz());
        permissaoDTO.setEditarMatriz(permissao.getEditarMatriz());

        permissaoDTO.setProduto(permissao.getProduto());
        permissaoDTO.setCadastrarProduto(permissao.getCadastrarProduto());
        permissaoDTO.setEditarProduto(permissao.getEditarProduto());
        permissaoDTO.setDeletarProduto(permissao.getDeletarProduto());

        permissaoDTO.setEditarConfiguracoes(permissao.getEditarConfiguracoes());
        permissaoDTO.setAuditoria(permissao.getAuditoria());

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        if (permissao.getUsuario() != null) {
            usuarioDTO.setId(permissao.getUsuario().getId());
            permissaoDTO.setUsuario(usuarioDTO);
        }

        return permissaoDTO;
    }

    public DepositoDTO depositoToDTO(Deposito deposito) {
        DepositoDTO depositoDTO = new DepositoDTO();

        depositoDTO.setId(deposito.getId());
        depositoDTO.setAtivo(deposito.getAtivo());
        depositoDTO.setDeletado(deposito.getDeletado());
        depositoDTO.setQuantidade(deposito.getQuantidade());
        depositoDTO.setQuantidadeVendido(deposito.getQuantidadeVendido());
        depositoDTO.setValorTotal(deposito.getValorTotal());
        depositoDTO.setDataCadastrar(deposito.getDataCadastrar());
        depositoDTO.setDataDesativar(deposito.getDataDesativar());

        MateriaDTO materiaDTO = new MateriaDTO();
        if (deposito.getMateria() != null) {
            materiaDTO.setId(deposito.getMateria().getId());
            materiaDTO.setNome(deposito.getMateria().getNome());
            depositoDTO.setMateria(materiaDTO);
        }

        MatrizDTO matrizDTO = new MatrizDTO();
        if (deposito.getMatriz() != null) {
            matrizDTO.setId(deposito.getMatriz().getId());
            depositoDTO.setMatriz(matrizDTO);
        }

        return depositoDTO;
    }

    public DepositoDescartarDTO depositoDescartarToDTO(DepositoDescartar depositoDescartar) {
        DepositoDescartarDTO depositoDescartarDTO = new DepositoDescartarDTO();

        depositoDescartarDTO.setId(depositoDescartar.getId());
        depositoDescartarDTO.setQuantidade(depositoDescartar.getQuantidade());
        depositoDescartarDTO.setDataDescartar(depositoDescartar.getDataDescartar());
        depositoDescartarDTO.setMotivo(depositoDescartar.getMotivo());

        MateriaDTO materiaDTO = new MateriaDTO();
        if (depositoDescartar.getMateria() != null) {
            materiaDTO.setId(depositoDescartar.getMateria().getId());
            materiaDTO.setNome(depositoDescartar.getMateria().getNome());
            depositoDescartarDTO.setMateria(materiaDTO);
        }

        MatrizDTO matrizDTO = new MatrizDTO();
        if (depositoDescartar.getMatriz() != null) {
            matrizDTO.setId(depositoDescartar.getMatriz().getId());
            depositoDescartarDTO.setMatriz(matrizDTO);
        }

        return depositoDescartarDTO;
    }

    public EstoqueDTO estoqueToDTO(Estoque estoque) {
        EstoqueDTO estoqueDTO = new EstoqueDTO();

        estoqueDTO.setId(estoque.getId());
        estoqueDTO.setAtivo(estoque.getAtivo());
        estoqueDTO.setDeletado(estoque.getDeletado());
        estoqueDTO.setQuantidade(estoque.getQuantidade());
        estoqueDTO.setQuantidadeVendido(estoque.getQuantidadeVendido());
        estoqueDTO.setValorTotal(estoque.getValorTotal());
        estoqueDTO.setDataCadastrar(estoque.getDataCadastrar());
        estoqueDTO.setDataDesativar(estoque.getDataDesativar());

        ProdutoDTO produtoDTO = new ProdutoDTO();
        if (estoque.getProduto() != null) {
            produtoDTO.setId(estoque.getProduto().getId());
            produtoDTO.setNome(estoque.getProduto().getNome());
            produtoDTO.setValor(estoque.getProduto().getValor());
            estoqueDTO.setProduto(produtoDTO);
        }

        MatrizDTO matrizDTO = new MatrizDTO();
        if (estoque.getMatriz() != null) {
            matrizDTO.setId(estoque.getMatriz().getId());
            estoqueDTO.setMatriz(matrizDTO);
        }

        return estoqueDTO;
    }

    public EstoqueDescartarDTO estoqueDescartarToDTO(EstoqueDescartar estoqueDescartar) {
        EstoqueDescartarDTO estoqueDescartarDTO = new EstoqueDescartarDTO();

        estoqueDescartarDTO.setId(estoqueDescartar.getId());
        estoqueDescartarDTO.setQuantidade(estoqueDescartar.getQuantidade());
        estoqueDescartarDTO.setQuantidade(estoqueDescartar.getQuantidade());
        estoqueDescartarDTO.setDataDescartar(estoqueDescartar.getDataDescartar());
        estoqueDescartarDTO.setMotivo(estoqueDescartar.getMotivo());

        ProdutoDTO produtoDTO = new ProdutoDTO();
        if (estoqueDescartar.getProduto() != null) {
            produtoDTO.setId(estoqueDescartar.getProduto().getId());
            produtoDTO.setNome(estoqueDescartar.getProduto().getNome());
            produtoDTO.setValor(estoqueDescartar.getProduto().getValor());
            estoqueDescartarDTO.setProduto(produtoDTO);
        }

        MatrizDTO matrizDTO = new MatrizDTO();
        if (estoqueDescartar.getMatriz() != null) {
            matrizDTO.setId(estoqueDescartar.getMatriz().getId());
            estoqueDescartarDTO.setMatriz(matrizDTO);
        }

        return estoqueDescartarDTO;
    }

    public MateriaDTO materiaToDTO(Materia materia) {
        MateriaDTO materiaDTO = new MateriaDTO();

        materiaDTO.setId(materia.getId());
        materiaDTO.setAtivo(materia.getAtivo());
        materiaDTO.setDeletado(materia.getDeletado());
        materiaDTO.setNome(materia.getNome());

        if (materia.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(materia.getMatriz().getId());
            materiaDTO.setMatriz(matrizDTO);
        }

        return materiaDTO;
    }

    public CaixaDTO caixaToDTO(Caixa caixa) {
        CaixaDTO caixaDTO = new CaixaDTO();

        caixaDTO.setId(caixa.getId());
        caixaDTO.setAtivo(caixa.getAtivo());
        caixaDTO.setDeletado(caixa.getDeletado());
        caixaDTO.setValorAbertura(caixa.getValorAbertura());
        caixaDTO.setSaldoDinheiro(caixa.getSaldoDinheiro());
        caixaDTO.setSaldoDebito(caixa.getSaldoDebito());
        caixaDTO.setSaldoCredito(caixa.getSaldoCredito());
        caixaDTO.setSaldoPix(caixa.getSaldoPix());
        caixaDTO.setDataAbertura(caixa.getDataAbertura());
        caixaDTO.setDataFechamento(caixa.getDataFechamento());
        caixaDTO.setSaldo(caixa.getSaldo());
        caixaDTO.setNomeImpressora(caixa.getNomeImpressora());

        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        if (caixa.getFuncionario() != null) {
            funcionarioDTO.setId(caixa.getFuncionario().getId());
            funcionarioDTO.setNome(caixa.getFuncionario().getNome());
            caixaDTO.setFuncionario(funcionarioDTO);
        }

        MatrizDTO matrizDTO = new MatrizDTO();
        if (caixa.getMatriz() != null) {
            matrizDTO.setId(caixa.getMatriz().getId());
            caixaDTO.setMatriz(matrizDTO);
        }

        List<VendaDTO> listaVendas = new ArrayList<>();
        if (caixa.getVendas() != null)
            for (int i = 0; i < caixa.getVendas().size(); i++) {
                listaVendas.add(vendaToDTO(caixa.getVendas().get(i)));
            }
        caixaDTO.setVendas(listaVendas);

        List<SangriaDTO> listaSangrias = new ArrayList<>();
        if (caixa.getSangrias() != null)
            for (Sangria sangria : caixa.getSangrias()) {
                if (Boolean.TRUE.equals(sangria.getAtivo())) {
                    listaSangrias.add(sangriaToDTO(sangria));
                }
            }
        caixaDTO.setSangrias(listaSangrias);

        List<SuprimentoDTO> listaSuprimentos = new ArrayList<>();
        if (caixa.getSuprimentos() != null)
            for (Suprimento suprimento : caixa.getSuprimentos()) {
                if (Boolean.TRUE.equals(suprimento.getAtivo())) {
                    listaSuprimentos.add(suprimentoToDTO(suprimento));
                }
            }
        caixaDTO.setSuprimentos(listaSuprimentos);

        List<GorjetaDTO> listaGorjetas = new ArrayList<>();
        if (caixa.getGorjetas() != null)
            for (Gorjeta gorjeta : caixa.getGorjetas()) {
                if (Boolean.TRUE.equals(gorjeta.getAtivo())) {
                    listaGorjetas.add(gorjetaToDTO(gorjeta));
                }
            }
        caixaDTO.setGorjetas(listaGorjetas);

        return caixaDTO;
    }

    private SuprimentoDTO suprimentoToDTO(Suprimento suprimento) {
        SuprimentoDTO suprimentoDTO = new SuprimentoDTO();

        suprimentoDTO.setId(suprimento.getId());
        suprimentoDTO.setAtivo(suprimento.getAtivo());
        suprimentoDTO.setDataSuprimento(suprimento.getDataSuprimento());
        suprimentoDTO.setMotivo(suprimento.getMotivo());
        suprimentoDTO.setValor(suprimento.getValor());
        suprimentoDTO.setNomeImpressora(suprimento.getNomeImpressora());

        if (suprimentoDTO.getFuncionario() != null) {
            FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
            funcionarioDTO.setId(suprimento.getFuncionario().getId());
            funcionarioDTO.setNome(suprimento.getFuncionario().getNome());
            suprimentoDTO.setFuncionario(funcionarioDTO);
        }

        if (suprimentoDTO.getCaixa() != null) {
            CaixaDTO caixaDTO = new CaixaDTO();
            caixaDTO.setId(suprimento.getCaixa().getId());
            suprimentoDTO.setCaixa(caixaDTO);
        }

        return suprimentoDTO;
    }

    private GorjetaDTO gorjetaToDTO(Gorjeta gorjeta) {
        GorjetaDTO gorjetaDTO = new GorjetaDTO();

        gorjetaDTO.setId(gorjeta.getId());
        gorjetaDTO.setAtivo(gorjeta.getAtivo());
        gorjetaDTO.setDataGorjeta(gorjeta.getDataGorjeta());
        gorjetaDTO.setDinheiro(gorjeta.getDinheiro());
        gorjetaDTO.setDebito(gorjeta.getDebito());
        gorjetaDTO.setCredito(gorjeta.getCredito());
        gorjetaDTO.setPix(gorjeta.getPix());
        gorjetaDTO.setNomeImpressora(gorjeta.getNomeImpressora());

        if (gorjetaDTO.getFuncionario() != null) {
            FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
            funcionarioDTO.setId(gorjeta.getFuncionario().getId());
            funcionarioDTO.setNome(gorjeta.getFuncionario().getNome());
            gorjetaDTO.setFuncionario(funcionarioDTO);
        }

        if (gorjetaDTO.getCaixa() != null) {
            CaixaDTO caixaDTO = new CaixaDTO();
            caixaDTO.setId(gorjeta.getCaixa().getId());
            gorjetaDTO.setCaixa(caixaDTO);
        }

        return gorjetaDTO;
    }

    private SangriaDTO sangriaToDTO(Sangria sangria) {
        SangriaDTO sangriaDTO = new SangriaDTO();

        sangriaDTO.setId(sangria.getId());
        sangriaDTO.setAtivo(sangria.getAtivo());
        sangriaDTO.setDataSangria(sangria.getDataSangria());
        sangriaDTO.setMotivo(sangria.getMotivo());
        sangriaDTO.setValor(sangria.getValor());
        sangriaDTO.setNomeImpressora(sangria.getNomeImpressora());
        sangriaDTO.setTipo(sangria.getTipo());
        sangriaDTO.setNomeFuncionario(sangria.getNomeFuncionario());

        if (sangriaDTO.getFuncionario() != null) {
            FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
            funcionarioDTO.setId(sangria.getFuncionario().getId());
            funcionarioDTO.setNome(sangria.getFuncionario().getNome());
            sangriaDTO.setFuncionario(funcionarioDTO);
        }

        if (sangriaDTO.getCaixa() != null) {
            CaixaDTO caixaDTO = new CaixaDTO();
            caixaDTO.setId(sangria.getCaixa().getId());
            sangriaDTO.setCaixa(caixaDTO);
        }

        return sangriaDTO;
    }

    public VendaDTO vendaToDTO(Venda venda) {
        VendaDTO vendaDTO = new VendaDTO();

        vendaDTO.setId(venda.getId());
        vendaDTO.setAtivo(venda.getAtivo());
        vendaDTO.setRetirada(venda.getRetirada());
        vendaDTO.setEntrega(venda.getEntrega());
        vendaDTO.setBalcao(venda.getBalcao());
        vendaDTO.setDeletado(venda.getDeletado());
        vendaDTO.setChaveUnico(venda.getChaveUnico());
        vendaDTO.setImprimirDeletar(venda.getImprimirDeletar());
        vendaDTO.setImprimirCadastrar(venda.getImprimirCadastrar());
        vendaDTO.setImprimirNotaFiscal(venda.getImprimirNotaFiscal());
        vendaDTO.setNotaFiscal(venda.getNotaFiscal());
        vendaDTO.setStatusEmAberto(venda.getStatusEmAberto());
        vendaDTO.setStatusEmPagamento(venda.getStatusEmPagamento());
        vendaDTO.setValorTotal(venda.getValorTotal());
        vendaDTO.setDataVenda(venda.getDataVenda());
        vendaDTO.setDataEdicao(venda.getDataEdicao());
        vendaDTO.setMesa(venda.getMesa());
        vendaDTO.setMotivoDeletar(venda.getMotivoDeletar());
        vendaDTO.setNomeImpressora(venda.getNomeImpressora());
        vendaDTO.setTaxaEntrega(venda.getTaxaEntrega());
        vendaDTO.setTempoEstimado(venda.getTempoEstimado());
        vendaDTO.setValorServico(venda.getValorServico());
        vendaDTO.setValorBruto(venda.getValorBruto());
        vendaDTO.setDesconto(venda.getDesconto());
        vendaDTO.setMotivoDesconto(venda.getMotivoDesconto());

        if (venda.getCliente() != null) {
            vendaDTO.setCliente(clienteToDTO(venda.getCliente()));
        }

        if (venda.getEndereco() != null) {
            vendaDTO.setEndereco(enderecoToDTO(venda.getEndereco()));
        }

        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        if (venda.getFuncionario() != null) {
            funcionarioDTO.setId(venda.getFuncionario().getId());
            funcionarioDTO.setNome(venda.getFuncionario().getNome());
            vendaDTO.setFuncionario(funcionarioDTO);
        }

        if (venda.getCaixa() != null) {
            CaixaDTO caixaDTO = new CaixaDTO();
            caixaDTO.setId(venda.getCaixa().getId());
            vendaDTO.setCaixa(caixaDTO);
        }

        if (venda.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(venda.getMatriz().getId());
            vendaDTO.setMatriz(matrizDTO);
        }

        List<ProdutoVendaDTO> listaProdutoVendasDTO = new ArrayList<>();
        if (venda.getProdutoVendas() != null)
            for (int i = 0; i < venda.getProdutoVendas().size(); i++) {
                listaProdutoVendasDTO.add(produtoVendaToDTO(venda.getProdutoVendas().get(i)));
            }
        vendaDTO.setProdutoVendas(listaProdutoVendasDTO);

        if (venda.getVendaPagamento() != null) {
            VendaPagamentoDTO vendaPagamentoDTO = new VendaPagamentoDTO();

            vendaPagamentoDTO.setId(venda.getVendaPagamento().getId());
            vendaPagamentoDTO.setDinheiro(venda.getVendaPagamento().getDinheiro());
            vendaPagamentoDTO.setPix(venda.getVendaPagamento().getPix());
            vendaPagamentoDTO.setDebito(venda.getVendaPagamento().getDebito());
            vendaPagamentoDTO.setCredito(venda.getVendaPagamento().getCredito());

            vendaPagamentoDTO.setDescontoDinheiro(venda.getVendaPagamento().getDescontoDinheiro());
            vendaPagamentoDTO.setDescontoCredito(venda.getVendaPagamento().getDescontoCredito());
            vendaPagamentoDTO.setDescontoDebito(venda.getVendaPagamento().getDescontoDebito());
            vendaPagamentoDTO.setDescontoPix(venda.getVendaPagamento().getDescontoPix());

            vendaPagamentoDTO.setServicoDinheiro(venda.getVendaPagamento().getServicoDinheiro());
            vendaPagamentoDTO.setServicoCredito(venda.getVendaPagamento().getServicoCredito());
            vendaPagamentoDTO.setServicoDebito(venda.getVendaPagamento().getServicoDebito());
            vendaPagamentoDTO.setServicoPix(venda.getVendaPagamento().getServicoPix());


            vendaDTO.setVendaPagamento(vendaPagamentoDTO);
        }

        return vendaDTO;
    }

    public ClienteDTO clienteToDTO(Cliente cliente) {
        ClienteDTO clienteDTO = new ClienteDTO();

        clienteDTO.setId(cliente.getId());
        clienteDTO.setAtivo(cliente.getAtivo());
        clienteDTO.setNome(cliente.getNome());
        clienteDTO.setCpf(cliente.getCpf());
        clienteDTO.setCelular(cliente.getCelular());

        List<EnderecoDTO> listaEnderecos = new ArrayList<>();
        if (cliente.getEnderecos() != null)
            for (int i = 0; i < cliente.getEnderecos().size(); i++) {
                listaEnderecos.add(enderecoToDTO(cliente.getEnderecos().get(i)));
            }
        clienteDTO.setEnderecos(listaEnderecos);

        if (cliente.getMatriz() != null) {
            MatrizDTO matrizDTO = new MatrizDTO();
            matrizDTO.setId(cliente.getMatriz().getId());
            clienteDTO.setMatriz(matrizDTO);
        }

        return clienteDTO;
    }

    public EnderecoDTO enderecoToDTO(Endereco endereco) {
        EnderecoDTO enderecoDTO = new EnderecoDTO();

        enderecoDTO.setId(endereco.getId());
        enderecoDTO.setAtivo(endereco.getAtivo());
        enderecoDTO.setEstado(endereco.getEstado());
        enderecoDTO.setCidade(endereco.getCidade());
        enderecoDTO.setBairro(endereco.getBairro());
        enderecoDTO.setCep(endereco.getCep());
        enderecoDTO.setRua(endereco.getRua());
        enderecoDTO.setNumero(endereco.getNumero());
        enderecoDTO.setComplemento(endereco.getComplemento());
        enderecoDTO.setReferencia(endereco.getReferencia());
        enderecoDTO.setLatitude(endereco.getLatitude());
        enderecoDTO.setLongitude(endereco.getLongitude());

        return enderecoDTO;
    }

    public ProdutoVendaDTO produtoVendaToDTO(ProdutoVenda produtoVenda) {
        ProdutoVendaDTO produtoVendaDTO = new ProdutoVendaDTO();

        produtoVendaDTO.setId(produtoVenda.getId());
        produtoVendaDTO.setAtivo(produtoVenda.getAtivo());
        produtoVendaDTO.setQuantidade(produtoVenda.getQuantidade());
        produtoVendaDTO.setValor(produtoVenda.getValor());
        produtoVendaDTO.setData(produtoVenda.getData());
        produtoVendaDTO.setObservacaoProdutoVenda(produtoVenda.getObservacaoProdutoVenda());
        produtoVendaDTO.setMotivoExclusao(produtoVenda.getMotivoExclusao());
        produtoVendaDTO.setOrigemTransferenciaNumero(produtoVenda.getOrigemTransferenciaNumero());

        if (produtoVenda.getProduto() != null) {
            produtoVendaDTO.setProduto(produtoToDTO(produtoVenda.getProduto()));
        }

        VendaDTO vendaDTO = new VendaDTO();
        if (produtoVenda.getVenda() != null) {
            vendaDTO.setId(produtoVenda.getVenda().getId());
            produtoVendaDTO.setVenda(vendaDTO);
        }
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        if (produtoVenda.getFuncionario() != null) {
            funcionarioDTO.setId(produtoVenda.getFuncionario().getId());
            funcionarioDTO.setNome(produtoVenda.getFuncionario().getNome());
            produtoVendaDTO.setFuncionario(funcionarioDTO);
        }

        if (produtoVenda.getObservacoesProdutoVenda() != null) {
            List<ObservacoesDTO> listaObservacoesDTO = new ArrayList<>();
            for (int i = 0; i < produtoVenda.getObservacoesProdutoVenda().size(); i++) {
                listaObservacoesDTO.add(observacoesToDTO(produtoVenda.getObservacoesProdutoVenda().get(i)));
            }
            produtoVendaDTO.setObservacoesProdutoVenda(listaObservacoesDTO);
        }

        return produtoVendaDTO;
    }

    public ProdutoDTO produtoToDTO(Produto produto) {
        ProdutoDTO produtoDTO = new ProdutoDTO();

        produtoDTO.setId(produto.getId());
        produtoDTO.setAtivo(produto.getAtivo());
        produtoDTO.setDeletado(produto.getDeletado());
        produtoDTO.setCardapio(produto.getCardapio());
        produtoDTO.setNome(produto.getNome());
        produtoDTO.setValor(produto.getValor());
        produtoDTO.setTipo(produto.getTipo());
        produtoDTO.setCodigo(produto.getCodigo());
        produtoDTO.setValidarExestencia(produto.getValidarExestencia());
        produtoDTO.setEstocavel(produto.getEstocavel());
        if (produto.getDeveImprimir() != null) {
            produtoDTO.setDeveImprimir(produto.getDeveImprimir());
        }

        if (produto.getImpressoras() != null) {
            List<ImpressoraDTO> impressorasDTO = produto.getImpressoras().stream()
                    .map(this::impressoraToDTO)
                    .collect(Collectors.toList());
            produtoDTO.setImpressoras(impressorasDTO);
        } else {
            produtoDTO.setImpressoras(new ArrayList<>());
        }

        List<ProdutoMateriaDTO> materiasDTO = produto.getProdutoMaterias() != null ?
                produto.getProdutoMaterias().stream()
                        .filter(pm -> Boolean.TRUE.equals(pm.getAtivo()))
                        .map(this::produtoMateriaToDTO)
                        .collect(Collectors.toList()) : new ArrayList<>();
        produtoDTO.setProdutoMaterias(materiasDTO);

        List<ProdutoCompostoDTO> compostosDTO = produto.getProdutoCompostos() != null ?
                produto.getProdutoCompostos().stream()
                        .filter(pc -> Boolean.TRUE.equals(pc.getAtivo()))
                        .map(this::produtoCompostoToDTO)
                        .collect(Collectors.toList()) : new ArrayList<>();
        produtoDTO.setProdutoCompostos(compostosDTO);

        MatrizDTO matrizDTO = new MatrizDTO();
        if (produto.getMatriz() != null) {
            matrizDTO.setId(produto.getMatriz().getId());
            produtoDTO.setMatriz(matrizDTO);
        }

        if (produto.getCategoria() != null) {
            produtoDTO.setCategoria(categoriaToDTO(produto.getCategoria()));
        }

        return produtoDTO;
    }

    public ProdutoCompostoDTO produtoCompostoToDTO(ProdutoComposto produtoComposto) {
        ProdutoCompostoDTO dto = new ProdutoCompostoDTO();

        dto.setId(produtoComposto.getId());
        dto.setAtivo(produtoComposto.getAtivo());
        dto.setQuantidadeGasto(produtoComposto.getQuantidadeGasto());

        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(produtoComposto.getProduto().getId());
        dto.setProduto(produtoDTO);

        Produto produtoCompostoEntity = produtoComposto.getProdutoComposto();
        if (produtoCompostoEntity != null) {
            ProdutoDTO compostoDTO = new ProdutoDTO();
            compostoDTO.setId(produtoCompostoEntity.getId());
            compostoDTO.setAtivo(produtoCompostoEntity.getAtivo());
            compostoDTO.setDeletado(produtoCompostoEntity.getDeletado());
            compostoDTO.setCardapio(produtoCompostoEntity.getCardapio());
            compostoDTO.setNome(produtoCompostoEntity.getNome());
            compostoDTO.setTipo(produtoCompostoEntity.getTipo());
            compostoDTO.setValor(produtoCompostoEntity.getValor());
            compostoDTO.setCodigo(produtoCompostoEntity.getCodigo());
            compostoDTO.setValidarExestencia(produtoCompostoEntity.getValidarExestencia());
            compostoDTO.setEstocavel(produtoCompostoEntity.getEstocavel());

            List<ProdutoMateriaDTO> materiasDTO = produtoCompostoEntity.getProdutoMaterias() != null ?
                    produtoCompostoEntity.getProdutoMaterias().stream()
                            .filter(pm -> Boolean.TRUE.equals(pm.getAtivo()))
                            .map(this::produtoMateriaToDTO)
                            .collect(Collectors.toList()) : new ArrayList<>();
            compostoDTO.setProdutoMaterias(materiasDTO);

            List<ProdutoCompostoDTO> compostosFilhosDTO = produtoCompostoEntity.getProdutoCompostos() != null ?
                    produtoCompostoEntity.getProdutoCompostos().stream()
                            .filter(pc -> Boolean.TRUE.equals(pc.getAtivo()))
                            .map(this::produtoCompostoToDTO)
                            .collect(Collectors.toList()) : new ArrayList<>();
            compostoDTO.setProdutoCompostos(compostosFilhosDTO);

            dto.setProdutoComposto(compostoDTO);
        }

        return dto;
    }

    public ProdutoMateriaDTO produtoMateriaToDTO(ProdutoMateria produtoMateria) {
        ProdutoMateriaDTO produtoMateriaDTO = new ProdutoMateriaDTO();

        produtoMateriaDTO.setId(produtoMateria.getId());
        produtoMateriaDTO.setAtivo(produtoMateria.getAtivo());
        produtoMateriaDTO.setQuantidadeGasto(produtoMateria.getQuantidadeGasto());

        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(produtoMateria.getProduto().getId());
        produtoMateriaDTO.setProduto(produtoDTO);

        MateriaDTO materiaDTO = new MateriaDTO();
        if (produtoMateria.getMateria() != null) {
            materiaDTO.setId(produtoMateria.getMateria().getId());
            materiaDTO.setAtivo(produtoMateria.getMateria().getAtivo());
            materiaDTO.setDeletado(produtoMateria.getMateria().getDeletado());
            materiaDTO.setNome(produtoMateria.getMateria().getNome());
            produtoMateriaDTO.setMateria(materiaDTO);
        }

        return produtoMateriaDTO;
    }

    public CategoriaDTO categoriaToDTO(Categoria categoria) {
        CategoriaDTO categoriaDTO = new CategoriaDTO();

        categoriaDTO.setId(categoria.getId());
        categoriaDTO.setAtivo(categoria.getAtivo());
        categoriaDTO.setDeletado(categoria.getDeletado());
        categoriaDTO.setNome(categoria.getNome());
        categoriaDTO.setObsObrigatotio(categoria.getObsObrigatotio());
        categoriaDTO.setMaxObs(categoria.getMaxObs());

        MatrizDTO matrizDTO = new MatrizDTO();
        if (categoria.getMatriz() != null) {
            matrizDTO.setId(categoria.getMatriz().getId());
            categoriaDTO.setMatriz(matrizDTO);
        }

        if (categoria.getObservacoesCategoria() != null) {
            List<ObservacoesDTO> listaObservacoesDTO = categoria.getObservacoesCategoria().stream()
                    .filter(obs -> Boolean.TRUE.equals(obs.getAtivo()))
                    .map(this::observacoesToDTO)
                    .collect(Collectors.toList());

            categoriaDTO.setObservacoesCategoria(listaObservacoesDTO);
        } else {
            categoriaDTO.setObservacoesCategoria(new ArrayList<>());
        }

        return categoriaDTO;
    }

    public ObservacoesDTO observacoesToDTO(Observacoes observacoes) {
        ObservacoesDTO observacoesDTO = new ObservacoesDTO();

        observacoesDTO.setId(observacoes.getId());
        observacoesDTO.setAtivo(observacoes.getAtivo());
        observacoesDTO.setObservacao(observacoes.getObservacao());
        observacoesDTO.setValidarExestencia(observacoes.getValidarExestencia());
        observacoesDTO.setExtra(observacoes.getExtra());
        if (observacoes.getValor() != null) {
            observacoesDTO.setValor(observacoes.getValor());
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        if (observacoes.getCategoria() != null) {
            categoriaDTO.setId(observacoes.getCategoria().getId());
            observacoesDTO.setCategoria(categoriaDTO);
        }

        List<ObservacaoMateriaDTO> materiasDTO = observacoes.getObservacaoMaterias() != null ?
                observacoes.getObservacaoMaterias().stream()
                        .filter(m -> Boolean.TRUE.equals(m.getAtivo()))
                        .map(this::observacaoMateriasToDTO)
                        .collect(Collectors.toList()) : new ArrayList<>();
        observacoesDTO.setObservacaoMaterias(materiasDTO);

        List<ObservacaoProdutoDTO> produtosDTO = observacoes.getObservacaoProdutos() != null ?
                observacoes.getObservacaoProdutos().stream()
                        .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                        .map(this::observacaoProdutosToDTO)
                        .collect(Collectors.toList()) : new ArrayList<>();
        observacoesDTO.setObservacaoProdutos(produtosDTO);

        return observacoesDTO;
    }

    public ObservacaoMateriaDTO observacaoMateriasToDTO(ObservacaoMateria ObservacaoMateria) {
        ObservacaoMateriaDTO observacaoMateriaDTO = new ObservacaoMateriaDTO();

        observacaoMateriaDTO.setId(ObservacaoMateria.getId());
        observacaoMateriaDTO.setAtivo(ObservacaoMateria.getAtivo());
        observacaoMateriaDTO.setQuantidadeGasto(ObservacaoMateria.getQuantidadeGasto());

        ObservacoesDTO observacoesDTO = new ObservacoesDTO();
        observacoesDTO.setId(ObservacaoMateria.getObservacoes().getId());
        observacaoMateriaDTO.setObservacoes(observacoesDTO);

        MateriaDTO materiaDTO = new MateriaDTO();
        if (ObservacaoMateria.getMateria() != null) {
            materiaDTO.setId(ObservacaoMateria.getMateria().getId());
            materiaDTO.setAtivo(ObservacaoMateria.getMateria().getAtivo());
            materiaDTO.setDeletado(ObservacaoMateria.getMateria().getDeletado());
            materiaDTO.setNome(ObservacaoMateria.getMateria().getNome());
            observacaoMateriaDTO.setMateria(materiaDTO);
        }

        return observacaoMateriaDTO;
    }

    public ObservacaoProdutoDTO observacaoProdutosToDTO(ObservacaoProduto observacaoProduto) {
        ObservacaoProdutoDTO observacaoProdutoDTO = new ObservacaoProdutoDTO();

        observacaoProdutoDTO.setId(observacaoProduto.getId());
        observacaoProdutoDTO.setQuantidadeGasto(observacaoProduto.getQuantidadeGasto());

        ObservacoesDTO observacoesDTO = new ObservacoesDTO();
        observacoesDTO.setId(observacaoProduto.getObservacoes().getId());
        observacaoProdutoDTO.setObservacoes(observacoesDTO);

        ProdutoDTO produtoDTO = new ProdutoDTO();
        if (observacaoProduto.getProduto() != null) {
            produtoDTO.setId(observacaoProduto.getProduto().getId());
            produtoDTO.setAtivo(observacaoProduto.getProduto().getAtivo());
            produtoDTO.setDeletado(observacaoProduto.getProduto().getDeletado());
            produtoDTO.setCardapio(observacaoProduto.getProduto().getCardapio());
            produtoDTO.setNome(observacaoProduto.getProduto().getNome());
            observacaoProdutoDTO.setProduto(produtoDTO);
        }

        return observacaoProdutoDTO;
    }

    public AdminDTO adminToDTO(Admin admin) {
        AdminDTO adminDTO = new AdminDTO();

        adminDTO.setId(admin.getId());
        adminDTO.setAtivo(admin.getAtivo());
        adminDTO.setDeletado(admin.getDeletado());
        adminDTO.setNome(admin.getNome());
        adminDTO.setCnpj(admin.getCnpj());
        adminDTO.setUsername(admin.getUsername());
        adminDTO.setCelular(admin.getCelular());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setRole(admin.getRole());
        adminDTO.setChaveApiCoordenades(admin.getChaveApiCoordenades());

        if (admin.getPermissao() != null) {
            PermissaoDTO permissaoDTO = permissaoToDTO(admin.getPermissao());

            adminDTO.setPermissao(permissaoDTO);
        }

        return adminDTO;
    }

    public AdminFuncionarioDTO adminFuncionarioToDTO(AdminFuncionario adminFuncionario) {
        AdminFuncionarioDTO adminFuncionarioDTO = new AdminFuncionarioDTO();

        adminFuncionarioDTO.setId(adminFuncionario.getId());
        adminFuncionarioDTO.setAtivo(adminFuncionario.getAtivo());
        adminFuncionarioDTO.setDeletado(adminFuncionario.getDeletado());
        adminFuncionarioDTO.setNome(adminFuncionario.getNome());
        adminFuncionarioDTO.setUsername(adminFuncionario.getUsername());
        adminFuncionarioDTO.setCelular(adminFuncionario.getCelular());
        adminFuncionarioDTO.setEmail(adminFuncionario.getEmail());
        adminFuncionarioDTO.setRole(adminFuncionario.getRole());

        AdminDTO adminDTO = new AdminDTO();
        if (adminFuncionario.getAdmin() != null) {
            adminDTO.setId(adminFuncionario.getAdmin().getId());
            adminFuncionarioDTO.setAdmin(adminDTO);
        }


        if (adminFuncionario.getPermissao() != null) {
            PermissaoDTO permissaoDTO = permissaoToDTO(adminFuncionario.getPermissao());

            adminFuncionarioDTO.setPermissao(permissaoDTO);
        }

        return adminFuncionarioDTO;
    }
}