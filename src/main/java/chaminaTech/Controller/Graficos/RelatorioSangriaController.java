package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.SangriaDTO;
import chaminaTech.Graficos.GraficoSangria.GraficoTipoSangria;
import chaminaTech.Graficos.GraficoSangria.GraficoTotalSangria;
import chaminaTech.Service.Graficos.RelatorioSangriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/sangria")
public class RelatorioSangriaController {
    @Autowired
    private RelatorioSangriaService relatorioSangriaService;

    @PostMapping("/gerarRelatorioSangria")
    public ResponseEntity<Page<SangriaDTO>> gerarRelatorioSangria(@RequestBody RelatorioDTO relatorioDTO) {
        Page<SangriaDTO> resultado = relatorioSangriaService.gerarRelatorioSangria(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/graficoTipoSangria")
    public List<GraficoTipoSangria> gerarGraficoTipoSangria(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioSangriaService.gerarGraficoTipoSangria(relatorioDTO);
    }

    @PostMapping("/graficoTotalSangria")
    public List<GraficoTotalSangria> gerarGraficoTotalSangriaCaixa(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioSangriaService.gerarGraficoTotalSangriaPorCaixa(relatorioDTO);
    }
}