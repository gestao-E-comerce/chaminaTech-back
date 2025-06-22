package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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

    private BigDecimal latitude;

    private BigDecimal longitude;

    private ClienteDTO cliente;
}
