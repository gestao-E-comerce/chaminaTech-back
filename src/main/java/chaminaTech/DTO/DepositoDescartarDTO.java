package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class DepositoDescartarDTO {
    private Long id;

    private MateriaDTO materia;

    private BigDecimal quantidade = BigDecimal.ZERO;

    private Timestamp dataDescartar;

    private String motivo;

    private MatrizDTO matriz;
}