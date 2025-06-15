package chaminaTech.DTO.ConfiguracaoDTO;

import chaminaTech.DTO.MatrizDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracaoRetiradaDTO {
    private Long id;

    private Integer tempoEstimadoRetidara;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico"})
    private MatrizDTO matriz;
}
