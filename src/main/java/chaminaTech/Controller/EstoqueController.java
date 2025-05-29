package chaminaTech.Controller;

import chaminaTech.DTO.EstoqueDTO;
import chaminaTech.DTO.EstoqueDescartarDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "http://localhost:4200")
public class EstoqueController {
    @Autowired
    EstoqueService estoqueService;
    
    @GetMapping("/lista")
    public ResponseEntity<List<EstoqueDTO>> listarEstoques(
            @RequestParam Long matrizId,
            @RequestParam Boolean deletado,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String produtoNome
    ) {
        List<EstoqueDTO> resultado = estoqueService.listarEstoques(matrizId, deletado, ativo, produtoNome);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listaDescartados")
    public ResponseEntity<List<EstoqueDescartarDTO>> listaDescartados(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String produtoNome
    ) {
        List<EstoqueDescartarDTO> resultado = estoqueService.listarEstoquesDescartados(matrizId, produtoNome);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarEstoque(@RequestBody EstoqueDTO estoqueDTO) {
        try {
            return ResponseEntity.ok(estoqueService.cadastrarEstoque(estoqueDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarEstoque(@PathVariable Long id, @RequestBody EstoqueDTO estoqueDTO) {
        try {
            return ResponseEntity.ok(estoqueService.editarEstoque(id, estoqueDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarEstoque(@PathVariable Long id, @RequestBody EstoqueDTO estoqueDTO) {
        try {
            return ResponseEntity.ok(estoqueService.ativarOuDesativarEstoque(id, estoqueDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PostMapping("/descartar")
    public ResponseEntity<MensagemDTO> descartarEstoque(@RequestBody EstoqueDescartarDTO estoqueDescartarDTO) {
        try {
            return ResponseEntity.ok(estoqueService.descartarEstoque(estoqueDescartarDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}