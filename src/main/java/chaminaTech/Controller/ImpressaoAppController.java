package chaminaTech.Controller;

import chaminaTech.Entity.Impressao;
import chaminaTech.Service.ImpressaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/impressao")
public class ImpressaoAppController {
    @Autowired
    private ImpressaoService impressaoService;
    @GetMapping("/pendentes/{matrizId}")
    public List<Impressao> listarPendentes(@PathVariable Long matrizId) {
        return impressaoService.listarPendentes(matrizId);
    }

    @DeleteMapping("/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        impressaoService.deletar(id);
    }
}
