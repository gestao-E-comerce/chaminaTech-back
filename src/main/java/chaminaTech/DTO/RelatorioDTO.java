package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RelatorioDTO {
    private Long id;

    private String nome;
    private MatrizDTO matriz;
    private String tipoConsulta;

    private Boolean deletado;
    private Long funcionarioId;
    private List<String> tiposVenda;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Boolean taxaEntrega;
    private Boolean taxaServico;
    private Boolean desconto;
    private List<String> formasPagamento;

    private String ordenacao;
    private int pagina = 0;
    private int tamanho = 10;
}
