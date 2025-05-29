package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ObservacaoMateriaDTO {
    private Long id;

    private Boolean ativo = true;

    @JsonIgnoreProperties("observacaoMaterias")
    private ObservacoesDTO observacoes;

    private MateriaDTO materia;

    private BigDecimal quantidadeGasto;
}