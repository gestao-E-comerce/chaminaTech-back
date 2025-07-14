package chaminaTech.Service.Graficos;

import chaminaTech.DTO.*;
import chaminaTech.Graficos.GraficoVenda.GraficoPagamentoVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoPeriodoVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoTipoVendaVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoValorTotalVenda;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Venda;
import chaminaTech.Repository.Graficos.RelatorioVendaRepository;
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
public class RelatorioVendaService {
    @Autowired
    private RelatorioVendaRepository relatorioVendaRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    
    public GraficoPagamentoVenda gerarGraficoPagamentoTotalVenda(RelatorioDTO dto) {
        LocalDateTime inicio = tentarParseData(dto.getDataInicio());
        LocalDateTime fim = tentarParseData(dto.getDataFim());

        return relatorioVendaRepository.gerarGraficoPagamentoTotal(dto.getMatriz().getId(),
                inicio,
                fim,
                dto.getDeletado(),
                dto.getFuncionarioId(),
                dto.getClienteId(),
                dto.getBalcao(),
                dto.getRetirada(),
                dto.getEntrega(),
                dto.getMesa(),
                dto.getTaxaEntrega(),
                dto.getTaxaServico(),
                dto.getDesconto(),
                dto.getPix(),
                dto.getDebito(),
                dto.getCredito(),
                dto.getDinheiro(),
                dto.getPeriodoDia());
    }

    public GraficoTipoVendaVenda gerarGraficoTipoVendaVenda(RelatorioDTO dto) {
        LocalDateTime inicio = tentarParseData(dto.getDataInicio());
        LocalDateTime fim = tentarParseData(dto.getDataFim());

        return relatorioVendaRepository.gerarGraficoTipoVendaTotal(dto.getMatriz().getId(),
                inicio,
                fim,
                dto.getDeletado(),
                dto.getFuncionarioId(),
                dto.getClienteId(),
                dto.getBalcao(),
                dto.getRetirada(),
                dto.getEntrega(),
                dto.getMesa(),
                dto.getTaxaEntrega(),
                dto.getTaxaServico(),
                dto.getDesconto(),
                dto.getPix(),
                dto.getDebito(),
                dto.getCredito(),
                dto.getDinheiro(),
                dto.getPeriodoDia());
    }

    public GraficoPeriodoVenda gerarGraficoVendasPeriodoVenda(RelatorioDTO dto) {
        LocalDateTime inicio = tentarParseData(dto.getDataInicio());
        LocalDateTime fim = tentarParseData(dto.getDataFim());

        return relatorioVendaRepository.gerarGraficoPeriodoTotal(
                dto.getMatriz().getId(),
                inicio,
                fim,
                dto.getDeletado(),
                dto.getFuncionarioId(),
                dto.getClienteId(),
                dto.getBalcao(),
                dto.getRetirada(),
                dto.getEntrega(),
                dto.getMesa(),
                dto.getTaxaEntrega(),
                dto.getTaxaServico(),
                dto.getDesconto(),
                dto.getPix(),
                dto.getDebito(),
                dto.getCredito(),
                dto.getDinheiro(),
                dto.getPeriodoDia()
        );
    }

    public List<GraficoValorTotalVenda> gerarGraficoValorTotalVenda(RelatorioDTO dto) {
        String agrupamento = dto.getAgrupamento() != null ? dto.getAgrupamento() : "DIA";

        LocalDateTime inicio = tentarParseData(dto.getDataInicio());
        LocalDateTime fim = tentarParseData(dto.getDataFim());

        return switch (agrupamento) {
            case "HORA" -> relatorioVendaRepository.gerarGraficoValorTotalHora(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getClienteId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa(),
                    dto.getTaxaEntrega(),
                    dto.getTaxaServico(),
                    dto.getDesconto(),
                    dto.getPix(),
                    dto.getDebito(),
                    dto.getCredito(),
                    dto.getDinheiro(),
                    dto.getPeriodoDia());
            case "MES" -> relatorioVendaRepository.gerarGraficoValorTotalMes(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getClienteId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa(),
                    dto.getTaxaEntrega(),
                    dto.getTaxaServico(),
                    dto.getDesconto(),
                    dto.getPix(),
                    dto.getDebito(),
                    dto.getCredito(),
                    dto.getDinheiro(),
                    dto.getPeriodoDia());
            case "ANO" -> relatorioVendaRepository.gerarGraficoValorTotalAno(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getClienteId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa(),
                    dto.getTaxaEntrega(),
                    dto.getTaxaServico(),
                    dto.getDesconto(),
                    dto.getPix(),
                    dto.getDebito(),
                    dto.getCredito(),
                    dto.getDinheiro(),
                    dto.getPeriodoDia());
            default -> relatorioVendaRepository.gerarGraficoValorTotalDia(dto.getMatriz().getId(),
                    inicio,
                    fim,
                    dto.getDeletado(),
                    dto.getFuncionarioId(),
                    dto.getClienteId(),
                    dto.getBalcao(),
                    dto.getRetirada(),
                    dto.getEntrega(),
                    dto.getMesa(),
                    dto.getTaxaEntrega(),
                    dto.getTaxaServico(),
                    dto.getDesconto(),
                    dto.getPix(),
                    dto.getDebito(),
                    dto.getCredito(),
                    dto.getDinheiro(),
                    dto.getPeriodoDia());
        };
    }

    public Page<VendaDTO> gerarRelatorioVenda(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<Venda> relatorios = relatorioVendaRepository.gerarRelatorioVenda(
                relatorio.getMatriz().getId(),
                relatorio.getDeletado(),
                relatorio.getFuncionarioId(),
                relatorio.getClienteId(),
                relatorio.getBalcao(),
                relatorio.getRetirada(),
                relatorio.getEntrega(),
                relatorio.getMesa(),
                dataInicio,
                dataFim,
                relatorio.getTaxaEntrega(),
                relatorio.getTaxaServico(),
                relatorio.getDesconto(),
                relatorio.getPix(),
                relatorio.getDebito(),
                relatorio.getCredito(),
                relatorio.getDinheiro(),
                relatorio.getPeriodoDia(),
                pageable
        );

        return relatorios.map(entityToDTO::vendaToDTORelatorio);


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

                case "valorBrutoDesc":
                    sort = Sort.by(Sort.Direction.DESC, "valorBruto");
                    break;
                case "valorBrutoAsc":
                    sort = Sort.by(Sort.Direction.ASC, "valorBruto");
                    break;

                case "descontoDesc":
                    sort = Sort.by(Sort.Direction.DESC, "desconto");
                    break;
                case "descontoAsc":
                    sort = Sort.by(Sort.Direction.ASC, "desconto");
                    break;

                case "valorServicoDesc":
                    sort = Sort.by(Sort.Direction.DESC, "valorServico");
                    break;
                case "valorServicoAsc":
                    sort = Sort.by(Sort.Direction.ASC, "valorServico");
                    break;

                case "taxaEntregaDesc":
                    sort = Sort.by(Sort.Direction.DESC, "taxaEntrega");
                    break;
                case "taxaEntregaAsc":
                    sort = Sort.by(Sort.Direction.ASC, "taxaEntrega");
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
