package chaminaTech.Service;

import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Auditoria;
import chaminaTech.Repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    public Page<Auditoria> listarAuditorias(
            Long matrizId,
            String usuario,
            String operacao,
            String tipo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    ) {
        PermissaoUtil.validarOuLancar("auditoria");
        if (matrizId == null) {
            throw new IllegalArgumentException("O campo 'matrizId' é obrigatório.");
        }

        return auditoriaRepository.buscarComFiltros(
                matrizId, usuario, operacao, tipo, dataInicio, dataFim, pageable
        );
    }

    public void salvarAuditoria(String operacao, String tipo, String descricao, String usuario, Long matrizId) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setOperacao(operacao);
            auditoria.setTipo(tipo);
            auditoria.setDescricao(descricao);
            auditoria.setUsuario(usuario);
            auditoria.setMatrizId(matrizId);
            auditoria.setDataHora(LocalDateTime.now());
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            System.err.println("Erro ao salvar auditoria: " + e.getMessage());
        }
    }
}
