package chaminaTech.Controller;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoImpressaoDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.ConfiguracaoImpressaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/confImpressao")
public class ConfiguracaoImpressaoController {
    @Autowired
    private ConfiguracaoImpressaoService configuracaoImpressaoService;

    @GetMapping("/{matrizId}")
    public ResponseEntity<ConfiguracaoImpressaoDTO> listarPorMatriz(@PathVariable Long matrizId) {
        ConfiguracaoImpressaoDTO configuracaoImpressaoDTO = configuracaoImpressaoService.buscarConfiguracaoImpressao(matrizId);
        return ResponseEntity.ok(configuracaoImpressaoDTO);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> salvar(@RequestBody ConfiguracaoImpressaoDTO configuracaoImpressaoDTO) {
        try {
            MensagemDTO resposta = configuracaoImpressaoService.salvarConfiguracaoImpressao(configuracaoImpressaoDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
}
