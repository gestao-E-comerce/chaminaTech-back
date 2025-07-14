package chaminaTech.Graficos.GraficoSuprimento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoPagamentoServico {
    private BigDecimal pix;
    private BigDecimal credito;
    private BigDecimal debito;
    private BigDecimal dinheiro;
}
