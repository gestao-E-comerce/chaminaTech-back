package chaminaTech.Controller.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTCOFINSDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.DadosFiscais.CSTCOFINSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cstCofins")
public class CSTCOFINSController {
    @Autowired
    CSTCOFINSService cstcofinsService;

    @GetMapping("/lista")
    public List<CSTCOFINSDTO> listarCSTCOFINSS() {
        return cstcofinsService.listarCSTCOFINSS();
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarCSTCOFINS(@RequestBody CSTCOFINSDTO cstcofinsDTO) {
        try {
            return ResponseEntity.ok(cstcofinsService.cadastrarCSTCOFINS(cstcofinsDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarCSTCOFINS(@PathVariable Long id, @RequestBody CSTCOFINSDTO cstcofinsDTO) {
        try {
            return ResponseEntity.ok(cstcofinsService.editarCSTCOFINS(id, cstcofinsDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCSTCOFINS(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cstcofinsService.deletarCSTCOFINS(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}