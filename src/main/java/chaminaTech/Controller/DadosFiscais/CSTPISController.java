package chaminaTech.Controller.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTPISDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.DadosFiscais.CSTPISService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cstPis")
public class CSTPISController {
    @Autowired
    CSTPISService cstpisService;

    @GetMapping("/lista")
    public List<CSTPISDTO> listarCSTPISS() {
        return cstpisService.listarCSTPISS();
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarCSTPIS(@RequestBody CSTPISDTO cstpisDTO) {
        try {
            return ResponseEntity.ok(cstpisService.cadastrarCSTPIS(cstpisDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarCSTPIS(@PathVariable Long id, @RequestBody CSTPISDTO cstpisDTO) {
        try {
            return ResponseEntity.ok(cstpisService.editarCSTPIS(id, cstpisDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCSTPIS(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cstpisService.deletarCSTPIS(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}