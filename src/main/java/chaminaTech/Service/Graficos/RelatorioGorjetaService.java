package chaminaTech.Service.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.GorjetaDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Gorjeta;
import chaminaTech.Graficos.GraficoGorjeta.GraficoPagamentoGorjeta;
import chaminaTech.Graficos.GraficoGorjeta.GraficoTotalGorjeta;
import chaminaTech.Repository.Graficos.RelatorioGorjetaRepository;
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
public class RelatorioGorjetaService {
    @Autowired
    private RelatorioGorjetaRepository relatorioGorjetaRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public Page<GorjetaDTO> gerarRelatorioGorjeta(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);
        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<Gorjeta> relatorios = relatorioGorjetaRepository.gerarRelatorioGorjeta(
                relatorio.getMatriz().getId(),
                relatorio.getFuncionarioId(),
                relatorio.getCaixaId(),
                relatorio.getPix(),
                relatorio.getDebito(),
                relatorio.getCredito(),
                relatorio.getDinheiro(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::gorjetaToDTO);
    }

    public List<GraficoTotalGorjeta> gerarGraficoTotalGorjetaPorCaixa(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioGorjetaRepository.gerarGraficoTotalGorjeta(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getFuncionarioId(),
                relatorioDTO.getCaixaId(),
                relatorioDTO.getPix(),
                relatorioDTO.getDebito(),
                relatorioDTO.getCredito(),
                relatorioDTO.getDinheiro(),
                dataInicio,
                dataFim
        );
    }

    public GraficoPagamentoGorjeta gerarGraficoPagamentoGorjeta(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioGorjetaRepository.gerarGraficoPagamentoGorjeta(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getFuncionarioId(),
                relatorioDTO.getCaixaId(),
                relatorioDTO.getPix(),
                relatorioDTO.getDebito(),
                relatorioDTO.getCredito(),
                relatorioDTO.getDinheiro(),
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
            sort = Sort.by(Sort.Direction.DESC, "dataGorjeta");
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
                    sort = Sort.by(Sort.Direction.ASC, "dataGorjeta");
                    break;
                case "dataDesc":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "dataGorjeta");
                    break;
            }
        }

        return PageRequest.of(relatorio.getPagina(), relatorio.getTamanho(), sort);
    }
}
