package chaminaTech.Graficos.GraficoDeposito;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoResumoDeposito {
    private String materiaNome;
    private BigDecimal quantidadeTotal;
    private BigDecimal quantidadeVendida;
    private BigDecimal quantidadeDisponivel;
    private BigDecimal valorTotal;
}