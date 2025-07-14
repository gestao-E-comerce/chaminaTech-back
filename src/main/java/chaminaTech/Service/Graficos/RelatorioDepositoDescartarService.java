package chaminaTech.Service.Graficos;

import chaminaTech.DTO.DepositoDescartarDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.DepositoDescartar;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoDepositoDescartar.GraficoResumoDepositoDescartar;
import chaminaTech.Repository.Graficos.RelatorioDepositoDescartarRepository;
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
public class RelatorioDepositoDescartarService {
    @Autowired
    private RelatorioDepositoDescartarRepository relatorioDepositoDescartarRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public Page<DepositoDescartarDTO> gerarRelatorioDepositoDescartar(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);
        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<DepositoDescartar> relatorios = relatorioDepositoDescartarRepository.gerarRelatorioDepositoDescartar(
                relatorio.getMatriz().getId(),
                relatorio.getMateriaId(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::depositoDescartarToDTO);
    }

    public List<GraficoResumoDepositoDescartar> gerarGraficoResumoDepositoDescartar(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioDepositoDescartarRepository.gerarGraficoResumoDepositoDescartar(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getMateriaId(),
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
                case "materiaAsc":
                    sort = Sort.by(Sort.Direction.ASC, "materia.nome");
                    break;
                case "materiaDesc":
                    sort = Sort.by(Sort.Direction.DESC, "materia.nome");
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
