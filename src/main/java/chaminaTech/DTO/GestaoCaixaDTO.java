package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GestaoCaixaDTO {
    private Long id;

    private Boolean ativo = true;

    private Integer cupom;

    private VendaDTO venda;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;
}
