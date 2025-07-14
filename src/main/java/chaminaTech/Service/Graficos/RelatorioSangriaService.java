package chaminaTech.Service.Graficos;

import chaminaTech.DTO.SangriaDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Sangria;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoSangria.GraficoTipoSangria;
import chaminaTech.Graficos.GraficoSangria.GraficoTotalSangria;
import chaminaTech.Repository.Graficos.RelatorioSangriaRepository;
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
public class RelatorioSangriaService {
    @Autowired
    private RelatorioSangriaRepository relatorioSangriaRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public Page<SangriaDTO> gerarRelatorioSangria(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);

        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<Sangria> relatorios = relatorioSangriaRepository.gerarRelatorioSangria(
                relatorio.getMatriz().getId(),
                relatorio.getFuncionarioId(),
                relatorio.getFuncionarioNome(),
                relatorio.getTipo(),
                relatorio.getCaixaId(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::sangriaToDTO);


    }

    public List<GraficoTipoSangria> gerarGraficoTipoSangria(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioSangriaRepository.gerarGraficoTipoSangria(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getFuncionarioId(),
                relatorioDTO.getFuncionarioNome(),
                relatorioDTO.getTipo(),
                relatorioDTO.getCaixaId(),
                dataInicio,
                dataFim
        );
    }

    public List<GraficoTotalSangria> gerarGraficoTotalSangriaPorCaixa(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioSangriaRepository.gerarGraficoTotalSangriaPorCaixa(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getFuncionarioId(),
                relatorioDTO.getFuncionarioNome(),
                relatorioDTO.getTipo(),
                relatorioDTO.getCaixaId(),
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
            sort = Sort.by(Sort.Direction.DESC, "dataSangria");
        } else {
            switch (ordenar) {
                case "valorAsc":
                    sort = Sort.by(Sort.Order.asc("valor").nullsLast());
                    break;
                case "valorDesc":
                    sort = Sort.by(Sort.Order.desc("valor").nullsLast());
                    break;

                case "funcionarioAsc":
                    sort = Sort.by(Sort.Direction.ASC, "funcionario.nome");
                    break;
                case "funcionarioDesc":
                    sort = Sort.by(Sort.Direction.DESC, "funcionario.nome");
                    break;

                case "dataAsc":
                    sort = Sort.by(Sort.Direction.ASC, "dataSangria");
                    break;
                case "dataDesc":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "dataSangria");
                    break;
            }
        }

        return PageRequest.of(relatorio.getPagina(), relatorio.getTamanho(), sort);
    }
}
