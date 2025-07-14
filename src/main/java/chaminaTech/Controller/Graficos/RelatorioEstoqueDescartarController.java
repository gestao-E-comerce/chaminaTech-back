package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.EstoqueDescartarDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Graficos.GraficoEstoqueDescartar.GraficoResumoEstoqueDescartar;
import chaminaTech.Service.Graficos.RelatorioEstoqueDescartarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/estoqueDescartar")
public class RelatorioEstoqueDescartarController {
    @Autowired
    private RelatorioEstoqueDescartarService relatorioEstoqueDescartarService;

    @PostMapping("/gerarRelatorioEstoqueDescartar")
    public ResponseEntity<Page<EstoqueDescartarDTO>> gerarRelatorioEstoqueDescartar(@RequestBody RelatorioDTO relatorioDTO) {
        Page<EstoqueDescartarDTO> resultado = relatorioEstoqueDescartarService.gerarRelatorioEstoqueDescartar(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }
    @PostMapping("/graficoResumoEstoqueDescartar")
    public List<GraficoResumoEstoqueDescartar> gerarGraficoResumoEstoqueDescartar(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioEstoqueDescartarService.gerarGraficoResumoEstoqueDescartar(relatorioDTO);
    }
}