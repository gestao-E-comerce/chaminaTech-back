package chaminaTech.Controller;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoTaxaServicoDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.ConfiguracaoTaxaServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/confTaxaServico")
public class ConfiguracaoTaxaServicoController {
    @Autowired
    private ConfiguracaoTaxaServicoService configuracaoTaxaServicoService;

    @GetMapping("/{matrizId}")
    public ResponseEntity<ConfiguracaoTaxaServicoDTO> listarPorMatriz(@PathVariable Long matrizId) {
        ConfiguracaoTaxaServicoDTO configuracaoTaxaServicoDTO = configuracaoTaxaServicoService.buscarConfiguracaoTaxaServico(matrizId);
        return ResponseEntity.ok(configuracaoTaxaServicoDTO);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> salvar(@RequestBody ConfiguracaoTaxaServicoDTO configuracaoTaxaServicoDTO) {
        try {
            MensagemDTO resposta = configuracaoTaxaServicoService.salvarConfiguracaoTaxaServico(configuracaoTaxaServicoDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
}
