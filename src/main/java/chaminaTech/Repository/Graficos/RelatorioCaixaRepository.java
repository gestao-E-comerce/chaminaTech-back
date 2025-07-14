package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Caixa;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoCaixa.GraficoPagamentoCaixa;
import chaminaTech.Graficos.GraficoCaixa.GraficoResumoCaixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioCaixaRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT c FROM Caixa c
                WHERE c.matriz.id = :matrizId

                AND (:deletado IS NULL OR c.deletado = :deletado)
                AND (:ativo IS NULL OR c.ativo = :ativo)
                AND (:funcionarioId IS NULL OR c.funcionario.id = :funcionarioId)

                AND c.dataAbertura >= COALESCE(:dataInicio, c.dataAbertura)
                AND c.dataAbertura <= COALESCE(:dataFim, c.dataAbertura)
            """)
    Page<Caixa> gerarRelatorioCaixa(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("ativo") Boolean ativo,
            @Param("funcionarioId") Long funcionarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoCaixa.GraficoResumoCaixa(
                    TO_CHAR(c.dataAbertura, 'dd/MM/yyyy HH24:MI'),
                    c.valorAbertura,
                    (SELECT COALESCE(SUM(s.valor), 0) FROM Suprimento s WHERE s.caixa.id = c.id AND s.ativo = true),
                    (SELECT COALESCE(SUM(v.valorBruto), 0) FROM Venda v WHERE v.caixa.id = c.id AND v.deletado = false),
                    (SELECT COALESCE(SUM(v.valorServico), 0) FROM Venda v WHERE v.caixa.id = c.id AND v.deletado = false),
                    (SELECT
                        COALESCE(SUM(COALESCE(g.dinheiro, 0)), 0) +
                        COALESCE(SUM(COALESCE(g.debito, 0)), 0) +
                        COALESCE(SUM(COALESCE(g.credito, 0)), 0) +
                        COALESCE(SUM(COALESCE(g.pix, 0)), 0)
                     FROM Gorjeta g WHERE g.caixa.id = c.id AND g.ativo = true),
                    (SELECT COALESCE(SUM(s.valor), 0) FROM Sangria s WHERE s.caixa.id = c.id AND s.ativo = true),
                    (SELECT COALESCE(SUM(v.desconto), 0) FROM Venda v WHERE v.caixa.id = c.id AND v.deletado = false),
                    (COALESCE(c.saldoDinheiro, 0) + COALESCE(c.saldoPix, 0) + COALESCE(c.saldoCredito, 0) + COALESCE(c.saldoDebito, 0)),
                    c.saldo
                )
                FROM Caixa c
                WHERE c.matriz.id = :matrizId
                AND (:deletado IS NULL OR c.deletado = :deletado)
                AND (:ativo IS NULL OR c.ativo = :ativo)
                AND (:funcionarioId IS NULL OR c.funcionario.id = :funcionarioId)
                AND c.dataAbertura BETWEEN COALESCE(:dataInicio, c.dataAbertura) AND COALESCE(:dataFim, c.dataAbertura)
                ORDER BY c.dataAbertura
            """)
    List<GraficoResumoCaixa> gerarGraficoResumoCaixa(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("ativo") Boolean ativo,
            @Param("funcionarioId") Long funcionarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoCaixa.GraficoPagamentoCaixa(
                    TO_CHAR(c.dataAbertura, 'dd/MM/yyyy HH24:MI'),

                    SUM(COALESCE(vp.dinheiro, 0)),
                    SUM(COALESCE(vp.credito, 0)),
                    SUM(COALESCE(vp.debito, 0)),
                    SUM(COALESCE(vp.pix, 0)),

                    SUM(COALESCE(vp.descontoDinheiro, 0)),
                    SUM(COALESCE(vp.descontoCredito, 0)),
                    SUM(COALESCE(vp.descontoDebito, 0)),
                    SUM(COALESCE(vp.descontoPix, 0)),

                    SUM(CASE WHEN g.ativo = true THEN COALESCE(g.dinheiro, 0) ELSE 0 END),
                    SUM(CASE WHEN g.ativo = true THEN COALESCE(g.credito, 0) ELSE 0 END),
                    SUM(CASE WHEN g.ativo = true THEN COALESCE(g.debito, 0) ELSE 0 END),
                    SUM(CASE WHEN g.ativo = true THEN COALESCE(g.pix, 0) ELSE 0 END),

                    SUM(COALESCE(vp.servicoDinheiro, 0)),
                    SUM(COALESCE(vp.servicoCredito, 0)),
                    SUM(COALESCE(vp.servicoDebito, 0)),
                    SUM(COALESCE(vp.servicoPix, 0))
                )
                FROM Caixa c
                LEFT JOIN c.vendas v ON v.deletado = false
                LEFT JOIN v.vendaPagamento vp
                LEFT JOIN Gorjeta g ON g.caixa.id = c.id AND g.ativo = true

                WHERE c.matriz.id = :matrizId
                AND (:deletado IS NULL OR c.deletado = :deletado)
                AND (:ativo IS NULL OR c.ativo = :ativo)
                AND (:funcionarioId IS NULL OR c.funcionario.id = :funcionarioId)
                AND c.dataAbertura >= COALESCE(:dataInicio, c.dataAbertura)
                AND c.dataAbertura <= COALESCE(:dataFim, c.dataAbertura)

                GROUP BY TO_CHAR(c.dataAbertura, 'dd/MM/yyyy HH24:MI')
                ORDER BY TO_CHAR(c.dataAbertura, 'dd/MM/yyyy HH24:MI')
            """)
    List<GraficoPagamentoCaixa> gerarGraficoComposicaoSaldo(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("ativo") Boolean ativo,
            @Param("funcionarioId") Long funcionarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
