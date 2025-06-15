package chaminaTech.Controller;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoEntregaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.ConfiguracaoEntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/confEntrega")
@CrossOrigin(origins = "*")
public class ConfiguracaoEntregaController {
    @Autowired
    private ConfiguracaoEntregaService configuracaoEntregaService;

    @GetMapping("/{matrizId}")
    public ResponseEntity<ConfiguracaoEntregaDTO> buscarConfiguracaoEntrega(@PathVariable Long matrizId) {
        ConfiguracaoEntregaDTO configuracaoEntregaDTO = configuracaoEntregaService.buscarConfiguracaoEntrega(matrizId);
        return ResponseEntity.ok(configuracaoEntregaDTO);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> salvarLista(@RequestBody ConfiguracaoEntregaDTO configuracaoEntregaDTO) {
        try {
            MensagemDTO resposta = configuracaoEntregaService.salvarConfiguracaoEntrega(configuracaoEntregaDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
}
