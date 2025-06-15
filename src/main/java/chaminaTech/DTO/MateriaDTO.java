package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MateriaDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private String nome;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;

    private Double quantidadeDisponivel;

    private Double quantidadeDescartada;
}