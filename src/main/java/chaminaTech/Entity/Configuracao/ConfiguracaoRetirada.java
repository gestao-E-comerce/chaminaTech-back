package chaminaTech.Entity.Configuracao;

import chaminaTech.Entity.Matriz;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ConfiguracaoRetirada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer tempoEstimadoRetidara;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matriz_id", nullable = false)
    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico"})
    private Matriz matriz;
}
