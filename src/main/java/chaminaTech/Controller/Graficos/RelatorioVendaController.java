package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.VendaDTO;
import chaminaTech.Graficos.GraficoVenda.GraficoPagamentoVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoPeriodoVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoTipoVendaVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoValorTotalVenda;
import chaminaTech.Service.Graficos.RelatorioVendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/venda")
public class RelatorioVendaController {
    @Autowired
    private RelatorioVendaService relatorioVendaService;

    @PostMapping("/gerarRelatorioVenda")
    public ResponseEntity<Page<VendaDTO>> gerarRelatorioVenda(@RequestBody RelatorioDTO relatorioDTO) {
        Page<VendaDTO> resultado = relatorioVendaService.gerarRelatorioVenda(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/graficoValorTotalVenda")
    public List<GraficoValorTotalVenda> gerarGraficoValorTotalVenda(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioVendaService.gerarGraficoValorTotalVenda(relatorioDTO);
    }

    @PostMapping("/graficoPagamentoVenda")
    public ResponseEntity<GraficoPagamentoVenda> gerarGraficoPagamentoTotal(@RequestBody RelatorioDTO dto) {
        return ResponseEntity.ok(relatorioVendaService.gerarGraficoPagamentoTotalVenda(dto));
    }

    @PostMapping("/graficoTipoVendaVenda")
    public ResponseEntity<GraficoTipoVendaVenda> gerarGraficoTipoVenda(@RequestBody RelatorioDTO dto) {
        return ResponseEntity.ok(relatorioVendaService.gerarGraficoTipoVendaVenda(dto));
    }

    @PostMapping("/graficoPeriodoVenda")
    public ResponseEntity<GraficoPeriodoVenda> gerarGraficoPeriodo(@RequestBody RelatorioDTO dto) {
        return ResponseEntity.ok(relatorioVendaService.gerarGraficoVendasPeriodoVenda(dto));
    }
}