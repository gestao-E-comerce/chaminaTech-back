package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class ProdutoDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private Boolean cardapio = true;

    private Boolean validarExestencia = false;

    private Boolean estocavel = false;

    private Boolean deveImprimir = false;

    private List<ProdutoMateriaDTO> produtoMaterias;

    private List<ProdutoCompostoDTO> produtoCompostos;

    private MatrizDTO matriz;

    private CategoriaDTO categoria;

    private List<ImpressoraDTO> impressoras;

    private BigDecimal quantidadeDisponivel;

    private BigDecimal quantidadeDescartada;

    private BigDecimal margemLucro;

    private String nome;

    private BigDecimal valor;

    private BigDecimal valorCusto;

    private String unidadeComercial;

    private String unidadeTributavel;

    private Integer codigo;

    private String codigoBarras;

    private String ncm;

    private String cest;

    private String cfop;

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
}