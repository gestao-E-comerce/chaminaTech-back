package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Estoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean deletado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_produto", nullable = false)
    @JsonIgnoreProperties(value = { "matriz" })
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private BigDecimal quantidade = BigDecimal.ZERO;

    @Column(name = "quantidade_vendido")
    private BigDecimal quantidadeVendido = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal = 0.0;

    @Column(name = "data_cadastrar")
    private Timestamp dataCadastrar;

    @Column(name = "data_desativar")
    private Timestamp dataDesativar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_matriz", nullable = false)
    @JsonIgnoreProperties(value = { "configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao",
            "configuracaoTaxaServico", }, allowSetters = true)
    private Matriz matriz;
}