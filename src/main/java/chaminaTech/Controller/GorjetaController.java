package chaminaTech.Controller;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.GorjetaDTO;
import chaminaTech.Service.GorjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gorjeta")
@CrossOrigin(origins = "*")
public class GorjetaController {

    @Autowired
    private GorjetaService gorjetaService;
    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarGorjeta(@RequestBody GorjetaDTO gorjetaDTO) {
        try {
            MensagemDTO resposta = gorjetaService.cadastrarGorjeta(gorjetaDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarGorjeta(@PathVariable Long id, @RequestBody GorjetaDTO gorjetaDTO) {
        try {
            return ResponseEntity.ok(gorjetaService.editarGorjeta(id, gorjetaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarGorjeta(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(gorjetaService.deletarGorjeta(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}
