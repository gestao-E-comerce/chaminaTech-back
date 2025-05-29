package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class ObservacaoProduto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn( name = "observacao_produto_observacao", nullable = false)
    @JsonIgnoreProperties("observacaoProdutos")
    private Observacoes observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observacao_produto_produto")
    private Produto produto;

    private BigDecimal quantidadeGasto = BigDecimal.ZERO;
}
