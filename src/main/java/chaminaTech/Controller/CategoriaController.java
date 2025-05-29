package chaminaTech.Controller;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.CategoriaDTO;
import chaminaTech.Service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categoria")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {
    @Autowired
    CategoriaService categoriaService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> findCategoriaById(@PathVariable Long id) {
        CategoriaDTO categoriaDTO = categoriaService.findCategoriaById(id);
        if (categoriaDTO != null) {
            return ResponseEntity.ok(categoriaDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<List<CategoriaDTO>> listarCategorias(
            @RequestParam Long matrizId,
            @RequestParam Boolean deletado,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String nome
    ) {
        List<CategoriaDTO> resultado = categoriaService.listarCategorias(matrizId, deletado, ativo, nome);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        try{
            return ResponseEntity.ok(categoriaService.cadastrarCategoria(categoriaDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarCategoria(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        try {
            return ResponseEntity.ok(categoriaService.editarCategoria(id, categoriaDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarCategoria(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        try {
            return ResponseEntity.ok(categoriaService.ativarOuDesativarCategoria(id, categoriaDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCategoria(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoriaService.deletarCategoria(id));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}
