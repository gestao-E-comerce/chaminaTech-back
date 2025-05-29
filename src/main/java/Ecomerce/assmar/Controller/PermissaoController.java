package Ecomerce.assmar.Controller;

import Ecomerce.assmar.DTO.PermissaoDTO;
import Ecomerce.assmar.DTO.MensagemDTO;
import Ecomerce.assmar.Service.PermissaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissao")
@CrossOrigin(origins = "http://localhost:4200")
public class PermissaoController {
    @Autowired
    private PermissaoService permissaoService;

    @GetMapping("/lista/{usuarioId}")
    public List<PermissaoDTO> listarPermissaosPorUsuarioId(@PathVariable Long usuarioId) {
        return permissaoService.listarPermissaosPorUsuarioId(usuarioId);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarPermissao(@RequestBody PermissaoDTO permissaoDTO) {
        try{
            return ResponseEntity.ok(permissaoService.cadastrarPermissao(permissaoDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarPermissao(@PathVariable Long id, @RequestBody PermissaoDTO permissaoDTO) {
        try {
            return ResponseEntity.ok(permissaoService.editarPermissao(id, permissaoDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarPermissao(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(permissaoService.deletarPermissao(id));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}
