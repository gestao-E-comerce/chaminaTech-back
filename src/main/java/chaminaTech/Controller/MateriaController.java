package chaminaTech.Controller;

import chaminaTech.DTO.MateriaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materia")
public class MateriaController {
    @Autowired
    MateriaService materiaService;

    @GetMapping("/lista")
    public List<MateriaDTO> listarMateriasPorNome(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String termoPesquisa,
            @RequestParam(required = false) Boolean ativo) {
        return materiaService.listarMaterias(matrizId, termoPesquisa, ativo);
    }

    @GetMapping("/listarMateriasDeposito")
    public ResponseEntity<List<MateriaDTO>> listarMateriasDeposito(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String termoPesquisa) {
        return ResponseEntity.ok(materiaService.listarMateriasDepositos(matrizId, termoPesquisa));
    }

    @GetMapping("/listarMateriasDepositoDescartar")
    public ResponseEntity<List<MateriaDTO>> listarMateriasDepositosDescartados(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String termoPesquisa) {
        return ResponseEntity.ok(materiaService.listarMateriasDepositosDescartados(matrizId, termoPesquisa));
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarMateria(@RequestBody MateriaDTO materiaDTO) {
        try {
            return ResponseEntity.ok(materiaService.cadastrarMateria(materiaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarMateria(@PathVariable Long id, @RequestBody MateriaDTO materiaDTO) {
        try {
            return ResponseEntity.ok(materiaService.editarMateria(id, materiaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarMateria(@PathVariable Long id, @RequestBody MateriaDTO materiaDTO) {
        try {
            return ResponseEntity.ok(materiaService.ativarOuDesativarMateria(id, materiaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarMateria(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(materiaService.deletarMateria(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}