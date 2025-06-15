package chaminaTech.Entity;

import chaminaTech.Entity.Configuracao.ConfiguracaoImpressao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Impressora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String apelidoImpressora;

    @Column(nullable = false)
    private String nomeImpressora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracao_impressao_id", nullable = false)
    @JsonIgnoreProperties("impressoras")
    private ConfiguracaoImpressao configuracaoImpressao;
}
