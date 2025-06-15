package chaminaTech.Entity;

import chaminaTech.Entity.Configuracao.ConfiguracaoEntrega;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class TaxaEntregaKm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer km;

    private Double valor;

    private Integer tempo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracao_entrega_id", nullable = false)
    @JsonIgnoreProperties("taxasEntregaKm")
    private ConfiguracaoEntrega configuracaoEntrega;
}