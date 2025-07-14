package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Suprimento;
import chaminaTech.Graficos.GraficoSuprimento.GraficoTotalSuprimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioSuprimentoRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT s FROM Suprimento s
                WHERE s.caixa.matriz.id = :matrizId
                AND (:funcionarioId IS NULL OR s.funcionario.id = :funcionarioId)
                AND (:caixaId IS NULL OR (s.caixa.id = :caixaId AND s.caixa.matriz.id = :matrizId))
                AND s.dataSuprimento >= COALESCE(:dataInicio, s.dataSuprimento)
                AND s.dataSuprimento <= COALESCE(:dataFim, s.dataSuprimento)
                AND s.ativo = true
            """)
    Page<Suprimento> gerarRelatorioSuprimento(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("caixaId") Long caixaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoSuprimento.GraficoTotalSuprimento(
                    s.caixa.id,
                    SUM(s.valor)
                )
                FROM Suprimento s
                WHERE s.caixa.matriz.id = :matrizId
                AND s.ativo = true
                AND (:funcionarioId IS NULL OR s.funcionario.id = :funcionarioId)
                AND (:caixaId IS NULL OR (s.caixa.id = :caixaId AND s.caixa.matriz.id = :matrizId))
                AND s.dataSuprimento >= COALESCE(:dataInicio, s.dataSuprimento)
                AND s.dataSuprimento <= COALESCE(:dataFim, s.dataSuprimento)
                GROUP BY s.caixa.id
                ORDER BY s.caixa.id
            """)
    List<GraficoTotalSuprimento> gerarGraficoTotalSuprimentoPorCaixa(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("caixaId") Long caixaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
