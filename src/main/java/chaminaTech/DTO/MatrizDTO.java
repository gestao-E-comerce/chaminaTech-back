package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import chaminaTech.DTO.ConfiguracaoDTO.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatrizDTO extends UsuarioDTO {
    @JsonIgnoreProperties("filhos")
    private MatrizDTO matriz;

    private String estado;

    private String cidade;

    private String bairro;

    private String cep;

    private String rua;

    private Integer numero;

    private Double latitude;

    private Double longitude;

    @JsonIgnoreProperties(value = {"matriz"}, allowSetters = true)
    private ConfiguracaoEntregaDTO configuracaoEntrega;

    @JsonIgnoreProperties(value = {"matriz"}, allowSetters = true)
    private ConfiguracaoRetiradaDTO configuracaoRetirada;

    @JsonIgnoreProperties(value = {"matriz"}, allowSetters = true)
    private ConfiguracaoImpressaoDTO configuracaoImpressao;

    @JsonIgnoreProperties(value = {"matriz"}, allowSetters = true)
    private ConfiguracaoTaxaServicoDTO configuracaoTaxaServicio;
}