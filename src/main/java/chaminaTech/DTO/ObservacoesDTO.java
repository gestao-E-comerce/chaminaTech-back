package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ObservacoesDTO {
    private Long id;

    private Boolean ativo = true;

    @JsonIgnoreProperties(value = {"produtos", "matriz","observacoes"})
    private CategoriaDTO categoria;

    private String observacao;

    private Double valor;

    private Boolean validarExestencia = false;

    private Boolean extra = false;

    @JsonIgnoreProperties("observacoes")
    private List<ObservacaoMateriaDTO> observacaoMaterias;

    @JsonIgnoreProperties("observacoes")
    private List<ObservacaoProdutoDTO> observacaoProdutos;
}