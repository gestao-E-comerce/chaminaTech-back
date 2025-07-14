package chaminaTech.DTO;

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

    private PermissaoDTO permissao;
}