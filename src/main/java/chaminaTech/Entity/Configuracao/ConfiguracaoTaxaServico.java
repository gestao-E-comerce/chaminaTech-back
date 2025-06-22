package chaminaTech.Entity.Configuracao;

import chaminaTech.Entity.Matriz;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class ConfiguracaoTaxaServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean aplicar = false;

    private BigDecimal percentual;

    private BigDecimal valorFixo;

    private String tipo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matriz_id", nullable = false)
    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico"})
    private Matriz matriz;
}