package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(value = {"produtos"}, allowGetters = true)
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean deletado = false;

    @Column(length = 100, nullable = false)
    private String nome;

    private Boolean obsObrigatotio = false;

    private Integer maxObs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_matriz",nullable = false)
    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private Matriz matriz;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"produtoVenda","categoria"}, allowSetters = true)
    private List<Observacoes> observacoesCategoria;
}