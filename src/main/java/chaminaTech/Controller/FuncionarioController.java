package chaminaTech.Controller;

import chaminaTech.DTO.FuncionarioDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionario")
@CrossOrigin(origins = "http://localhost:4200")
public class FuncionarioController {
    @Autowired
    FuncionarioService funcionarioService;

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> findFuncionarioById(@PathVariable Long id) {
        FuncionarioDTO funcionarioDTO = funcionarioService.findFuncionarioById(id);
        if (funcionarioDTO != null) {
            return ResponseEntity.ok(funcionarioDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lista")
    public List<FuncionarioDTO> listarFuncionarios(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String termoPesquisa,
            @RequestParam(required = false) Boolean ativo) {
        return funcionarioService.listarFuncionarios(matrizId, termoPesquisa, ativo);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarFuncionario(@RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            return ResponseEntity.ok(funcionarioService.cadastrarFuncionario(funcionarioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/preferencias/impressao")
    public ResponseEntity<MensagemDTO> salvarPreferenciasImpressao(@RequestBody List<FuncionarioDTO> funcionarios) {
        try {
            return ResponseEntity.ok(funcionarioService.salvarPreferenciasImpressao(funcionarios));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarFuncionario(@PathVariable Long id, @RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            return ResponseEntity.ok(funcionarioService.editarFuncionario(id, funcionarioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarFuncionario(@PathVariable Long id, @RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            return ResponseEntity.ok(funcionarioService.ativarOuDesativarFuncionario(id, funcionarioDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(funcionarioService.deletarFuncionario(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}