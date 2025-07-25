package chaminaTech.Controller.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTIPIDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.DadosFiscais.CSTIPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cstIpi")
public class CSTIPIController {
    @Autowired
    CSTIPIService cstipiService;

    @GetMapping("/lista")
    public List<CSTIPIDTO> listarCSTIPIS() {
        return cstipiService.listarCSTIPIS();
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarCSTIPI(@RequestBody CSTIPIDTO cstipiDTO) {
        try {
            return ResponseEntity.ok(cstipiService.cadastrarCSTIPI(cstipiDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarCSTIPI(@PathVariable Long id, @RequestBody CSTIPIDTO cstipiDTO) {
        try {
            return ResponseEntity.ok(cstipiService.editarCSTIPI(id, cstipiDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCSTIPI(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cstipiService.deletarCSTIPI(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}