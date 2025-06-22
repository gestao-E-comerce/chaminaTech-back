package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Gorjeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;

    private BigDecimal dinheiro;

    private BigDecimal debito;

    private BigDecimal credito;

    private BigDecimal pix;

    private String nomeImpressora;

    @Column(nullable = false)
    private Timestamp dataGorjeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sangria_caixa", nullable = false)
    @JsonIgnoreProperties(value = {"vendas","funcionario","sangrias","suprimentos","gorjetas"})
    private Caixa caixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "sangria_funcionario", nullable = false)
    @JsonIgnoreProperties(value = {"matriz","caixas"})
    private Funcionario funcionario;
}
