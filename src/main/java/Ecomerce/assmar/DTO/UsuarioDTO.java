package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private String nome;

    private String cnpj;

    private String username;

    private String celular;

    private String email;

    private String password;

    private String token;

    private String role;

    @JsonIgnoreProperties("matriz")
    private PermissaoDTO permissao;
}