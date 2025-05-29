package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendaPagamentoDTO {

    private Long id;

    private Double dinheiro;

    private Double debito;

    private Double credito;

    private Double pix;

    @JsonIgnoreProperties("vendaPagamento")
    private VendaDTO venda;
}