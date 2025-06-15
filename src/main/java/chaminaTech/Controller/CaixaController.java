package chaminaTech.Controller;

import chaminaTech.DTO.CaixaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.CaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caixa")
@CrossOrigin(origins = "*")
public class CaixaController {

    @Autowired
    private CaixaService caixaService;

    @GetMapping("/{id}")
    public ResponseEntity<CaixaDTO> findCaixaById(@PathVariable Long id) {
        CaixaDTO caixaDTO = caixaService.findCaixaById(id);
        if (caixaDTO != null) {
            return ResponseEntity.ok(caixaDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/vendaAtiva/{matrizId}")
    public ResponseEntity<Boolean> verificaVendaAtiva(@PathVariable Long matrizId) {
        boolean vendaAtiva = caixaService.verificaVendasAtivasNaMatriz(matrizId);
        return ResponseEntity.ok(vendaAtiva);
    }

    @GetMapping("/caixa-ativa/{funcionarioId}")
    public ResponseEntity<CaixaDTO> getCaixaAtiva(@PathVariable Long funcionarioId) {
        return caixaService.buscarCaixaAtivaPorFuncionario(funcionarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/buscarCaixas")
    public ResponseEntity<List<CaixaDTO>> buscarCaixas(
            @RequestParam(required = false) String nome,
            @RequestParam Long matrizId,
            @RequestParam(required = false) String tipo) {  // Agora tipo é String, podendo ser 'aberto', 'fechado' ou null
        List<CaixaDTO> lista = caixaService.buscarCaixasPorFuncionarioNomeAtivoEPorMatrizId(nome, matrizId, tipo);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<CaixaDTO> abrirCaixa(@RequestBody CaixaDTO caixaDTO) {
        try {
            CaixaDTO caixaCriado = caixaService.abrirCaixa(caixaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(caixaCriado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/fechar-caixa/{id}")
    public ResponseEntity<MensagemDTO> fecharCaixa(@PathVariable Long id, @RequestBody CaixaDTO caixaDTO) {
        try {
            return ResponseEntity.ok(caixaService.fecharCaixa(id, caixaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<MensagemDTO> editarCaixa(@PathVariable Long id, @RequestBody CaixaDTO caixaDTO) {
        try {
            return ResponseEntity.ok(caixaService.editarCaixa(id, caixaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCaixa(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(caixaService.deletarCaixa(id));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @GetMapping("/{caixaId}/totalPix")
    public Double getTotalPixByCaixaId(@PathVariable Long caixaId) {
        return caixaService.getTotalPixByCaixaId(caixaId);
    }

    @GetMapping("/{caixaId}/totalDinheiro")
    public Double getTotalDinheiroByCaixaId(@PathVariable Long caixaId) {
        return caixaService.getTotalDinheiroByCaixaId(caixaId);
    }

    @GetMapping("/{caixaId}/totalDebito")
    public Double getTotalDebitoByCaixaId(@PathVariable Long caixaId) {
        return caixaService.getTotalDebitoByCaixaId(caixaId);
    }

    @GetMapping("/{caixaId}/totalCredito")
    public Double getTotalCreditoByCaixaId(@PathVariable Long caixaId) {
        return caixaService.getTotalCreditoByCaixaId(caixaId);
    }

    @GetMapping("/{caixaId}/totalSangrias")
    public Double getTotalSangriasByCaixaId(@PathVariable Long caixaId) {
        return caixaService.getTotalSangriasByCaixaId(caixaId);
    }

    @GetMapping("/{caixaId}/totalSuprimentos")
    public Double getTotalSuprimentosByCaixaId(@PathVariable Long caixaId) {
        return caixaService.getTotalSuprimentosByCaixaId(caixaId);
    }
}