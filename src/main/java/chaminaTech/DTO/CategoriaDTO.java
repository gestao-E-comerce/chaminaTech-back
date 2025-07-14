package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoriaDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private String nome;

    private Boolean obsObrigatotio = false;

    private Integer maxObs;

    private MatrizDTO matriz;

    private List<ObservacoesDTO> observacoesCategoria;
}