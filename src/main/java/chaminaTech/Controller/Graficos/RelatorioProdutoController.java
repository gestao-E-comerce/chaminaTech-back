package chaminaTech.Controller.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido;
import chaminaTech.Service.Graficos.RelatorioProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorio/produto")
public class RelatorioProdutoController {
    @Autowired
    private RelatorioProdutoService relatorioProdutoService;

    @PostMapping("/gerarRelatorioProduto")
    public List<ProdutoMaisVendido> gerarRelatorioProduto(@RequestBody RelatorioDTO relatorioDTO) {
        return relatorioProdutoService.gerarRelatorioProduto(relatorioDTO);
    }

    @PostMapping("/graficoProdutoRetirada")
    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoRetirada(@RequestBody RelatorioDTO dto) {
        return relatorioProdutoService.gerarGraficoProdutoMaisVendidoRetirada(dto);
    }

    @PostMapping("/graficoProdutoEntrega")
    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoEntrega(@RequestBody RelatorioDTO dto) {
        return relatorioProdutoService.gerarGraficoProdutoMaisVendidoEntrega(dto);
    }

    @PostMapping("/graficoProdutoBalcao")
    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoBalcao(@RequestBody RelatorioDTO dto) {
        return relatorioProdutoService.gerarGraficoProdutoMaisVendidoBalcao(dto);
    }

    @PostMapping("/graficoProdutoMesa")
    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoMesa(@RequestBody RelatorioDTO dto) {
        return relatorioProdutoService.gerarGraficoProdutoMaisVendidoMesa(dto);
    }
}