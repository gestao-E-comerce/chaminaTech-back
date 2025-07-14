package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.DepositoDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Graficos.GraficoDeposito.GraficoResumoDeposito;
import chaminaTech.Service.Graficos.RelatorioDepositoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/deposito")
public class RelatorioDepositoController {
    @Autowired
    private RelatorioDepositoService relatorioDepositoService;

    @PostMapping("/gerarRelatorioDeposito")
    public ResponseEntity<Page<DepositoDTO>> gerarRelatorioDeposito(@RequestBody RelatorioDTO relatorioDTO) {
        Page<DepositoDTO> resultado = relatorioDepositoService.gerarRelatorioDeposito(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }
    @PostMapping("/graficoResumoDeposito")
    public List<GraficoResumoDeposito> gerarGraficoResumoDeposito(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioDepositoService.gerarGraficoResumoDeposito(relatorioDTO);
    }
}