package chaminaTech.Service.Graficos;

import chaminaTech.DTO.EstoqueDescartarDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.EstoqueDescartar;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoEstoqueDescartar.GraficoResumoEstoqueDescartar;
import chaminaTech.Repository.Graficos.RelatorioEstoqueDescartarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class RelatorioEstoqueDescartarService {
    @Autowired
    private RelatorioEstoqueDescartarRepository relatorioEstoqueDescartarRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public Page<EstoqueDescartarDTO> gerarRelatorioEstoqueDescartar(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);
        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<EstoqueDescartar> relatorios = relatorioEstoqueDescartarRepository.gerarRelatorioEstoqueDescartar(
                relatorio.getMatriz().getId(),
                relatorio.getProdutoId(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::estoqueDescartarToDTO);
    }

    public List<GraficoResumoEstoqueDescartar> gerarGraficoResumoEstoqueDescartar(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioEstoqueDescartarRepository.gerarGraficoResumoEstoqueDescartar(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getProdutoId(),
                dataInicio,
                dataFim
        );
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

    private Pageable criarPageable(Relatorio relatorio) {
        String ordenar = relatorio.getOrdenacao();
        Sort sort;

        if (ordenar == null || ordenar.isBlank()) {
            sort = Sort.by(Sort.Direction.DESC, "dataDescartar");
        } else {
            switch (ordenar) {
                case "quantidadeAsc":
                    sort = Sort.by(Sort.Order.asc("quantidade").nullsLast());
                    break;
                case "quantidadeDesc":
                    sort = Sort.by(Sort.Order.desc("quantidade").nullsLast());
                    break;
                case "produtoAsc":
                    sort = Sort.by(Sort.Direction.ASC, "produto.nome");
                    break;
                case "produtoDesc":
                    sort = Sort.by(Sort.Direction.DESC, "produto.nome");
                    break;
                case "dataAsc":
                    sort = Sort.by(Sort.Direction.ASC, "dataDescartar");
                    break;
                case "dataDesc":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "dataDescartar");
                    break;
            }
        }

        return PageRequest.of(relatorio.getPagina(), relatorio.getTamanho(), sort);
    }
}
