package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.DepositoDescartar;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoDepositoDescartar.GraficoResumoDepositoDescartar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioDepositoDescartarRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT d FROM DepositoDescartar d
                WHERE d.matriz.id = :matrizId
                AND (:materiaId IS NULL OR d.materia.id = :materiaId)
                AND d.dataDescartar >= COALESCE(:dataInicio, d.dataDescartar)
                AND d.dataDescartar <= COALESCE(:dataFim, d.dataDescartar)
            """)
    Page<DepositoDescartar> gerarRelatorioDepositoDescartar(
            @Param("matrizId") Long matrizId,
            @Param("materiaId") Long materiaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoDepositoDescartar.GraficoResumoDepositoDescartar(
                    d.materia.nome,
                    SUM(COALESCE(d.quantidade, 0))
                )
                FROM DepositoDescartar d
                WHERE d.matriz.id = :matrizId
                AND (:materiaId IS NULL OR d.materia.id = :materiaId)
                AND d.dataDescartar >= COALESCE(:dataInicio, d.dataDescartar)
                AND d.dataDescartar <= COALESCE(:dataFim, d.dataDescartar)
                GROUP BY d.materia.nome
                ORDER BY d.materia.nome
            """)
    List<GraficoResumoDepositoDescartar> gerarGraficoResumoDepositoDescartar(
            @Param("matrizId") Long matrizId,
            @Param("materiaId") Long materiaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}