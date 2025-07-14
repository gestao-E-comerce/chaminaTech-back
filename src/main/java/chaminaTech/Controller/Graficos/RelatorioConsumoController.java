package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.VendaDTO;
import chaminaTech.Graficos.GraficoConsumo.GraficoTipoVendaConsumo;
import chaminaTech.Graficos.GraficoConsumo.GraficoValorTotalConsumo;
import chaminaTech.Service.Graficos.RelatorioConsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/consumo")
public class RelatorioConsumoController {
    @Autowired
    private RelatorioConsumoService relatorioConsumoService;

    @PostMapping("/gerarRelatorioConsumo")
    public ResponseEntity<Page<VendaDTO>> gerarRelatorioConsumo(@RequestBody RelatorioDTO relatorioDTO) {
        Page<VendaDTO> resultado = relatorioConsumoService.gerarRelatorioConsumo(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/graficoTipoVendaConsumo")
    public ResponseEntity<GraficoTipoVendaConsumo> gerarGraficoTipoVendaConsumo(@RequestBody RelatorioDTO dto) {
        return ResponseEntity.ok(relatorioConsumoService.gerarGraficoTipoVendaConsumo(dto));
    }

    @PostMapping("/graficoValorTotalConsumo")
    public List<GraficoValorTotalConsumo> gerarGraficoValorTotalConsumo(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioConsumoService.gerarGraficoValorTotalConsumo(relatorioDTO);
    }


}