package chaminaTech.Graficos.GraficoVenda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoPagamentoVenda {
    private BigDecimal pix;
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal dinheiro;
}
