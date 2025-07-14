package chaminaTech.Graficos.GraficoEstoque;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoResumoEstoque {
    private String produtoNome;
    private BigDecimal quantidadeTotal;
    private BigDecimal quantidadeVendida;
    private BigDecimal quantidadeDisponivel;
    private BigDecimal valorTotal;
}