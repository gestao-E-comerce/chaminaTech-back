package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ProdutoDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private Boolean cardapio = true;

    private String nome;

    private Double valor;

    private Boolean tipo;

    private Integer codigo;

    private Boolean validarExestencia = false;

    private Boolean estocavel = false;

    private Boolean deveImprimir = false;

    @JsonIgnoreProperties("produto")
    private List<ProdutoMateriaDTO> produtoMaterias;

    @JsonIgnoreProperties("produto")
    private List<ProdutoCompostoDTO> produtoCompostos;

    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private MatrizDTO matriz;

    @JsonIgnoreProperties(value = {"produtos", "matriz"}, allowSetters = true)
    private CategoriaDTO categoria;

    @JsonIgnoreProperties("matriz")
    private List<ImpressoraDTO> impressoras;

    private Double quantidadeDisponivel;

    private Double quantidadeDescartada;
}