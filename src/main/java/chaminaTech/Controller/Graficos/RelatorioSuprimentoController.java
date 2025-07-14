package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.SuprimentoDTO;
import chaminaTech.Graficos.GraficoSuprimento.GraficoTotalSuprimento;
import chaminaTech.Service.Graficos.RelatorioSuprimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/suprimento")
public class RelatorioSuprimentoController {
    @Autowired
    private RelatorioSuprimentoService relatorioSuprimentoService;

    @PostMapping("/gerarRelatorioSuprimento")
    public ResponseEntity<Page<SuprimentoDTO>> gerarRelatorioSuprimento(@RequestBody RelatorioDTO relatorioDTO) {
        Page<SuprimentoDTO> resultado = relatorioSuprimentoService.gerarRelatorioSuprimento(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/graficoTotalSuprimento")
    public List<GraficoTotalSuprimento> gerarGraficoTotalSuprimentoCaixa(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioSuprimentoService.gerarGraficoTotalSuprimentoPorCaixa(relatorioDTO);
    }
}