package chaminaTech.Graficos.GraficoVenda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoPeriodoVenda {
    private String periodo;
    private Long madrugada;
    private Long manha;
    private Long tarde;
    private Long noite;
}