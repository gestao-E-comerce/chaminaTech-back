package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class ObservacaoMateria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "observacoes_materia_observacao")
    @JsonIgnoreProperties("observacaoMaterias")
    private Observacoes observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observacoes_materia_materia")
    private Materia materia;

    private BigDecimal quantidadeGasto = BigDecimal.ZERO;
}