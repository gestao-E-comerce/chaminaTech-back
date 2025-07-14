package chaminaTech.Graficos.GraficoEstoqueDescartar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoResumoEstoqueDescartar {
    private String produtoNome;
    private BigDecimal quantidadeDescartada;
}