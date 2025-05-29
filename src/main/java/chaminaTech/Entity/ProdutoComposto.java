package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoComposto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "produto_composto_produto")
    @JsonIgnoreProperties("materiaProdutos")
    @JsonBackReference
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_composto_pc")
    private Produto produtoComposto;

    private BigDecimal quantidadeGasto = BigDecimal.ZERO;
}