package chaminaTech.DTO;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoImpressaoDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImpressoraDTO {
    private Long id;

    private String apelidoImpressora;

    private String nomeImpressora;

    private ConfiguracaoImpressaoDTO configuracaoImpressao;
}