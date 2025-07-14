package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.EstoqueDescartar;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoEstoqueDescartar.GraficoResumoEstoqueDescartar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioEstoqueDescartarRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT e FROM EstoqueDescartar e
                WHERE e.matriz.id = :matrizId
                AND (:produtoId IS NULL OR e.produto.id = :produtoId)
                AND e.dataDescartar >= COALESCE(:dataInicio, e.dataDescartar)
                AND e.dataDescartar <= COALESCE(:dataFim, e.dataDescartar)
            """)
    Page<EstoqueDescartar> gerarRelatorioEstoqueDescartar(
            @Param("matrizId") Long matrizId,
            @Param("produtoId") Long produtoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoEstoqueDescartar.GraficoResumoEstoqueDescartar(
                    e.produto.nome,
                    SUM(COALESCE(e.quantidade, 0))
                )
                FROM EstoqueDescartar e
                WHERE e.matriz.id = :matrizId
                AND (:produtoId IS NULL OR e.produto.id = :produtoId)
                AND e.dataDescartar >= COALESCE(:dataInicio, e.dataDescartar)
                AND e.dataDescartar <= COALESCE(:dataFim, e.dataDescartar)
                GROUP BY e.produto.nome
                ORDER BY e.produto.nome
            """)
    List<GraficoResumoEstoqueDescartar> gerarGraficoResumoEstoqueDescartar(
            @Param("matrizId") Long matrizId,
            @Param("produtoId") Long produtoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}