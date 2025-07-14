package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ProdutoVendaDTO {
    private Long id;

    private Boolean ativo = true;

    private BigDecimal quantidade;

    private BigDecimal valor;

    private Timestamp data;

    private String observacaoProdutoVenda;

    private String motivoExclusao;

    private int origemTransferenciaNumero;

    private List<ObservacoesDTO> observacoesProdutoVenda;

    private VendaDTO venda;

    private ProdutoDTO produto;

    private FuncionarioDTO funcionario;
}