package chaminaTech.Controller;

import chaminaTech.DTO.UsuarioDTO;
import chaminaTech.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findUsuarioById(@PathVariable Long id) {
        UsuarioDTO usuarioDTO = usuarioService.findUsuarioById(id);

        if (usuarioDTO != null) {
            return ResponseEntity.ok(usuarioDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/token")
    public ResponseEntity<UsuarioDTO> findUsuarioByToken(@RequestBody String token) {
        try {
            UsuarioDTO usuarioDTO = usuarioService.findUsuarioByToken(token);
            return ResponseEntity.ok(usuarioDTO);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
