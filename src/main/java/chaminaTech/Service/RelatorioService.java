package chaminaTech.Service;

import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.*;
import chaminaTech.Repository.RelatorioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository relatorioRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<RelatorioDTO> listarRelatorios(Long matrizId) {
        List<Relatorio> relatorios = relatorioRepository.listarRelatorios(matrizId);

        return relatorios.stream()
                .map(entityToDTO::relatorioToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarRelatorio(RelatorioDTO relatorioDTO) {
        PermissaoUtil.validarOuLancar("cadastrarRelatorio");
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);

        if (relatorioRepository.existsByNomeAndMatrizId(relatorioDTO.getMatriz().getId(), relatorio.getNome())) {
            throw new IllegalStateException("Já existe um relatorio com esse nome!");
        }


        relatorioRepository.save(relatorio);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "RELATORIO",
                "Cadastrou o relatório: " + relatorio.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                relatorio.getMatriz().getId()
        );
        return new MensagemDTO("Relatório cadastrado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarRelatorio(Long id, RelatorioDTO relatorioDTO) {
        PermissaoUtil.validarOuLancar("editarRelatorio");
        relatorioDTO.setId(id);
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);

        if (relatorioRepository.existsByNomeAndMatrizIdAndNotId(relatorioDTO.getMatriz().getId(), relatorio.getNome(), relatorio.getId())) {
            throw new IllegalStateException("Já existe um relatorio com esse nome!");
        }

        relatorioRepository.save(relatorio);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CATEGORIA",
                "Editou a relatório: " + relatorio.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                relatorio.getMatriz().getId()
        );
        return new MensagemDTO("Relatório atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarRelatorio(Long id) {
        PermissaoUtil.validarOuLancar("deletarRelatorio");
        Relatorio relatorioBanco = relatorioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relatorio com ID " + id + " não existe!"));

        relatorioRepository.delete(relatorioBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CATEGORIA",
                "Deletou a relatório: " + relatorioBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                relatorioBanco.getMatriz().getId()
        );
        return new MensagemDTO("Relatório deledato com sucesso!", HttpStatus.CREATED);
    }

    public Page<?> gerarRelatorio(RelatorioDTO relatorioDTO) {
        Pageable pageable = criarPageable(relatorioDTO);

        switch (relatorioDTO.getTipoConsulta()) {
            case "VENDA":
                return relatorioRepository.findVendasWithFilters(
                        relatorioDTO.getDeletado(),
                        relatorioDTO.getFuncionarioId(),
                        relatorioDTO.getTiposVenda(),
                        relatorioDTO.getDataInicio(),
                        relatorioDTO.getDataFim(),
                        relatorioDTO.getTaxaEntrega(),
                        relatorioDTO.getTaxaServico(),
                        relatorioDTO.getDesconto(),
                        relatorioDTO.getFormasPagamento(),
                        pageable
                );
            // você pode adicionar outros cases tipo:
            // case "PRODUTO":
            //     return produtoRepository.findComFiltros(...);
            default:
                throw new IllegalArgumentException("Tipo de consulta inválido: " + relatorioDTO.getTipoConsulta());
        }
    }

    private Pageable criarPageable(RelatorioDTO relatorioDTO) {
        String ordenar = relatorioDTO.getOrdenacao();
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

        return PageRequest.of(relatorioDTO.getPagina(), relatorioDTO.getTamanho(), sort);
    }
}
