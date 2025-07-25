package chaminaTech.Controller.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.ModalidadeBaseCalculoIcmsDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.DadosFiscais.ModalidadeBaseCalculoIcmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modalidadeBaseCalculoIcms")
public class ModalidadeBaseCalculoIcmsController {
    @Autowired
    ModalidadeBaseCalculoIcmsService modalidadeBaseCalculoIcmsService;

    @GetMapping("/lista")
    public List<ModalidadeBaseCalculoIcmsDTO> listarModalidadeBaseCalculoIcmss() {
        return modalidadeBaseCalculoIcmsService.listarModalidadeBaseCalculoIcmss();
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarModalidadeBaseCalculoIcms(@RequestBody ModalidadeBaseCalculoIcmsDTO modalidadeBaseCalculoIcmsDTO) {
        try {
            return ResponseEntity.ok(modalidadeBaseCalculoIcmsService.cadastrarModalidadeBaseCalculoIcms(modalidadeBaseCalculoIcmsDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarModalidadeBaseCalculoIcms(@PathVariable Long id, @RequestBody ModalidadeBaseCalculoIcmsDTO modalidadeBaseCalculoIcmsDTO) {
        try {
            return ResponseEntity.ok(modalidadeBaseCalculoIcmsService.editarModalidadeBaseCalculoIcms(id, modalidadeBaseCalculoIcmsDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarModalidadeBaseCalculoIcms(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(modalidadeBaseCalculoIcmsService.deletarModalidadeBaseCalculoIcms(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}