package chaminaTech.DTO;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoEntregaDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TaxaEntregaKmDTO {
    private Long id;

    private Integer km;

    private Double valor;

    private Integer tempo;

    @JsonIgnoreProperties("taxasEntregaKm")
    private ConfiguracaoEntregaDTO configuracaoEntrega;
}