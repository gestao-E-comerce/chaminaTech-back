package chaminaTech.DTO;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoEntregaDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class TaxaEntregaKmDTO {
    private Long id;

    private BigDecimal km;

    private BigDecimal valor;

    private Integer tempo;

    private ConfiguracaoEntregaDTO configuracaoEntrega;
}