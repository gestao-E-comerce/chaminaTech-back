package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;

    @JsonIgnoreProperties(value = {"produtoVenda","categoria"}, allowSetters = true)
    private List<ObservacoesDTO> observacoesCategoria;
}