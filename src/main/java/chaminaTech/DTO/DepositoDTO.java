package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter @Setter

public class DepositoDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    @JsonIgnoreProperties(value = {"matriz"})
    private MateriaDTO materia;

    private BigDecimal quantidade;

    private BigDecimal quantidadeVendido;

    private Double valorTotal = 0.0;

    private Timestamp dataCadastrar;

    private Timestamp dataDesativar;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;
}