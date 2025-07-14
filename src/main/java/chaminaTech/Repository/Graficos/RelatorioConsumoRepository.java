package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Venda;
import chaminaTech.Graficos.GraficoConsumo.GraficoTipoVendaConsumo;
import chaminaTech.Graficos.GraficoConsumo.GraficoValorTotalConsumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioConsumoRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT v FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = true

                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)

                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )
            """)
    Page<Venda> gerarRelatorioConsumo(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    // Por HORA
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoConsumo.GraficoValorTotalConsumo(
                    TO_CHAR(v.dataVenda, 'HH24:MI'),
                    SUM(COALESCE(v.valorTotal, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = true
                AND v.matriz.id = :matrizId
                
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )
                
                GROUP BY TO_CHAR(v.dataVenda, 'HH24:MI')
                ORDER BY TO_CHAR(v.dataVenda, 'HH24:MI')
            """)
    List<GraficoValorTotalConsumo> gerarGraficoValorTotalHora(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa
    );

    // Por DIA
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoConsumo.GraficoValorTotalConsumo(
                    TO_CHAR(v.dataVenda, 'dd/MM/yyyy'),
                    SUM(COALESCE(v.valorTotal, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = true
                AND v.matriz.id = :matrizId
                
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                GROUP BY TO_CHAR(v.dataVenda, 'dd/MM/yyyy')
                ORDER BY TO_CHAR(v.dataVenda, 'dd/MM/yyyy')
            """)
    List<GraficoValorTotalConsumo> gerarGraficoValorTotalDia(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa
    );

    // Por MÊS
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoConsumo.GraficoValorTotalConsumo(
                    TO_CHAR(v.dataVenda, 'MM/yyyy'),
                    SUM(COALESCE(v.valorTotal, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = true
                AND v.matriz.id = :matrizId
                
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                GROUP BY TO_CHAR(v.dataVenda, 'MM/yyyy')
                ORDER BY TO_CHAR(v.dataVenda, 'MM/yyyy')
            """)
    List<GraficoValorTotalConsumo> gerarGraficoValorTotalMes(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa
    );

    // Por ANO
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoConsumo.GraficoValorTotalConsumo(
                    TO_CHAR(v.dataVenda, 'yyyy'),
                    SUM(COALESCE(v.valorTotal, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = true
                AND v.matriz.id = :matrizId
                
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                GROUP BY TO_CHAR(v.dataVenda, 'yyyy')
                ORDER BY TO_CHAR(v.dataVenda, 'yyyy')
            """)
    List<GraficoValorTotalConsumo> gerarGraficoValorTotalAno(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoConsumo.GraficoTipoVendaConsumo(
                    'TOTAL',
                    COUNT(CASE WHEN v.mesa IS NOT NULL THEN 1 END),
                    COUNT(CASE WHEN v.balcao = true THEN 1 END),
                    COUNT(CASE WHEN v.entrega = true THEN 1 END),
                    COUNT(CASE WHEN v.retirada = true THEN 1 END)
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = true
                AND v.matriz.id = :matrizId
                
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )
            """)
    GraficoTipoVendaConsumo gerarGraficoTipoVendaTotal(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa
    );
}
