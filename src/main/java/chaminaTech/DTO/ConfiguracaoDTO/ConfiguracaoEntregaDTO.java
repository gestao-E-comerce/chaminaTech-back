package chaminaTech.DTO.ConfiguracaoDTO;

import chaminaTech.DTO.MatrizDTO;
import chaminaTech.DTO.TaxaEntregaKmDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfiguracaoEntregaDTO {
    private Long id;

    private Integer calcular = 0;

    @JsonIgnoreProperties("configuracaoEntrega")
    private List<TaxaEntregaKmDTO> taxasEntregaKm;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;
}