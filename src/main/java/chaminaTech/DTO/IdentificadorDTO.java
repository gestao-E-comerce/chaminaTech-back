package chaminaTech.DTO;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoImpressaoDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentificadorDTO {

    private Long id;

    private String impressoraNome;

    private String identificadorNome;

    private ConfiguracaoImpressaoDTO configuracaoImpressao;
}