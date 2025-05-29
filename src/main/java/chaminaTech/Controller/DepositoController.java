package chaminaTech.Controller;

import chaminaTech.Service.DepositoService;
import chaminaTech.DTO.DepositoDTO;
import chaminaTech.DTO.DepositoDescartarDTO;
import chaminaTech.DTO.MensagemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deposito")
@CrossOrigin(origins = "http://localhost:4200")
public class DepositoController {
    @Autowired
    private DepositoService depositoService;

    @GetMapping("/lista")
    public ResponseEntity<List<DepositoDTO>> listarDepositos(
            @RequestParam Long matrizId,
            @RequestParam Boolean deletado,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String materiaNome
    ) {
        List<DepositoDTO> resultado = depositoService.listarDepositos(matrizId, deletado, ativo, materiaNome);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listaDescartados")
    public ResponseEntity<List<DepositoDescartarDTO>> listarDepositosDescartados(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String materiaNome
    ) {
        List<DepositoDescartarDTO> resultado = depositoService.listarDepositosDescartados(matrizId, materiaNome);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarDeposito(@RequestBody DepositoDTO depositoDTO) {
        try{
            return ResponseEntity.ok(depositoService.cadastrarDeposito(depositoDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarDeposito(@PathVariable Long id, @RequestBody DepositoDTO depositoDTO) {
        try {
            return ResponseEntity.ok(depositoService.editarDeposito(id, depositoDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarDeposito(@PathVariable Long id, @RequestBody DepositoDTO depositoDTO) {
        try {
            return ResponseEntity.ok(depositoService.ativarOuDesativarDeposito(id, depositoDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PostMapping("/descartar")
    public ResponseEntity<MensagemDTO> descartarDeposito(@RequestBody DepositoDescartarDTO depositoDescartarDTO) {
        try {
            return ResponseEntity.ok(depositoService.descartarDeposito(depositoDescartarDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}