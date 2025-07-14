package chaminaTech.Graficos.GraficoDepositoDescartar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoResumoDepositoDescartar {
    private String materiaNome;
    private BigDecimal quantidadeDescartada;
}