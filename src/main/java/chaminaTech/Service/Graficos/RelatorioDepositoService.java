package chaminaTech.Service.Graficos;

import chaminaTech.DTO.DepositoDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Deposito;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoDeposito.GraficoResumoDeposito;
import chaminaTech.Repository.Graficos.RelatorioDepositoRepository;
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
public class RelatorioDepositoService {
    @Autowired
    private RelatorioDepositoRepository relatorioDepositoRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public Page<DepositoDTO> gerarRelatorioDeposito(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);
        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<Deposito> relatorios = relatorioDepositoRepository.gerarRelatorioDeposito(
                relatorio.getMatriz().getId(),
                relatorio.getMateriaId(),
                relatorio.getAtivo(),
                relatorio.getDeletado(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::depositoToDTO);
    }

    public List<GraficoResumoDeposito> gerarGraficoResumoDeposito(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioDepositoRepository.gerarGraficoResumoDeposito(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getMateriaId(),
                relatorioDTO.getAtivo(),
                relatorioDTO.getDeletado(),
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
            sort = Sort.by(Sort.Direction.DESC, "dataCadastrar");
        } else {
            switch (ordenar) {
                case "valorAsc":
                    sort = Sort.by(Sort.Order.asc("valorTotal").nullsLast());
                    break;
                case "valorDesc":
                    sort = Sort.by(Sort.Order.desc("valorTotal").nullsLast());
                    break;
                case "materiaAsc":
                    sort = Sort.by(Sort.Direction.ASC, "materia.nome");
                    break;
                case "materiaDesc":
                    sort = Sort.by(Sort.Direction.DESC, "materia.nome");
                    break;
                case "dataAsc":
                    sort = Sort.by(Sort.Direction.ASC, "dataCadastrar");
                    break;
                case "dataDesc":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "dataCadastrar");
                    break;
            }
        }

        return PageRequest.of(relatorio.getPagina(), relatorio.getTamanho(), sort);
    }
}
