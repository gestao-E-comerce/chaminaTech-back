package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProdutoMateriaDTO {

    private Long id;

    private Boolean ativo = true;

    @JsonIgnoreProperties("produtoMaterias")
    private ProdutoDTO produto;

    private MateriaDTO materia;

    private BigDecimal quantidadeGasto;
}