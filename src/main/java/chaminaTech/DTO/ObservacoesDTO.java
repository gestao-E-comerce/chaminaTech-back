package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class ObservacoesDTO {
    private Long id;

    private Boolean ativo = true;

    private CategoriaDTO categoria;

    private String observacao;

    private BigDecimal valor;

    private Boolean validarExestencia = false;

    private Boolean extra = false;

    private List<ObservacaoMateriaDTO> observacaoMaterias;

    private List<ObservacaoProdutoDTO> observacaoProdutos;
}