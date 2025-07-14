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
public class GraficoValorTotalVenda {
    private String periodo;
    private BigDecimal valorTotal;
    private BigDecimal valorBruto;
    private BigDecimal valorServico;
    private BigDecimal taxaEntrega;
    private BigDecimal desconto;
}