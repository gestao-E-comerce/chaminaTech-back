package chaminaTech.Graficos.GraficoSangria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoTipoSangria {
    private String tipo;
    private BigDecimal total;
}
