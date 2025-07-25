package chaminaTech.Controller.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.OrigemDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.DadosFiscais.OrigemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/origem")
public class OrigemController {
    @Autowired
    OrigemService origemService;

    @GetMapping("/lista")
    public List<OrigemDTO> listarOrigens() {
        return origemService.listarOrigens();
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarOrigem(@RequestBody OrigemDTO origemDTO) {
        try {
            return ResponseEntity.ok(origemService.cadastrarOrigem(origemDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarOrigem(@PathVariable Long id, @RequestBody OrigemDTO origemDTO) {
        try {
            return ResponseEntity.ok(origemService.editarOrigem(id, origemDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarOrigem(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(origemService.deletarOrigem(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}