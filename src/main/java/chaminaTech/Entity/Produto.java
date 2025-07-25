package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean deletado = false;

    @Column(nullable = false)
    private Boolean cardapio = true;

    @Column(nullable = false)
    private Boolean validarExestencia = false;

    @Column(nullable = false)
    private Boolean estocavel = false;

    @Column(nullable = false)
    private Boolean deveImprimir = false;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("produto")
    private List<ProdutoMateria> produtoMaterias;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("produto")
    @JsonManagedReference
    private List<ProdutoComposto> produtoCompostos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_matriz")
    @JsonIgnoreProperties(value = { "configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao",
            "configuracaoTaxaServico", }, allowSetters = true)
    private Matriz matriz;

    @ManyToOne
    @JoinColumn(name = "produto_categoria")
    @JsonIgnoreProperties(value = { "produtos", "matriz" }, allowSetters = true)
    private Categoria categoria;

    @ManyToMany
    @JoinTable(name = "produto_impressoras", joinColumns = @JoinColumn(name = "produto_id"), inverseJoinColumns = @JoinColumn(name = "impressora_id"))
    @JsonIgnoreProperties("matriz")
    private List<Impressora> impressoras;

    private BigDecimal quantidadeDisponivel;

    private BigDecimal quantidadeDescartada;

    @Column(nullable = false)
    private BigDecimal margemLucro;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private BigDecimal valorCusto;

    @Column(nullable = false)
    private String unidadeComercial;

    @Column(nullable = false)
    private String unidadeTributavel;

    @Column(nullable = false)
    private Integer codigo;

    @Column(nullable = false)
    private String codigoBarras;

    @Column(nullable = false)
    private String ncm;

    @Column(nullable = false)
    private String cest;

    @Column(nullable = false)
    private String cfop;

    @Column(nullable = false)
    private Integer origem;

    // ===== ICMS (Imposto sobre Circulação de Mercadorias) =====
    private String csosnIcms;                 // CSOSNDTO (para Simples Nacional)
    private String cstIcms;                   // CSTDTO (para Regime Normal)
    private Integer modalidadeBaseCalculoIcms; // modBC
    private BigDecimal aliquotaIcms;          // pICMS

    // ===== IPI (Imposto sobre Produtos Industrializados) =====
    private String cstIpi;                    // Código de Situação Tributária IPI
    private String codigoEnquadramentoIpi;    // cEnq
    private Integer tipoCalculoIpi;           // 1 = Alíquota, 2 = Valor fixo
    private BigDecimal aliquotaIpi;           // pIPI

    // ===== PIS (Programa de Integração Social) =====
    private String cstPis;                    // Código de Situação Tributária PIS
    private BigDecimal aliquotaPis;           // pPIS

    // ===== COFINS (Contribuição para o Financiamento da Seguridade Social) =====
    private String cstCofins;                 // Código de Situação Tributária COFINS
    private BigDecimal aliquotaCofins;        // pCOFINS

//    @ManyToOne
//    @JoinColumn(name = "regra_fiscal_id")
//    private RegraFiscal regraFiscal;
}