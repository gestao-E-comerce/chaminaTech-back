package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminDTO extends UsuarioDTO{
    private String chaveApiCoordenades;

    @JsonIgnoreProperties("matriz")
    private List<MatrizDTO> matrizs;

    @JsonIgnoreProperties("admin")
    private List<AdminFuncionarioDTO> subAdmins;
}
