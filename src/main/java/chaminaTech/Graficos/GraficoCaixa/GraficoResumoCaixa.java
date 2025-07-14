package chaminaTech.Graficos.GraficoCaixa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoResumoCaixa {
    private String dataAbertura;
    private BigDecimal abertura;
    private BigDecimal suprimentos;
    private BigDecimal vendas;
    private BigDecimal servico;
    private BigDecimal gorjetas;
    private BigDecimal sangrias;
    private BigDecimal descontos;
    private BigDecimal saldoDefinido;
    private BigDecimal saldoFinal;
}
