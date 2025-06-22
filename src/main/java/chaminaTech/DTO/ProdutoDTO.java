package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private String nome;

    private BigDecimal valor;

    private Boolean tipo;

    private Integer codigo;

    private Boolean validarExestencia = false;

    private Boolean estocavel = false;

    private Boolean deveImprimir = false;

    @JsonIgnoreProperties("produto")
    private List<ProdutoMateriaDTO> produtoMaterias;

    @JsonIgnoreProperties("produto")
    private List<ProdutoCompostoDTO> produtoCompostos;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;

    @JsonIgnoreProperties(value = {"produtos", "matriz"}, allowSetters = true)
    private CategoriaDTO categoria;

    @JsonIgnoreProperties("matriz")
    private List<ImpressoraDTO> impressoras;

    private BigDecimal quantidadeDisponivel;

    private BigDecimal quantidadeDescartada;
}