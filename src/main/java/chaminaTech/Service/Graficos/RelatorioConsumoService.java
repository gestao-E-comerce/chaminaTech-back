package chaminaTech.Service.Graficos;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.VendaDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Venda;
import chaminaTech.Graficos.GraficoConsumo.GraficoTipoVendaConsumo;
import chaminaTech.Graficos.GraficoConsumo.GraficoValorTotalConsumo;
import chaminaTech.Repository.Graficos.RelatorioConsumoRepository;
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
public class RelatorioConsumoService {
    @Autowired
    private RelatorioConsumoRepository relatorioConsumoRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public GraficoTipoVendaConsumo gerarGraficoTipoVendaConsumo(RelatorioDTO dto) {
        LocalDateTime inicio = tentarParseData(dto.getDataInicio());
        LocalDateTime fim = tentarParseData(dto.getDataFim());

        return relatorioConsumoRepository.gerarGraficoTipoVendaTotal(dto.getMatriz().getId(),
                inicio,
                fim,
                dto.getDeletado(),
                dto.getFuncionarioId(),
                dto.getBalcao(),
                dto.getRetirada(),
                dto.getEntrega(),
                dto.getMesa());
    }

    public List<GraficoValorTotalConsumo> gerarGraficoValorTotalConsumo(RelatorioDTO dto) {
        String agrupamento = dto.getAgrupamento() != null ? dto.getAgrupamento() : "DIA";

        LocalDateTime inicio = tentarParseData(dto.getDataInicio());
        LocalDateTime fim = tentarParseData(dto.getDataFim());

        return switch (agrupamento) {
            case "HORA" -> relatorioConsumoRepository.gerarGraficoValorTotalHora(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa());
            case "MES" -> relatorioConsumoRepository.gerarGraficoValorTotalMes(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa());
            case "ANO" -> relatorioConsumoRepository.gerarGraficoValorTotalAno(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa());
            default -> relatorioConsumoRepository.gerarGraficoValorTotalDia(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa());
        };
    }

    public Page<VendaDTO> gerarRelatorioConsumo(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<Venda> relatorios = relatorioConsumoRepository.gerarRelatorioConsumo(
                relatorio.getMatriz().getId(),
                relatorio.getDeletado(),
                relatorio.getFuncionarioId(),
                relatorio.getBalcao(),
                relatorio.getRetirada(),
                relatorio.getEntrega(),
                relatorio.getMesa(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::vendaConsumoToDTORelatorio);


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
            sort = Sort.by(Sort.Direction.DESC, "dataVenda");
        } else {
            switch (ordenar) {
                case "valorTotalDesc":
                    sort = Sort.by(Sort.Direction.DESC, "valorTotal");
                    break;
                case "valorTotalAsc":
                    sort = Sort.by(Sort.Direction.ASC, "valorTotal");
                    break;

                case "funcionarioAsc":
                    sort = Sort.by(Sort.Direction.ASC, "funcionario.nome");
                    break;
                case "funcionarioDesc":
                    sort = Sort.by(Sort.Direction.DESC, "funcionario.nome");
                    break;

                case "dataAsc":
                    sort = Sort.by(Sort.Direction.ASC, "dataVenda");
                    break;
                case "dataDesc":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "dataVenda");
                    break;
            }
        }

        return PageRequest.of(relatorio.getPagina(), relatorio.getTamanho(), sort);
    }
}
