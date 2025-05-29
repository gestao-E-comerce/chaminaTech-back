package chaminaTech.Controller;

import chaminaTech.DTO.AdminFuncionarioDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.AdminFuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adminFuncionario")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminFuncionarioController {
    @Autowired
    AdminFuncionarioService adminFuncionarioService;

    @GetMapping("/{id}")
    public ResponseEntity<AdminFuncionarioDTO> findAdminFuncionarioById(@PathVariable Long id) {
        AdminFuncionarioDTO adminFuncionarioDTO = adminFuncionarioService.findAdminFuncionarioById(id);
        if (adminFuncionarioDTO != null) {
            return ResponseEntity.ok(adminFuncionarioDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lista")
    public List<AdminFuncionarioDTO> listarAdminFuncionarios(
            @RequestParam Long adminId,
            @RequestParam Boolean deletado,
            @RequestParam(required = false) String termoPesquisa,
            @RequestParam(required = false) Boolean ativo) {
        return adminFuncionarioService.listarAdminFuncionarios(adminId, deletado, termoPesquisa, ativo);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarAdminFuncionario(@RequestBody AdminFuncionarioDTO adminFuncionarioDTO) {
        try {
            return ResponseEntity.ok(adminFuncionarioService.cadastrarAdminFuncionario(adminFuncionarioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarAdminFuncionario(@PathVariable Long id, @RequestBody AdminFuncionarioDTO adminFuncionarioDTO) {
        try {
            return ResponseEntity.ok(adminFuncionarioService.editarAdminFuncionario(id, adminFuncionarioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarAdminFuncionario(@PathVariable Long id, @RequestBody AdminFuncionarioDTO adminFuncionarioDTO) {
        try {
            return ResponseEntity.ok(adminFuncionarioService.ativarOuDesativarAdminFuncionario(id, adminFuncionarioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminFuncionarioService.deletarAdminFuncionario(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}