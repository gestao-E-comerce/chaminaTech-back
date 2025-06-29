package chaminaTech.Controller;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio")
@CrossOrigin(origins = "*")
public class RelatorioController {
    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/lista/{matrizId}")
    public ResponseEntity<List<RelatorioDTO>> listarRelatorios(@PathVariable Long matrizId) {
        List<RelatorioDTO> resultado = relatorioService.listarRelatorios(matrizId);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarRelatorio(@RequestBody RelatorioDTO relatorioDTO) {
        try {
            return ResponseEntity.ok(relatorioService.cadastrarRelatorio(relatorioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PostMapping("/gerarRelatorio")
    public ResponseEntity<Page<?>> gerarRelatorio(@RequestBody RelatorioDTO relatorioDTO) {
        Page<?> resultado = relatorioService.gerarRelatorio(relatorioDTO);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarRelatorio(@PathVariable Long id, @RequestBody RelatorioDTO relatorioDTO) {
        try {
            return ResponseEntity.ok(relatorioService.editarRelatorio(id, relatorioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarRelatorio(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(relatorioService.deletarRelatorio(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}