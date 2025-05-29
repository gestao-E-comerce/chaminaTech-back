package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminFuncionarioDTO extends UsuarioDTO{
    @JsonIgnoreProperties("subAdmins")
    private AdminDTO admin;
}
