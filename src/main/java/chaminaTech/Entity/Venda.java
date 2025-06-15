package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    @Column(nullable = false)
    private Boolean balcao;

    @Column(nullable = false)
    private Boolean retirada;

    @Column(nullable = false)
    private Boolean entrega;

    private Integer mesa;

    private String chaveUnico;

    private Boolean imprimirDeletar = true;

    private Boolean imprimirCadastrar = true;

    private Boolean imprimirNotaFiscal = true;

    private Boolean notaFiscal = true;

    private Boolean statusEmAberto = false;

    private Boolean statusEmPagamento = false;

    private Double valorTotal;

    private Double valorBruto;

    @Column(nullable = false)
    private Timestamp dataVenda;

    private Timestamp dataEdicao;

    private String motivo;

    private String nomeImpressora;

    private Double taxaEntrega;

    private Integer tempoEstimado;

    private BigDecimal valorServico;

    private BigDecimal desconto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_cliente")
    @JsonIgnoreProperties(value = { "matriz", "enderecos" })
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_cliente")
    @JsonIgnoreProperties("cliente")
    private Endereco endereco;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "venda" }, allowSetters = true)
    @OrderBy("data ASC")
    private List<ProdutoVenda> produtoVendas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_funcionario", nullable = false)
    @JsonIgnoreProperties(value = { "matriz", "caixas" })
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_caixa")
    @JsonIgnoreProperties(value = { "vendas", "matriz", "funcionario", "sangrias", "suprimentos" })
    private Caixa caixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_matriz", nullable = false)
    @JsonIgnoreProperties(value = { "configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao",
            "configuracaoTaxaServico", }, allowSetters = true)
    private Matriz matriz;

    @OneToOne(mappedBy = "venda", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "venda" }, allowSetters = true)
    private VendaPagamento vendaPagamento;
}