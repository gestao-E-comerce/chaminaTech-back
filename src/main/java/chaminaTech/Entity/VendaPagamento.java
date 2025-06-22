package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
public class VendaPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne
    @JoinColumn(name = "venda_id", nullable = false, unique = true)
    @JsonIgnoreProperties("vendaPagamento")
    private Venda venda;
}