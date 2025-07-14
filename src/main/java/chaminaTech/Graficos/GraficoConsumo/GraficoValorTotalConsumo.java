package chaminaTech.Graficos.GraficoConsumo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoValorTotalConsumo {
    private String periodo;
    private BigDecimal valorTotal;
}
