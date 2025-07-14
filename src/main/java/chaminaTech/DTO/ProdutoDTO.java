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

    private String nome;

    private BigDecimal valor;

    private Boolean tipo;

    private Integer codigo;

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
}