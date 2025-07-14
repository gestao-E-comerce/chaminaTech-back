package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminDTO extends UsuarioDTO{
    private String chaveApiCoordenades;

    private List<MatrizDTO> matrizs;

    private List<AdminFuncionarioDTO> subAdmins;
}
