package chaminaTech.Controller;

import chaminaTech.DTO.MatrizDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.MatrizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriz")
@CrossOrigin(origins = "*")
public class MatrizController {
    @Autowired
    private MatrizService matrizService;

    @GetMapping("/{id}")
    public ResponseEntity<MatrizDTO> findMatrizById(@PathVariable Long id) {
        MatrizDTO matrizDTO = matrizService.findMatrizById(id);
        if (matrizDTO != null) {
            return ResponseEntity.ok(matrizDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lista/{matrizId}")
    public List<MatrizDTO> listarFilhosPorMatrizId(@PathVariable Long matrizId) {
        return matrizService.listarFilhosPorMatrizId(matrizId);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarMatriz(@RequestBody MatrizDTO matrizDTO) {
        try{
            return ResponseEntity.ok(matrizService.cadastrarMatriz(matrizDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarMatriz(@PathVariable Long id, @RequestBody MatrizDTO matrizDTO) {
        try {
            return ResponseEntity.ok(matrizService.editarMatriz(id, matrizDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarMatriz(@PathVariable Long id, @RequestBody MatrizDTO matrizDTO) {
        try {
            return ResponseEntity.ok(matrizService.ativarOuDesativarMatriz(id, matrizDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}