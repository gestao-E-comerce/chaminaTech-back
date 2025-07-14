package chaminaTech.Service;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.RelatorioDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Repository.RelatorioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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

    public RelatorioDTO  cadastrarRelatorio(RelatorioDTO relatorioDTO) throws IllegalStateException {
        PermissaoUtil.validarOuLancar("cadastrarRelatorio");
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);

        if (relatorioRepository.existsByNomeAndMatrizId(relatorioDTO.getMatriz().getId(), relatorio.getNome())) {
            throw new IllegalStateException("Já existe um relatorio com esse nome!");
        }


        Relatorio salvo = relatorioRepository.save(relatorio);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "RELATORIO",
                "Cadastrou o relatório: " + relatorio.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                relatorio.getMatriz().getId()
        );
        return entityToDTO.relatorioToDTO(salvo);
    }

    public RelatorioDTO  editarRelatorio(Long id, RelatorioDTO relatorioDTO) throws IllegalStateException {
        PermissaoUtil.validarOuLancar("editarRelatorio");
        relatorioDTO.setId(id);
        Relatorio relatorio = dtoToEntity.DTOToRelatorio(relatorioDTO);

        if (relatorioRepository.existsByNomeAndMatrizIdAndNotId(relatorioDTO.getMatriz().getId(), relatorio.getNome(), relatorio.getId())) {
            throw new IllegalStateException("Já existe um relatorio com esse nome!");
        }

        Relatorio salvo = relatorioRepository.save(relatorio);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "RELATORIO",
                "Editou a relatório: " + relatorio.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                relatorio.getMatriz().getId()
        );
        return entityToDTO.relatorioToDTO(salvo);
    }

    public MensagemDTO deletarRelatorio(Long id) {
        PermissaoUtil.validarOuLancar("deletarRelatorio");
        Relatorio relatorioBanco = relatorioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relatorio com ID " + id + " não existe!"));

        relatorioRepository.delete(relatorioBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "RELATORIO",
                "Deletou a relatório: " + relatorioBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                relatorioBanco.getMatriz().getId()
        );
        return new MensagemDTO("Relatório deledato com sucesso!", HttpStatus.CREATED);
    }
}
