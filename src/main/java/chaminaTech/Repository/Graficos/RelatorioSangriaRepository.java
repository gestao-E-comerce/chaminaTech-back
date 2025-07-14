package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Sangria;
import chaminaTech.Graficos.GraficoSangria.GraficoTipoSangria;
import chaminaTech.Graficos.GraficoSangria.GraficoTotalSangria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioSangriaRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT s FROM Sangria s
                WHERE s.caixa.matriz.id = :matrizId
                AND (:funcionarioId IS NULL OR s.funcionario.id = :funcionarioId)
                AND (:funcionarioNome IS NULL OR s.nomeFuncionario = :funcionarioNome)
                AND (:tipo IS NULL OR s.tipo = :tipo)
                AND (:caixaId IS NULL OR (s.caixa.id = :caixaId AND s.caixa.matriz.id = :matrizId))
                AND s.dataSangria >= COALESCE(:dataInicio, s.dataSangria)
                AND s.dataSangria <= COALESCE(:dataFim, s.dataSangria)
                AND s.ativo = true
            """)
    Page<Sangria> gerarRelatorioSangria(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("funcionarioNome") String funcionarioNome,
            @Param("tipo") String tipo,
            @Param("caixaId") Long caixaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoSangria.GraficoTipoSangria(
                    s.tipo,
                    SUM(s.valor)
                )
                FROM Sangria s
                WHERE s.caixa.matriz.id = :matrizId
                AND s.ativo = true
                AND (:funcionarioId IS NULL OR s.funcionario.id = :funcionarioId)
                AND (:funcionarioNome IS NULL OR s.nomeFuncionario = :funcionarioNome)
                AND (:tipo IS NULL OR s.tipo = :tipo)
                AND (:caixaId IS NULL OR (s.caixa.id = :caixaId AND s.caixa.matriz.id = :matrizId))
                AND s.dataSangria >= COALESCE(:dataInicio, s.dataSangria)
                AND s.dataSangria <= COALESCE(:dataFim, s.dataSangria)
                GROUP BY s.tipo
                ORDER BY s.tipo
            """)
    List<GraficoTipoSangria> gerarGraficoTipoSangria(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("funcionarioNome") String funcionarioNome,
            @Param("tipo") String tipo,
            @Param("caixaId") Long caixaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoSangria.GraficoTotalSangria(
                    s.caixa.id,
                    SUM(s.valor)
                )
                FROM Sangria s
                WHERE s.caixa.matriz.id = :matrizId
                AND s.ativo = true
                AND (:funcionarioId IS NULL OR s.funcionario.id = :funcionarioId)
                AND (:funcionarioNome IS NULL OR s.nomeFuncionario = :funcionarioNome)
                AND (:tipo IS NULL OR s.tipo = :tipo)
                AND (:caixaId IS NULL OR (s.caixa.id = :caixaId AND s.caixa.matriz.id = :matrizId))
                AND s.dataSangria >= COALESCE(:dataInicio, s.dataSangria)
                AND s.dataSangria <= COALESCE(:dataFim, s.dataSangria)
                GROUP BY s.caixa.id
                ORDER BY s.caixa.id
            """)
    List<GraficoTotalSangria> gerarGraficoTotalSangriaPorCaixa(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("funcionarioNome") String funcionarioNome,
            @Param("tipo") String tipo,
            @Param("caixaId") Long caixaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
