package chaminaTech.Controller;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> cadastrarRelatorio(@RequestBody RelatorioDTO relatorioDTO) {
        try {
            RelatorioDTO relatorioSalvo = relatorioService.cadastrarRelatorio(relatorioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(relatorioSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarRelatorio(@PathVariable Long id, @RequestBody RelatorioDTO relatorioDTO) {
        try {
            RelatorioDTO relatorioEditado = relatorioService.editarRelatorio(id, relatorioDTO);
            return ResponseEntity.ok(relatorioEditado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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