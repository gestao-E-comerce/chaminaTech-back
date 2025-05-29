package Ecomerce.assmar.Controller;

import Ecomerce.assmar.DTO.AdminDTO;
import Ecomerce.assmar.DTO.MatrizDTO;
import Ecomerce.assmar.DTO.MensagemDTO;
import Ecomerce.assmar.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> findAdminById(@PathVariable Long id) {
        AdminDTO adminDTO = adminService.findAdminById(id);
        if (adminDTO != null) {
            return ResponseEntity.ok(adminDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarAdmin(@PathVariable Long id, @RequestBody AdminDTO adminDTO) {
        try {
            return ResponseEntity.ok(adminService.editarAdmin(id, adminDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @GetMapping("/listarMatrizes")
    public List<MatrizDTO> listarAdministracaoes(
            @RequestParam (required = false) Boolean deletado,
            @RequestParam(required = false) String termoPesquisa,
            @RequestParam(required = false) Boolean ativo) {
        return adminService.listarMatrizes(deletado, termoPesquisa, ativo);
    }

    @GetMapping("/listarFilhos")
    public List<MatrizDTO> listarFilhos(
            @RequestParam (required = false) Boolean deletado,
            @RequestParam(required = false) String termoPesquisa,
            @RequestParam(required = false) Boolean ativo
    ) {
        return adminService.listarFilhos(deletado, termoPesquisa, ativo);
    }

    @GetMapping("/chave-api")
    public String getChaveApi() {
        return adminService.buscarChaveApiCoordenadas();
    }
}