package chaminaTech.Controller;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.SuprimentoDTO;
import chaminaTech.Service.SuprimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suprimento")
@CrossOrigin(origins = "http://localhost:4200")
public class SuprimentoController {

    @Autowired
    private SuprimentoService suprimentoService;
    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarSuprimento(@RequestBody SuprimentoDTO suprimentoDTO) {
        try {
            MensagemDTO resposta = suprimentoService.cadastrarSuprimento(suprimentoDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), org.springframework.http.HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarSuprimento(@PathVariable Long id, @RequestBody SuprimentoDTO suprimentoDTO) {
        try {
            return ResponseEntity.ok(suprimentoService.editarSuprimento(id, suprimentoDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarSuprimento(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(suprimentoService.deletarSuprimento(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}
