package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class SangriaDTO {
    private Long id;

    private Boolean ativo = true;

    private BigDecimal valor;

    private String motivo;

    private Timestamp dataSangria;

    private String nomeImpressora;

    private String tipo;

    private String nomeFuncionario;

    private CaixaDTO caixa;

    private FuncionarioDTO funcionario;
}
