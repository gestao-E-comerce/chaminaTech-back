package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.CaixaDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Graficos.GraficoCaixa.GraficoPagamentoCaixa;
import chaminaTech.Graficos.GraficoCaixa.GraficoResumoCaixa;
import chaminaTech.Service.Graficos.RelatorioCaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/caixa")
public class RelatorioCaixaController {
    @Autowired
    private RelatorioCaixaService relatorioCaixaService;

    @PostMapping("/gerarRelatorioCaixa")
    public ResponseEntity<Page<CaixaDTO>> gerarRelatorioCaixa(@RequestBody RelatorioDTO relatorioDTO) {
        Page<CaixaDTO> resultado = relatorioCaixaService.gerarRelatorioCaixa(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/graficoResumoCaixa")
    public List<GraficoResumoCaixa> gerarGraficoResumoCaixa(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioCaixaService.gerarGraficoResumoCaixa(relatorioDTO);
    }

    @PostMapping("/graficoComposicaoSaldoCaixa")
    public List<GraficoPagamentoCaixa> gerarGraficoComposicaoSaldo(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioCaixaService.gerarGraficoComposicaoSaldo(relatorioDTO);
    }
}