package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ObservacaoProdutoDTO {
    private Long id;

    private Boolean ativo = true;

    private ObservacoesDTO observacoes;

    private ProdutoDTO produto;

    private BigDecimal quantidadeGasto;
}