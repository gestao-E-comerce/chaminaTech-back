package chaminaTech.Controller.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.DadosFiscais.CSTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cst")
public class CSTController {
    @Autowired
    CSTService cstService;

    @GetMapping("/lista")
    public List<CSTDTO> listarCSTS() {
        return cstService.listarCSTS();
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarCST(@RequestBody CSTDTO cstDTO) {
        try {
            return ResponseEntity.ok(cstService.cadastrarCST(cstDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarCST(@PathVariable Long id, @RequestBody CSTDTO cstDTO) {
        try {
            return ResponseEntity.ok(cstService.editarCST(id, cstDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCST(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cstService.deletarCST(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}