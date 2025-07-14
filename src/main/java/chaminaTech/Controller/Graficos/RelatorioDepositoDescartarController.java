package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.DepositoDescartarDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Graficos.GraficoDepositoDescartar.GraficoResumoDepositoDescartar;
import chaminaTech.Service.Graficos.RelatorioDepositoDescartarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/depositoDescartar")
public class RelatorioDepositoDescartarController {
    @Autowired
    private RelatorioDepositoDescartarService relatorioDepositoDescartarService;

    @PostMapping("/gerarRelatorioDepositoDescartar")
    public ResponseEntity<Page<DepositoDescartarDTO>> gerarRelatorioDepositoDescartar(@RequestBody RelatorioDTO relatorioDTO) {
        Page<DepositoDescartarDTO> resultado = relatorioDepositoDescartarService.gerarRelatorioDepositoDescartar(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }
    @PostMapping("/graficoResumoDepositoDescartar")
    public List<GraficoResumoDepositoDescartar> gerarGraficoResumoDepositoDescartar(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioDepositoDescartarService.gerarGraficoResumoDepositoDescartar(relatorioDTO);
    }
}