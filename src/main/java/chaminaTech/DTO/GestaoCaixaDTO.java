package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GestaoCaixaDTO {
    private Long id;

    private Boolean ativo = true;

    private Integer cupom;

    private VendaDTO venda;

    private MatrizDTO matriz;
}
