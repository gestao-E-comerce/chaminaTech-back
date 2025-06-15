package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class DepositoDescartarDTO {
    private Long id;

    @JsonIgnoreProperties(value = {"matriz"})
    private MateriaDTO materia;

    private BigDecimal quantidade = BigDecimal.ZERO;

    private Timestamp dataDescartar;

    private String motivo;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;
}