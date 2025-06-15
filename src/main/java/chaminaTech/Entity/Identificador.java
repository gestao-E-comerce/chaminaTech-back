package chaminaTech.Entity;

import chaminaTech.Entity.Configuracao.ConfiguracaoImpressao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Identificador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String impressoraNome;
    
    @Column(nullable = false)
    private String identificadorNome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracao_impressao_id", nullable = false)
    @JsonIgnoreProperties("identificador")
    private ConfiguracaoImpressao configuracaoImpressao;
}