package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ObservacaoProdutoDTO {
    private Long id;

    private Boolean ativo = true;

    @JsonIgnoreProperties("observacaoProdutos")
    private ObservacoesDTO observacoes;

    private ProdutoDTO produto;

    private BigDecimal quantidadeGasto;
}