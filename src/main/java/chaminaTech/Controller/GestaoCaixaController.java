package chaminaTech.Controller;

import chaminaTech.DTO.GestaoCaixaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.GestaoCaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gestaoCaixa")
@CrossOrigin(origins = "*")
public class GestaoCaixaController {
    @Autowired
    private GestaoCaixaService gestaoCaixaService;

    @GetMapping("/cupom/{numeroCupom}/matriz/{matrizId}/entrega")
    public ResponseEntity<GestaoCaixaDTO> findByCupomAndAtivoAndEntregaAndMatrizId(@PathVariable Integer numeroCupom, @PathVariable Long matrizId) {
        GestaoCaixaDTO gestaoCaixaDTO = gestaoCaixaService.findByCupomAndAtivoAndEntregaAndMatrizId(numeroCupom, matrizId);
        if (gestaoCaixaDTO != null) {
            return new ResponseEntity<>(gestaoCaixaDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/cupom/{numeroCupom}/matriz/{matrizId}/retirada")
    public ResponseEntity<GestaoCaixaDTO> findByCupomAndAtivoAndRetiradaAndMatrizId(@PathVariable Integer numeroCupom, @PathVariable Long matrizId) {
        GestaoCaixaDTO gestaoCaixaDTO = gestaoCaixaService.findByCupomAndAtivoAndRetiradaAndMatrizId(numeroCupom, matrizId);
        if (gestaoCaixaDTO != null) {
            return new ResponseEntity<>(gestaoCaixaDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/cuponsEntregaAtivos/{matrizId}")
    public ResponseEntity<List<Map<String, Object>>> buscarCuponsEntregaByMatrizId(@PathVariable Long matrizId) {
        List<Map<String, Object>> cuponsEntrega = gestaoCaixaService.buscarCuponsEntregaByMatrizId(matrizId);
        return ResponseEntity.ok(cuponsEntrega);
    }

    // Endpoint para obter cupons de retirada
    @GetMapping("/cuponsRetiradaAtivos/{matrizId}")
    public ResponseEntity<List<Map<String, Object>>> buscarCuponsRetiradaByMatrizId(@PathVariable Long matrizId) {
        List<Map<String, Object>> cupons = gestaoCaixaService.buscarCuponsRetiradaByMatrizId(matrizId);
        return ResponseEntity.ok(cupons);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<GestaoCaixaDTO>> buscarHistoricoComFiltro(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String cupom
    ) {
        List<GestaoCaixaDTO> lista = gestaoCaixaService.buscarHistoricoComFiltro(matrizId, tipo, cupom);
        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/{matrizId}/zerarCupom")
    public ResponseEntity<MensagemDTO> zerarCupom(@PathVariable Long matrizId) {
        try {
            return ResponseEntity.ok(gestaoCaixaService.zerarCupom(matrizId));
        } catch (Exception e) {
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

}