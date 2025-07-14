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
public class GraficoPagamentoCaixa {
    private String dataAbertura;

    // Pagamento
    private BigDecimal pagamentoDinheiro;
    private BigDecimal pagamentoCredito;
    private BigDecimal pagamentoDebito;
    private BigDecimal pagamentoPix;

    // Desconto
    private BigDecimal descontoDinheiro;
    private BigDecimal descontoCredito;
    private BigDecimal descontoDebito;
    private BigDecimal descontoPix;

    // Gorjeta
    private BigDecimal gorjetaDinheiro;
    private BigDecimal gorjetaCredito;
    private BigDecimal gorjetaDebito;
    private BigDecimal gorjetaPix;

    // Serviço
    private BigDecimal servicoDinheiro;
    private BigDecimal servicoCredito;
    private BigDecimal servicoDebito;
    private BigDecimal servicoPix;
}
