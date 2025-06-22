package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class ObservacoesDTO {
    private Long id;

    private Boolean ativo = true;

    @JsonIgnoreProperties(value = {"produtos", "matriz","observacoes"})
    private CategoriaDTO categoria;

    private String observacao;

    private BigDecimal valor;

    private Boolean validarExestencia = false;

    private Boolean extra = false;

    @JsonIgnoreProperties("observacoes")
    private List<ObservacaoMateriaDTO> observacaoMaterias;

    @JsonIgnoreProperties("observacoes")
    private List<ObservacaoProdutoDTO> observacaoProdutos;
}