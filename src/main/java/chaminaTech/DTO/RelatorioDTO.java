package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatorioDTO {
    private Long id;

    private String nome;
    private MatrizDTO matriz;
    private String tipoConsulta;

    private Boolean deletado;
    private Boolean ativo;
    private Long funcionarioId;
    private Long clienteId;
    private Boolean balcao;
    private Boolean retirada;
    private Boolean entrega;
    private Boolean mesa;
    private String dataInicio;
    private String dataFim;
    private Boolean taxaEntrega;
    private Boolean taxaServico;
    private Boolean desconto;
    private Boolean pix;
    private Boolean debito;
    private Boolean credito;
    private Boolean dinheiro;
    private String periodoDia;
    private String tipo;
    private Long caixaId;
    private String funcionarioNome;
    private Long produtoId;
    private Long materiaId;

    private String ordenacao;
    private String agrupamento = "DIA";
    private int pagina = 0;
    private int tamanho = 20;
}
