package chaminaTech.Graficos.GraficoConsumo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraficoTipoVendaConsumo {
    private String total;
    private Long mesa;
    private Long balcao;
    private Long entrega;
    private Long retirada;
}