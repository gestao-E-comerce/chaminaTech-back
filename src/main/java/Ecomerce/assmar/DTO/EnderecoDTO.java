package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoDTO {
    private Long id;

    private Boolean ativo = true;

    private String estado;

    private String cidade;

    private String bairro;

    private String cep;

    private String rua;

    private Integer numero;

    private String complemento;

    private String referencia;

    private Double latitude;

    private Double longitude;

    @JsonIgnoreProperties(value = {"enderecos","matriz"})
    private ClienteDTO cliente;
}
