package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Observacoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observacoes_categoria",nullable = false)
    @JsonIgnoreProperties(value = {"produtos","matriz","observacoes"})
    private Categoria categoria;

    @Column(nullable = false)
    private String observacao;

    private Double valor;

    private Boolean validarExestencia = false;

    @Column(nullable = false)
    private Boolean extra = false;

    @OneToMany(mappedBy = "observacoes",cascade = CascadeType.ALL)
    @JsonIgnoreProperties("observacoes")
    private List<ObservacaoMateria> observacaoMaterias;

    @OneToMany(mappedBy = "observacoes",cascade = CascadeType.ALL)
    @JsonIgnoreProperties("observacoes")
    private List<ObservacaoProduto> observacaoProdutos;
}