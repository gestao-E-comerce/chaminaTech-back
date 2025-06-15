package chaminaTech.Entity.Configuracao;

import chaminaTech.Entity.Matriz;
import chaminaTech.Entity.TaxaEntregaKm;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class ConfiguracaoEntrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer calcular = 0;

    @OneToMany(mappedBy = "configuracaoEntrega", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("configuracaoEntrega")
    @OrderBy("km")
    private List<TaxaEntregaKm> taxasEntregaKm;

    @OneToOne
    @JoinColumn(name = "matriz_id", nullable = false)
    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico"})
    private Matriz matriz;
}