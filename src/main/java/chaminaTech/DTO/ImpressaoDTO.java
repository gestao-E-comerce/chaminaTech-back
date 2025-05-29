package chaminaTech.DTO;

import lombok.Getter;

import java.util.List;

@Getter
public class ImpressaoDTO {
    private VendaDTO venda;
    private List<ProdutoVendaDTO> produtos;
    private Integer quantedade;
}
