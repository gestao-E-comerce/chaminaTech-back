package chaminaTech.Service.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.SuprimentoDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Suprimento;
import chaminaTech.Graficos.GraficoSuprimento.GraficoTotalSuprimento;
import chaminaTech.Repository.Graficos.RelatorioSuprimentoRepository;
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
public class RelatorioSuprimentoService {
    @Autowired
    private RelatorioSuprimentoRepository relatorioSuprimentoRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public Page<SuprimentoDTO> gerarRelatorioSuprimento(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);

        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<Suprimento> relatorios = relatorioSuprimentoRepository.gerarRelatorioSuprimento(
                relatorio.getMatriz().getId(),
                relatorio.getFuncionarioId(),
                relatorio.getCaixaId(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::suprimentoToDTO);


    }

    public List<GraficoTotalSuprimento> gerarGraficoTotalSuprimentoPorCaixa(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioSuprimentoRepository.gerarGraficoTotalSuprimentoPorCaixa(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getFuncionarioId(),
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
            sort = Sort.by(Sort.Direction.DESC, "dataSuprimento");
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
                    sort = Sort.by(Sort.Direction.ASC, "dataSuprimento");
                    break;
                case "dataDesc":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "dataSuprimento");
                    break;
            }
        }

        return PageRequest.of(relatorio.getPagina(), relatorio.getTamanho(), sort);
    }
}
