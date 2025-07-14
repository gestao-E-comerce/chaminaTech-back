package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter @Setter
public class DepositoDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private MateriaDTO materia;

    private BigDecimal quantidade;

    private BigDecimal quantidadeVendido;

    private BigDecimal valorTotal;

    private Timestamp dataCadastrar;

    private Timestamp dataDesativar;

    private MatrizDTO matriz;
}