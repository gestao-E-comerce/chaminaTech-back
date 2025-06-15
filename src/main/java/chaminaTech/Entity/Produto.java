package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean deletado = false;

    @Column(nullable = false)
    private Boolean cardapio = true;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private Boolean tipo;

    @Column(nullable = false)
    private Integer codigo;

    @Column(nullable = false)
    private Boolean validarExestencia = false;

    @Column(nullable = false)
    private Boolean estocavel = false;

    @Column(nullable = false)
    private Boolean deveImprimir = false;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("produto")
    private List<ProdutoMateria> produtoMaterias;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("produto")
    @JsonManagedReference
    private List<ProdutoComposto> produtoCompostos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_matriz")
    @JsonIgnoreProperties(value = { "configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao",
            "configuracaoTaxaServico", }, allowSetters = true)
    private Matriz matriz;

    @ManyToOne
    @JoinColumn(name = "produto_categoria")
    @JsonIgnoreProperties(value = { "produtos", "matriz" }, allowSetters = true)
    private Categoria categoria;

    @ManyToMany
    @JoinTable(name = "produto_impressoras", joinColumns = @JoinColumn(name = "produto_id"), inverseJoinColumns = @JoinColumn(name = "impressora_id"))
    @JsonIgnoreProperties("matriz")
    private List<Impressora> impressoras;

    private Double quantidadeDisponivel;

    private Double quantidadeDescartada;
}