package chaminaTech.DTO.ConfiguracaoDTO;

import chaminaTech.DTO.MatrizDTO;
import chaminaTech.DTO.TaxaEntregaKmDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfiguracaoEntregaDTO {
    private Long id;

    private Integer calcular = 0;

    private List<TaxaEntregaKmDTO> taxasEntregaKm;

    private MatrizDTO matriz;
}