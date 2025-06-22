package chaminaTech.DTO.ConfiguracaoDTO;

import chaminaTech.DTO.MatrizDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConfiguracaoTaxaServicoDTO {
    private Long id;

    private Boolean aplicar = false;

    private BigDecimal percentual;

    private BigDecimal valorFixo;

    private String tipo;

    private MatrizDTO matriz;
}