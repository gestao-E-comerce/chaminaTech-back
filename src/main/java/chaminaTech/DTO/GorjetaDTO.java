package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class GorjetaDTO {
    private Long id;

    private Boolean ativo = true;

    private BigDecimal dinheiro;

    private BigDecimal debito;

    private BigDecimal credito;

    private BigDecimal pix;

    private String nomeImpressora;

    private Timestamp dataGorjeta;

    private CaixaDTO caixa;

    private FuncionarioDTO funcionario;
}
