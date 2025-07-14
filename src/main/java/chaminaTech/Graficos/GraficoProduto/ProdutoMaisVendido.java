package chaminaTech.Graficos.GraficoProduto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoMaisVendido {
    private String produtoNome;
    private Long totalVendas;
    private BigDecimal quantidadeTotal;
    private BigDecimal valorTotal;
}
