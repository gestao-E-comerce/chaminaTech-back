package chaminaTech.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Relatorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_matriz", nullable = false)
    private Matriz matriz;

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
