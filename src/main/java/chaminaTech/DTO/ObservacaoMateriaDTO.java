package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ObservacaoMateriaDTO {
    private Long id;

    private Boolean ativo = true;

    private ObservacoesDTO observacoes;

    private MateriaDTO materia;

    private BigDecimal quantidadeGasto;
}