package Ecomerce.assmar.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
public class ProdutoVenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;

    @Column(nullable = false)
    private BigDecimal quantidade = BigDecimal.ZERO;

    private Double valor;

    @Column(nullable = false)
    private Timestamp data;

    private String observacaoProdutoVenda;

    private String motivoExclusao;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "produto_venda_observacoes")
    private List<Observacoes> observacoesProdutoVenda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "produto_venda_venda",nullable = false)
    @JsonIgnoreProperties(value = {"cliente","endereco","produtoVendas","funcionario","caixa","matriz","vendaPagamentos"})
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "produto_venda_produto",nullable = false)
    @JsonIgnoreProperties(value = {"matriz"})
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "produto_funcionario",nullable = false)
    @JsonIgnoreProperties(value = {"matriz","caixas"})
    private Funcionario funcionario;
}