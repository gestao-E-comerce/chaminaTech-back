package chaminaTech.Controller;

import chaminaTech.Service.ImpressaoService;
import chaminaTech.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/impressao")
@CrossOrigin(origins = "*")
public class ImpressaoController {
    @Autowired
    private ImpressaoService impressaoService;
    @PostMapping("/produtos")
    public ResponseEntity<MensagemDTO> imprimirProdutos(@RequestBody ImpressaoDTO impressaoDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirProdutos(impressaoDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
    @PostMapping("/conta")
    public ResponseEntity<MensagemDTO> imprimirConta(@RequestBody ImpressaoDTO impressaoDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirConta(impressaoDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
    @PostMapping("/conferencia")
    public ResponseEntity<MensagemDTO> imprimirConferencia(@RequestBody VendaDTO vendaDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirConferencia(vendaDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/comprovante")
    public ResponseEntity<MensagemDTO> imprimirComprovante(@RequestBody VendaDTO vendaDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirComprovante(vendaDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/sangria")
    public ResponseEntity<MensagemDTO> imprimirSangria(@RequestBody SangriaDTO sangriaDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirSangria(sangriaDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/suprimento")
    public ResponseEntity<MensagemDTO> imprimirSangria(@RequestBody SuprimentoDTO suprimentoDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirSuprimento(suprimentoDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/abertura")
    public ResponseEntity<MensagemDTO> imprimirAbertura(@RequestBody CaixaDTO caixaDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirAbertura(caixaDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/conferenciaCaixa")
    public ResponseEntity<MensagemDTO> imprimirConferenciaCaixa(@RequestBody CaixaDTO caixaDTO) {
        try {
            return ResponseEntity.ok(impressaoService.imprimirConferenciaCaixa(caixaDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

//    @PostMapping("/notaFiscal")
//    public ResponseEntity<MensagemDTO> imprimirNotaFiscal(@RequestBody VendaDTO vendaDTO) {
//        try {
//            return ResponseEntity.ok(impressaoService.imprimirNotaFiscal(vendaDTO));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
//        }
//    }
}
