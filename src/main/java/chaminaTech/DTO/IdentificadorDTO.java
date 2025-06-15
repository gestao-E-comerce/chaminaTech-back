package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoImpressaoDTO;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentificadorDTO {

    private Long id;

    @Column(nullable = false)
    private String impressoraNome;

    @Column(nullable = false)
    private String identificadorNome;

    @JsonIgnoreProperties("identificador")
    private ConfiguracaoImpressaoDTO configuracaoImpressao;
}