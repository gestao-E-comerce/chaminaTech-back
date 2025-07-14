package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProdutoCompostoDTO {
    private Long id;

    private Boolean ativo = true;

    private ProdutoDTO produto;

    private ProdutoDTO produtoComposto;

    private BigDecimal quantidadeGasto;
}
