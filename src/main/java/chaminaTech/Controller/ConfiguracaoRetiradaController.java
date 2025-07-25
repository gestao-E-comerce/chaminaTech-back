package chaminaTech.Controller;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoRetiradaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.ConfiguracaoRetiradaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/confRetirada")
public class ConfiguracaoRetiradaController {
    @Autowired
    private ConfiguracaoRetiradaService configuracaoRetiradaService;

    @GetMapping("/{matrizId}")
    public ResponseEntity<ConfiguracaoRetiradaDTO> listarPorMatriz(@PathVariable Long matrizId) {
        ConfiguracaoRetiradaDTO configuracaoRetiradaDTO = configuracaoRetiradaService.buscarConfiguracaoRetirada(matrizId);
        return ResponseEntity.ok(configuracaoRetiradaDTO);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> salvar(@RequestBody ConfiguracaoRetiradaDTO configuracaoRetiradaDTO) {
        try {
            MensagemDTO resposta = configuracaoRetiradaService.salvarConfiguracaoRetirada(configuracaoRetiradaDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
}
