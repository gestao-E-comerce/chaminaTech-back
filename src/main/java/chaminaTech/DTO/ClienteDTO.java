package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClienteDTO {
    private Long id;

    private Boolean ativo = true;

    private String nome;

    private String cpf;

    private String celular;

    private List<EnderecoDTO> enderecos;

    private MatrizDTO matriz;
}