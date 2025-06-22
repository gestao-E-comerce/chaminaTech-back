package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter @Setter
public class VendaDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private Boolean balcao;

    private Boolean retirada;

    private Boolean entrega;

    private String chaveUnico;

    private Boolean imprimirDeletar = true;

    private Boolean imprimirCadastrar = true;

    private Boolean imprimirNotaFiscal = true;

    private Boolean notaFiscal = true;

    private Boolean statusEmAberto = false;

    private Boolean statusEmPagamento = false;

    private BigDecimal valorTotal;

    private Timestamp dataVenda ;

    private Timestamp dataEdicao;

    private Integer mesa;

    private String motivoDeletar;

    private String nomeImpressora;

    private BigDecimal taxaEntrega;

    private Integer tempoEstimado;

    private BigDecimal valorServico;

    private BigDecimal valorBruto;

    private BigDecimal desconto;

    private String motivoDesconto;

//    @JsonIgnoreProperties(value = {"matriz","enderecos"})
    private ClienteDTO cliente;

//    @JsonIgnoreProperties("cliente")
    private EnderecoDTO endereco;

//    @JsonIgnoreProperties(value = {"venda"}, allowSetters = true)
    private List<ProdutoVendaDTO> produtoVendas;

//    @JsonIgnoreProperties(value = {"matriz","caixas"})
    private FuncionarioDTO funcionario;

//    @JsonIgnoreProperties(value = {"vendas","matriz","funcionario","sangrias","suprimentos"})
    private CaixaDTO caixa;

//    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;

//    @JsonIgnoreProperties(value = {"venda"}, allowSetters = true)
    private VendaPagamentoDTO vendaPagamento;
}