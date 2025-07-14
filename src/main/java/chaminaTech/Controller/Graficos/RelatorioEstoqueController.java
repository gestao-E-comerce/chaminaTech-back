package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.EstoqueDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Graficos.GraficoEstoque.GraficoResumoEstoque;
import chaminaTech.Service.Graficos.RelatorioEstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/estoque")
public class RelatorioEstoqueController {
    @Autowired
    private RelatorioEstoqueService relatorioEstoqueService;

    @PostMapping("/gerarRelatorioEstoque")
    public ResponseEntity<Page<EstoqueDTO>> gerarRelatorioEstoque(@RequestBody RelatorioDTO relatorioDTO) {
        Page<EstoqueDTO> resultado = relatorioEstoqueService.gerarRelatorioEstoque(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }
    @PostMapping("/graficoResumoEstoque")
    public List<GraficoResumoEstoque> gerarGraficoResumoEstoque(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioEstoqueService.gerarGraficoResumoEstoque(relatorioDTO);
    }
}