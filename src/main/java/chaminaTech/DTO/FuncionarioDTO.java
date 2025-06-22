package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class FuncionarioDTO extends UsuarioDTO{

    private BigDecimal salario;

    private MatrizDTO matriz;

    private List<CaixaDTO> caixas;

    private String preferenciaImpressaoProdutoNovo;

    private String preferenciaImpressaoProdutoDeletado;
}