package chaminaTech.Controller;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.SangriaDTO;
import chaminaTech.Service.SangriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sangria")
@CrossOrigin(origins = "*")
public class SangriaController {
    @Autowired
    private SangriaService sangriaService;
    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarSangria(@RequestBody SangriaDTO sangriaDTO) {
        try {
            MensagemDTO resposta = sangriaService.cadastrarSangria(sangriaDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), org.springframework.http.HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarSangria(@PathVariable Long id, @RequestBody SangriaDTO sangriaDTO) {
        try {
            return ResponseEntity.ok(sangriaService.editarSangria(id, sangriaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarSangria(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sangriaService.deletarSangria(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}
