package chaminaTech.Service.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido;
import chaminaTech.Repository.Graficos.RelatorioProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class RelatorioProdutoService {
    @Autowired
    private RelatorioProdutoRepository relatorioProdutoRepository;
    @Autowired
    private DTOToEntity dtoToEntity;

    public List<ProdutoMaisVendido> gerarRelatorioProduto(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);
        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        String ordenacao = relatorio.getOrdenacao() != null ? relatorio.getOrdenacao() : "quantidadeDesc";

        return switch (ordenacao) {
            case "quantidadeAsc" -> relatorioProdutoRepository.gerarProdutosMaisVendidosOrdenadoPorQuantidadeAsc(
                    relatorio.getMatriz().getId(), relatorio.getFuncionarioId(), relatorio.getProdutoId(),
                    relatorio.getBalcao(), relatorio.getRetirada(), relatorio.getEntrega(), relatorio.getMesa(),
                    relatorio.getDeletado(), dataInicio, dataFim
            );
            case "quantidadeDesc" -> relatorioProdutoRepository.gerarProdutosMaisVendidosOrdenadoPorQuantidadeDesc(
                    relatorio.getMatriz().getId(), relatorio.getFuncionarioId(), relatorio.getProdutoId(),
                    relatorio.getBalcao(), relatorio.getRetirada(), relatorio.getEntrega(), relatorio.getMesa(),
                    relatorio.getDeletado(), dataInicio, dataFim
            );
            case "valorAsc" -> relatorioProdutoRepository.gerarProdutosMaisVendidosOrdenadoPorValorAsc(
                    relatorio.getMatriz().getId(), relatorio.getFuncionarioId(), relatorio.getProdutoId(),
                    relatorio.getBalcao(), relatorio.getRetirada(), relatorio.getEntrega(), relatorio.getMesa(),
                    relatorio.getDeletado(), dataInicio, dataFim
            );
            case "valorDesc" -> relatorioProdutoRepository.gerarProdutosMaisVendidosOrdenadoPorValorDesc(
                    relatorio.getMatriz().getId(), relatorio.getFuncionarioId(), relatorio.getProdutoId(),
                    relatorio.getBalcao(), relatorio.getRetirada(), relatorio.getEntrega(), relatorio.getMesa(),
                    relatorio.getDeletado(), dataInicio, dataFim
            );
            default -> throw new IllegalArgumentException("Ordenação inválida: " + ordenacao);
        };
    }

    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoRetirada(RelatorioDTO dto) {
        return gerarGraficoTipo(dto, "retirada");
    }

    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoEntrega(RelatorioDTO dto) {
        return gerarGraficoTipo(dto, "entrega");
    }

    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoBalcao(RelatorioDTO dto) {
        return gerarGraficoTipo(dto, "balcao");
    }

    public List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoMesa(RelatorioDTO dto) {
        return gerarGraficoTipo(dto, "mesa");
    }

    private List<ProdutoMaisVendido> gerarGraficoTipo(RelatorioDTO dto, String tipo) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(dto);
        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        return switch (tipo) {
            case "retirada" -> relatorioProdutoRepository.gerarGraficoProdutoMaisVendidoRetirada(
                    relatorio.getMatriz().getId(), relatorio.getDeletado(), relatorio.getFuncionarioId(),
                    relatorio.getProdutoId(), dataInicio, dataFim);
            case "entrega" -> relatorioProdutoRepository.gerarGraficoProdutoMaisVendidoEntrega(
                    relatorio.getMatriz().getId(), relatorio.getDeletado(), relatorio.getFuncionarioId(),
                    relatorio.getProdutoId(), dataInicio, dataFim);
            case "balcao" -> relatorioProdutoRepository.gerarGraficoProdutoMaisVendidoBalcao(
                    relatorio.getMatriz().getId(), relatorio.getDeletado(), relatorio.getFuncionarioId(),
                    relatorio.getProdutoId(), dataInicio, dataFim);
            case "mesa" -> relatorioProdutoRepository.gerarGraficoProdutoMaisVendidoMesa(
                    relatorio.getMatriz().getId(), relatorio.getDeletado(), relatorio.getFuncionarioId(),
                    relatorio.getProdutoId(), dataInicio, dataFim);
            default -> throw new IllegalArgumentException("Tipo de venda inválido: " + tipo);
        };
    }


    private LocalDateTime tentarParseData(String dataTexto) {
        if (dataTexto == null || dataTexto.isBlank()) return null;

        List<DateTimeFormatter> formatos = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("ddMMyyyyHHmm"),
                DateTimeFormatter.ofPattern("ddMMyyyy")
        );

        for (DateTimeFormatter formatter : formatos) {
            try {
                if (formatter.toString().contains("H")) {
                    return LocalDateTime.parse(dataTexto, formatter);
                } else {
                    return LocalDate.parse(dataTexto, formatter).atStartOfDay();
                }
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Formato de data inválido. Use dd/MM/yyyy, dd/MM/yyyy HH:mm ou ddMMyyyy");
    }
}
