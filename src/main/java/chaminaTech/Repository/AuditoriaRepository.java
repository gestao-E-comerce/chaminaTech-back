package chaminaTech.Repository;

import chaminaTech.Entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    @Query("""
              SELECT a FROM Auditoria a
               WHERE a.matrizId = :matrizId
                 AND (:usuario   IS NULL OR a.usuario   = :usuario)
                 AND (:operacao  IS NULL OR a.operacao  = :operacao)
                 AND (:tipo      IS NULL OR a.tipo      = :tipo)
                 AND a.dataHora >= COALESCE(:dataInicio, a.dataHora)
                 AND a.dataHora <= COALESCE(:dataFim,    a.dataHora)
            """)
    Page<Auditoria> buscarComFiltros(
            @Param("matrizId") Long matrizId,
            @Param("usuario") String usuario,
            @Param("operacao") String operacao,
            @Param("tipo") String tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

}
