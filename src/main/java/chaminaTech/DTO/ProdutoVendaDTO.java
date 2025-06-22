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

//    @JsonIgnoreProperties(value = {"cliente","endereco","produtoVendas","funcionario","caixa","matriz","vendaPagamentos"})
    private VendaDTO venda;

//    @JsonIgnoreProperties(value = {"matriz"})
    private ProdutoDTO produto;

//    @JsonIgnoreProperties(value = {"matriz","caixas"})
    private FuncionarioDTO funcionario;
}