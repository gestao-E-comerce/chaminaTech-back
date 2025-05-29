package chaminaTech.Controller;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.TaxaEntregaKmDTO;
import chaminaTech.Service.TaxaEntregaKmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxasKm")
@CrossOrigin(origins = "http://localhost:4200")
public class TaxaEntregaKmController {

    @Autowired
    private TaxaEntregaKmService taxaEntregaKmService;

    @GetMapping("/lista/{matrizId}")
    public ResponseEntity<List<TaxaEntregaKmDTO>> listarPorMatriz(@PathVariable Long matrizId) {
        return ResponseEntity.ok(taxaEntregaKmService.buscarTaxasKmPorMatriz(matrizId));
    }
    @PostMapping("/salvarLista/{matrizId}")
    public ResponseEntity<MensagemDTO> salvarLista(@RequestBody List<TaxaEntregaKmDTO> lista, @PathVariable Long matrizId) {
        try {
            MensagemDTO resposta = taxaEntregaKmService.salvarlista(lista, matrizId);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
}
