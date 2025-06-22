package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class CaixaDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private BigDecimal valorAbertura;

    private BigDecimal saldoDinheiro;

    private BigDecimal saldoCredito;

    private BigDecimal saldoDebito;

    private BigDecimal saldoPix;

    private BigDecimal saldo;

    private Timestamp dataAbertura;

    private Timestamp dataFechamento;

    private String nomeImpressora;

    private FuncionarioDTO funcionario;

    private MatrizDTO matriz;

    private List<VendaDTO> vendas;

    private List<GorjetaDTO> gorjetas;

    private List<SangriaDTO> sangrias;

    private List<SuprimentoDTO> suprimentos;
}
