package chaminaTech.Controller.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSOSNDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.DadosFiscais.CSOSNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/csosn")
public class CSOSNController {
    @Autowired
    CSOSNService csosnService;

    @GetMapping("/lista")
    public List<CSOSNDTO> listarCsosn() {
        return csosnService.listarCsosn();
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarCSOSN(@RequestBody CSOSNDTO csosnDTO) {
        try {
            return ResponseEntity.ok(csosnService.cadastrarCSOSN(csosnDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarCSOSN(@PathVariable Long id, @RequestBody CSOSNDTO csosnDTO) {
        try {
            return ResponseEntity.ok(csosnService.editarCSOSN(id, csosnDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCSOSN(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(csosnService.deletarCSOSN(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}