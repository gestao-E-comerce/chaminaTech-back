package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class MateriaDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private String nome;

    private MatrizDTO matriz;

    private BigDecimal quantidadeDisponivel;

    private BigDecimal quantidadeDescartada;
}