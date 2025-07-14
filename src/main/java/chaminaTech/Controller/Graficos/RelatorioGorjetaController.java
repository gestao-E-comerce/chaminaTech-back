package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.GorjetaDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Graficos.GraficoGorjeta.GraficoPagamentoGorjeta;
import chaminaTech.Graficos.GraficoGorjeta.GraficoTotalGorjeta;
import chaminaTech.Service.Graficos.RelatorioGorjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/gorjeta")
public class RelatorioGorjetaController {
    @Autowired
    private RelatorioGorjetaService relatorioGorjetaService;

    @PostMapping("/gerarRelatorioGorjeta")
    public ResponseEntity<Page<GorjetaDTO>> gerarRelatorioGorjeta(@RequestBody RelatorioDTO relatorioDTO) {
        Page<GorjetaDTO> resultado = relatorioGorjetaService.gerarRelatorioGorjeta(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/graficoPagamentoGorjeta")
    public GraficoPagamentoGorjeta gerarGraficoPagamentoGorjeta(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioGorjetaService.gerarGraficoPagamentoGorjeta(relatorioDTO);
    }

    @PostMapping("/graficoTotalGorjeta")
    public List<GraficoTotalGorjeta> gerarGraficoTotalGorjetaCaixa(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioGorjetaService.gerarGraficoTotalGorjetaPorCaixa(relatorioDTO);
    }
}