package chaminaTech.DTO.ConfiguracaoDTO;

import chaminaTech.DTO.MatrizDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracaoRetiradaDTO {
    private Long id;

    private Integer tempoEstimadoRetidara;

    private MatrizDTO matriz;
}
