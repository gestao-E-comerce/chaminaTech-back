package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Gorjeta;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoGorjeta.GraficoPagamentoGorjeta;
import chaminaTech.Graficos.GraficoGorjeta.GraficoTotalGorjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioGorjetaRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT g FROM Gorjeta g
                WHERE g.caixa.matriz.id = :matrizId
                AND g.ativo = true
                AND (:funcionarioId IS NULL OR g.funcionario.id = :funcionarioId)
                AND (:caixaId IS NULL OR (g.caixa.id = :caixaId AND g.caixa.matriz.id = :matrizId))
                AND (
                  (:pix = true AND g.pix IS NOT NULL AND g.pix > 0) OR
                  (:debito = true AND g.debito IS NOT NULL AND g.debito > 0) OR
                  (:credito = true AND g.credito IS NOT NULL AND g.credito > 0) OR
                  (:dinheiro = true AND g.dinheiro IS NOT NULL AND g.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )
                AND g.dataGorjeta >= COALESCE(:dataInicio, g.dataGorjeta)
                AND g.dataGorjeta <= COALESCE(:dataFim, g.dataGorjeta)
            """)
    Page<Gorjeta> gerarRelatorioGorjeta(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("caixaId") Long caixaId,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoGorjeta.GraficoTotalGorjeta(
                    g.caixa.id,
                    SUM(
                        COALESCE(g.pix, 0) +
                        COALESCE(g.credito, 0) +
                        COALESCE(g.debito, 0) +
                        COALESCE(g.dinheiro, 0)
                    )
                )
                FROM Gorjeta g
                WHERE g.caixa.matriz.id = :matrizId
                AND g.ativo = true
                AND (:funcionarioId IS NULL OR g.funcionario.id = :funcionarioId)
                AND (:caixaId IS NULL OR (g.caixa.id = :caixaId AND g.caixa.matriz.id = :matrizId))
                AND (
                  (:pix = true AND g.pix IS NOT NULL AND g.pix > 0) OR
                  (:debito = true AND g.debito IS NOT NULL AND g.debito > 0) OR
                  (:credito = true AND g.credito IS NOT NULL AND g.credito > 0) OR
                  (:dinheiro = true AND g.dinheiro IS NOT NULL AND g.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )
                AND g.dataGorjeta >= COALESCE(:dataInicio, g.dataGorjeta)
                AND g.dataGorjeta <= COALESCE(:dataFim, g.dataGorjeta)
                GROUP BY g.caixa.id
                ORDER BY g.caixa.id
            """)
    List<GraficoTotalGorjeta> gerarGraficoTotalGorjeta(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("caixaId") Long caixaId,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoGorjeta.GraficoPagamentoGorjeta(
                    SUM(COALESCE(g.pix, 0)),
                    SUM(COALESCE(g.credito, 0)),
                    SUM(COALESCE(g.debito, 0)),
                    SUM(COALESCE(g.dinheiro, 0))
                )
                FROM Gorjeta g
                WHERE g.caixa.matriz.id = :matrizId
                AND g.ativo = true
                AND (:funcionarioId IS NULL OR g.funcionario.id = :funcionarioId)
                AND (:caixaId IS NULL OR (g.caixa.id = :caixaId AND g.caixa.matriz.id = :matrizId))
                AND (
                  (:pix = true AND g.pix IS NOT NULL AND g.pix > 0) OR
                  (:debito = true AND g.debito IS NOT NULL AND g.debito > 0) OR
                  (:credito = true AND g.credito IS NOT NULL AND g.credito > 0) OR
                  (:dinheiro = true AND g.dinheiro IS NOT NULL AND g.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )
                AND g.dataGorjeta >= COALESCE(:dataInicio, g.dataGorjeta)
                AND g.dataGorjeta <= COALESCE(:dataFim, g.dataGorjeta)
            """)
    GraficoPagamentoGorjeta gerarGraficoPagamentoGorjeta(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("caixaId") Long caixaId,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
