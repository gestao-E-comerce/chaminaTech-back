package chaminaTech.Service.Graficos;

import chaminaTech.DTO.CaixaDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Caixa;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoCaixa.GraficoPagamentoCaixa;
import chaminaTech.Graficos.GraficoCaixa.GraficoResumoCaixa;
import chaminaTech.Repository.Graficos.RelatorioCaixaRepository;
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
public class RelatorioCaixaService {
    @Autowired
    private RelatorioCaixaRepository relatorioCaixaRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;

    public Page<CaixaDTO> gerarRelatorioCaixa(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);

        LocalDateTime dataInicio = tentarParseData(relatorio.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorio.getDataFim());

        Pageable pageable = criarPageable(relatorio);

        Page<Caixa> relatorios = relatorioCaixaRepository.gerarRelatorioCaixa(
                relatorio.getMatriz().getId(),
                relatorio.getDeletado(),
                relatorio.getAtivo(),
                relatorio.getFuncionarioId(),
                dataInicio,
                dataFim,
                pageable
        );

        return relatorios.map(entityToDTO::caixaToDTORelatorio);


    }

    public List<GraficoResumoCaixa> gerarGraficoResumoCaixa(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioCaixaRepository.gerarGraficoResumoCaixa(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getDeletado(),
                relatorioDTO.getAtivo(),
                relatorioDTO.getFuncionarioId(),
                dataInicio,
                dataFim);
    }

    public List<GraficoPagamentoCaixa> gerarGraficoComposicaoSaldo(RelatorioDTO relatorioDTO) {
        LocalDateTime dataInicio = tentarParseData(relatorioDTO.getDataInicio());
        LocalDateTime dataFim = tentarParseData(relatorioDTO.getDataFim());

        return relatorioCaixaRepository.gerarGraficoComposicaoSaldo(
                relatorioDTO.getMatriz().getId(),
                relatorioDTO.getDeletado(),
                relatorioDTO.getAtivo(),
                relatorioDTO.getFuncionarioId(),
                dataInicio,
                dataFim);
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
            sort = Sort.by(Sort.Direction.DESC, "dataAbertura");
        } else {
            switch (ordenar) {
                case "saldoAsc":
                    sort = Sort.by(Sort.Order.asc("saldo").nullsLast());
                    break;
                case "saldoDesc":
                    sort = Sort.by(Sort.Order.desc("saldo").nullsLast());
                    break;

                case "funcionarioAsc":
                    sort = Sort.by(Sort.Direction.ASC, "funcionario.nome");
                    break;
                case "funcionarioDesc":
                    sort = Sort.by(Sort.Direction.DESC, "funcionario.nome");
                    break;

                case "dataAsc":
                    sort = Sort.by(Sort.Direction.ASC, "dataAbertura");
                    break;
                case "dataDesc":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "dataAbertura");
                    break;
            }
        }

        return PageRequest.of(relatorio.getPagina(), relatorio.getTamanho(), sort);
    }
}
