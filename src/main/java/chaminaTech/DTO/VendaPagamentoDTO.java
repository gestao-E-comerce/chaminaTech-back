package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VendaPagamentoDTO {

    private Long id;

    private BigDecimal dinheiro;
    private BigDecimal debito;
    private BigDecimal credito;
    private BigDecimal pix;

    private BigDecimal descontoDinheiro;
    private BigDecimal descontoDebito;
    private BigDecimal descontoCredito;
    private BigDecimal descontoPix;

    private BigDecimal servicoDinheiro;
    private BigDecimal servicoDebito;
    private BigDecimal servicoCredito;
    private BigDecimal servicoPix;

    private VendaDTO venda;
}