package chaminaTech.Controller;

import chaminaTech.DTO.TransferenciaDTO;
import chaminaTech.DTO.VendaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.VendaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/venda")
@CrossOrigin(origins = "*")
public class VendaController {
    @Autowired
    private VendaService vendaService;

    @GetMapping("/totalVenda/{matrizId}/{tipoVenda}")
    public ResponseEntity<Double> buscarTotalVendaPorMatriz(@PathVariable Long matrizId, @PathVariable String tipoVenda) {
        try {
            Double total = vendaService.buscarTotalVendaPorMatriz(matrizId, tipoVenda);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0.0);
        }
    }

    @GetMapping("/mesa/{mesa}/matriz/{matrizId}")
    public ResponseEntity<VendaDTO> buscarMesaAtivaByMatrizId(@PathVariable Integer mesa, @PathVariable Long matrizId) {
        VendaDTO vendaDTO = vendaService.buscarMesaAtivaByMatrizId(mesa, matrizId);
        if (vendaDTO != null) {
            return ResponseEntity.ok(vendaDTO);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/mesasAtivas/{matrizId}")
    public ResponseEntity<List<Map<String, Object>>> buscarNumeroMesasByMatrizId(@PathVariable Long matrizId) {
        List<Map<String, Object>> mesas = vendaService.buscarNumeroMesasByMatrizId(matrizId);
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaDTO> findVendaById(@PathVariable Long id) {
        VendaDTO vendaDTO = vendaService.findVendaById(id);
        if (vendaDTO != null) {
            return ResponseEntity.ok(vendaDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarVenda(@RequestBody VendaDTO vendaDTO) {
        try {
            return ResponseEntity.ok(vendaService.cadastrarVenda(vendaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PostMapping("/transferir")
    public ResponseEntity<MensagemDTO> transferirProdutos(@RequestBody TransferenciaDTO transferenciaDTO) {
        return ResponseEntity.ok(vendaService.transferirProdutos(transferenciaDTO));
    }
    @PostMapping("/cadastrarSimples")
    public VendaDTO cadastrarVendaSimples(@RequestBody VendaDTO vendaDTO) {
        return vendaService.salvarMesaApenasExistir(vendaDTO);
    }

    @PostMapping("/cadastrarParcial")
    public ResponseEntity<MensagemDTO> cadastrarVendaParcial(@RequestBody VendaDTO vendaDTO) {
        try {
            return ResponseEntity.ok(vendaService.salvarMesaParcial(vendaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarVenda(@PathVariable Long id, @RequestBody VendaDTO vendaDTO, @RequestHeader("chaveUnico") String chaveUnicoRecebida) {
        try {
            return ResponseEntity.ok(vendaService.editarVenda(id, vendaDTO, chaveUnicoRecebida));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/deletar/{id}")
    public ResponseEntity<MensagemDTO> deletarVenda(@PathVariable Long id, @RequestBody VendaDTO vendaDTO) {
        try {
            return ResponseEntity.ok(vendaService.deletarVenda(id, vendaDTO));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{vendaId}/marcar-em-uso")
    public ResponseEntity<MensagemDTO> marcarVendaComoEmUso(@PathVariable Long vendaId) {
        try {
            return ResponseEntity.ok(vendaService.marcarVendaComoEmUso(vendaId));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{vendaId}/marcar-em-pagamento")
    public ResponseEntity<MensagemDTO> marcarVendaComoEmPagamento(@PathVariable Long vendaId) {
        try {
            return ResponseEntity.ok(vendaService.marcarVendaComoEmPagamento(vendaId));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/liberarPorNumero")
    public ResponseEntity<MensagemDTO> liberarVendaPorNumero(
            @RequestBody Map<String, Object> requestBody) {
        try {
            Integer numero = (Integer) requestBody.get("numero");
            Long matrizId = ((Number) requestBody.get("matrizId")).longValue();
            String tipo = (String) requestBody.get("tipo");

            if (numero == null || matrizId == null || tipo == null) {
                return ResponseEntity.badRequest().body(new MensagemDTO("Dados inválidos!", HttpStatus.BAD_REQUEST));
            }

            return ResponseEntity.ok(vendaService.liberarVendaPorNumero(numero, matrizId, tipo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{vendaId}/liberar")
    public ResponseEntity<MensagemDTO> liberarVenda(@PathVariable Long vendaId) {
        try {
            return ResponseEntity.ok(vendaService.liberarVenda(vendaId));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/deletarMesa/{id}")
    public ResponseEntity<String> deletarMesa(@PathVariable Long id) {
        try {
            vendaService.deletarMesa(id);
            return ResponseEntity.ok("Mesa deletada com sucesso!"); // 🔥 Retorna mensagem
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar mesa: " + e.getMessage());
        }
    }
}