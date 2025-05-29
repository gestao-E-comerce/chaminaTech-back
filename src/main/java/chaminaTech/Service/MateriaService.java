package chaminaTech.Service;

import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.Entity.Materia;
import chaminaTech.Repository.MateriaRepository;
import chaminaTech.DTO.MateriaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaService {
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<MateriaDTO> listarMaterias(Long matrizId, Boolean deletado, String termoPesquisa, Boolean ativo) {
        return materiaRepository.listarMaterias(matrizId, deletado, termoPesquisa, ativo).stream()
                .map(entityToDTO::materiaToDTO)
                .collect(Collectors.toList());
    }

    public List<MateriaDTO> listarMateriasDepositos(Long matrizId, String termoPesquisa) {
        List<Object[]> resultados = materiaRepository.listarMateriasDepositos(matrizId, termoPesquisa);
        return resultados.stream().map(obj -> {
            Materia materia = (Materia) obj[0];
            Double quantidade = obj[1] != null ? ((BigDecimal) obj[1]).doubleValue() : 0.0;

            MateriaDTO dto = entityToDTO.materiaToDTO(materia);
            dto.setQuantidadeDisponivel(quantidade);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<MateriaDTO> listarMateriasDepositosDescartados(Long matrizId, String termoPesquisa) {
        List<Object[]> resultados = materiaRepository.listarMateriasDepositosDescartados(matrizId, termoPesquisa);
        return resultados.stream().map(obj -> {
            Materia materia = (Materia) obj[0];
            Double quantidade = obj[1] != null ? ((BigDecimal) obj[1]).doubleValue() : 0.0;

            MateriaDTO dto = entityToDTO.materiaToDTO(materia);
            dto.setQuantidadeDescartada(quantidade);
            return dto;
        }).collect(Collectors.toList());
    }

    public MensagemDTO cadastrarMateria(MateriaDTO materiaDTO) {
        PermissaoUtil.validarOuLancar("cadastrarMateria");
        Materia materia = dtoToEntity.DTOToMateria(materiaDTO);

        if (materiaRepository.existsByNomeAndMatrizIdAndDeletado(materiaDTO.getMatriz().getId(), materia.getNome(), false)) {
            throw new IllegalStateException("Já existe uma materia com esse nome!");
        }

        materiaRepository.save(materia);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "MATERIA",
                "Cadastrou a matéria: " + materia.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                materia.getMatriz().getId()
        );
        return new MensagemDTO("Materia cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarMateria(Long id, MateriaDTO materiaDTO) {
        PermissaoUtil.validarOuLancar("editarMateria");
        materiaDTO.setId(id);
        Materia materia = dtoToEntity.DTOToMateria(materiaDTO);

        if (materiaRepository.existsByNomeAndMatrizIdAndDeletadoAndNotId(materiaDTO.getMatriz().getId(), materia.getNome(), false, materia.getId())) {
            throw new IllegalStateException("Já existe uma materia com esse nome!");
        }

        materiaRepository.save(materia);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "MATERIA",
                "Editou a matéria: " + materia.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                materia.getMatriz().getId()
        );
        return new MensagemDTO("Materia atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO ativarOuDesativarMateria(Long id, MateriaDTO materiaDTO) {
        PermissaoUtil.validarOuLancar("editarMateria");
        materiaDTO.setId(id);
        Materia materia = dtoToEntity.DTOToMateria(materiaDTO);

        // Inverte o status atual
        boolean novoStatus = !Boolean.TRUE.equals(materiaDTO.getAtivo());
        materia.setAtivo(novoStatus);

        materiaRepository.save(materia);

        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                "MATERIA",
                (novoStatus ? "Ativou" : "Desativou") + " a matéria: " + materia.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                materia.getMatriz().getId()
        );
        String mensagem = novoStatus
                ? "Materia ativado com sucesso!"
                : "Materia desativado com sucesso!";

        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO deletarMateria(Long id) {
        PermissaoUtil.validarOuLancar("deletarMateria");
        Materia materiaBanco = materiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Materia com ID " + id + " não existe!"));

        if (materiaRepository.existsByMateriaEmProdutoMateria(id)) {
            throw new IllegalStateException("Matéria não pode ser deletada pois está vinculada a um produto ativo.");
        }

        if (materiaRepository.existsByMateriaEmDepositoAtivo(id)) {
            throw new IllegalStateException("Matéria não pode ser deletada pois está vinculada a um depósito ativo.");
        }

        if (materiaRepository.existsByMateriaEmObservacao(id)) {
            throw new IllegalStateException("Matéria não pode ser deletada pois está sendo usada em uma observação.");
        }

        desativarMateria(materiaBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "MATERIA",
                "Deletou a matéria: " + materiaBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                materiaBanco.getMatriz().getId()
        );
        return new MensagemDTO("Materia deletada com sucesso!", HttpStatus.CREATED);
    }

    private void desativarMateria(Materia materia) {
        materia.setDeletado(true);
        materia.setAtivo(false);
        materiaRepository.save(materia);
    }
}