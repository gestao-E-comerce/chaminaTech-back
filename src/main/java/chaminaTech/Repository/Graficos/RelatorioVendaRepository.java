package chaminaTech.Repository.Graficos;

import chaminaTech.Graficos.GraficoVenda.GraficoPagamentoVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoPeriodoVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoTipoVendaVenda;
import chaminaTech.Graficos.GraficoVenda.GraficoValorTotalVenda;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioVendaRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT v FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false

                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)

                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )

                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )
            """)
    Page<Venda> gerarRelatorioVenda(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia,
            Pageable pageable
    );

    // Por HORA
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoVenda.GraficoValorTotalVenda(
                    TO_CHAR(v.dataVenda, 'HH24:MI'),
                    SUM(COALESCE(v.valorTotal, 0)),
                    SUM(COALESCE(v.valorBruto, 0)),
                    SUM(COALESCE(v.valorServico, 0)),
                    SUM(COALESCE(v.taxaEntrega, 0)),
                    SUM(COALESCE(v.desconto, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = false
                AND v.matriz.id = :matrizId
                
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )
                
                GROUP BY TO_CHAR(v.dataVenda, 'HH24:MI')
                ORDER BY TO_CHAR(v.dataVenda, 'HH24:MI')
            """)
    List<GraficoValorTotalVenda> gerarGraficoValorTotalHora(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia
    );

    // Por DIA
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoVenda.GraficoValorTotalVenda(
                    TO_CHAR(v.dataVenda, 'dd/MM/yyyy'),
                    SUM(COALESCE(v.valorTotal, 0)),
                    SUM(COALESCE(v.valorBruto, 0)),
                    SUM(COALESCE(v.valorServico, 0)),
                    SUM(COALESCE(v.taxaEntrega, 0)),
                    SUM(COALESCE(v.desconto, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = false
                AND v.matriz.id = :matrizId
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )

                GROUP BY TO_CHAR(v.dataVenda, 'dd/MM/yyyy')
                ORDER BY TO_CHAR(v.dataVenda, 'dd/MM/yyyy')
            """)
    List<GraficoValorTotalVenda> gerarGraficoValorTotalDia(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia
    );

    // Por MÊS
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoVenda.GraficoValorTotalVenda(
                    TO_CHAR(v.dataVenda, 'MM/yyyy'),
                    SUM(COALESCE(v.valorTotal, 0)),
                    SUM(COALESCE(v.valorBruto, 0)),
                    SUM(COALESCE(v.valorServico, 0)),
                    SUM(COALESCE(v.taxaEntrega, 0)),
                    SUM(COALESCE(v.desconto, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = false
                AND v.matriz.id = :matrizId
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )

                GROUP BY TO_CHAR(v.dataVenda, 'MM/yyyy')
                ORDER BY TO_CHAR(v.dataVenda, 'MM/yyyy')
            """)
    List<GraficoValorTotalVenda> gerarGraficoValorTotalMes(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia
    );

    // Por ANO
    @Query("""
                SELECT new chaminaTech.Graficos.GraficoVenda.GraficoValorTotalVenda(
                    TO_CHAR(v.dataVenda, 'yyyy'),
                    SUM(COALESCE(v.valorTotal, 0)),
                    SUM(COALESCE(v.valorBruto, 0)),
                    SUM(COALESCE(v.valorServico, 0)),
                    SUM(COALESCE(v.taxaEntrega, 0)),
                    SUM(COALESCE(v.desconto, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = false
                AND v.matriz.id = :matrizId
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )

                GROUP BY TO_CHAR(v.dataVenda, 'yyyy')
                ORDER BY TO_CHAR(v.dataVenda, 'yyyy')
            """)
    List<GraficoValorTotalVenda> gerarGraficoValorTotalAno(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia
    );


    @Query("""
                SELECT new chaminaTech.Graficos.GraficoVenda.GraficoPagamentoVenda(
                    SUM(COALESCE(vp.pix, 0)),
                    SUM(COALESCE(vp.credito, 0)),
                    SUM(COALESCE(vp.debito, 0)),
                    SUM(COALESCE(vp.dinheiro, 0))
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = false
                AND v.matriz.id = :matrizId
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )
            """)
    GraficoPagamentoVenda gerarGraficoPagamentoTotal(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoVenda.GraficoTipoVendaVenda(
                    'TOTAL',
                    COUNT(CASE WHEN v.mesa IS NOT NULL THEN 1 END),
                    COUNT(CASE WHEN v.balcao = true THEN 1 END),
                    COUNT(CASE WHEN v.entrega = true THEN 1 END),
                    COUNT(CASE WHEN v.retirada = true THEN 1 END)
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = false
                AND v.matriz.id = :matrizId
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )

            """)
    GraficoTipoVendaVenda gerarGraficoTipoVendaTotal(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoVenda.GraficoPeriodoVenda(
                    'TOTAL',
                    SUM(CASE WHEN EXTRACT(HOUR FROM v.dataVenda) BETWEEN 0 AND 5 THEN 1 ELSE 0 END),
                    SUM(CASE WHEN EXTRACT(HOUR FROM v.dataVenda) BETWEEN 6 AND 11 THEN 1 ELSE 0 END),
                    SUM(CASE WHEN EXTRACT(HOUR FROM v.dataVenda) BETWEEN 12 AND 17 THEN 1 ELSE 0 END),
                    SUM(CASE WHEN EXTRACT(HOUR FROM v.dataVenda) BETWEEN 18 AND 23 THEN 1 ELSE 0 END)
                )
                FROM Venda v
                LEFT JOIN v.vendaPagamento vp
                WHERE v.consumoInterno = false
                AND v.matriz.id = :matrizId
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
                
                AND (
                    :periodoDia IS NULL OR (
                        EXTRACT(HOUR FROM v.dataVenda) >= CASE
                            WHEN :periodoDia = 'MANHA' THEN 6
                            WHEN :periodoDia = 'TARDE' THEN 12
                            WHEN :periodoDia = 'NOITE' THEN 18
                            WHEN :periodoDia = 'MADRUGADA' THEN 0
                        END
                        AND
                        EXTRACT(HOUR FROM v.dataVenda) < CASE
                            WHEN :periodoDia = 'MANHA' THEN 12
                            WHEN :periodoDia = 'TARDE' THEN 18
                            WHEN :periodoDia = 'NOITE' THEN 24
                            WHEN :periodoDia = 'MADRUGADA' THEN 6
                        END
                    )
                )
                
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                
                AND (
                  :taxaEntrega IS NULL OR
                  (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0) OR
                  (:taxaEntrega = false AND (v.taxaEntrega IS NULL OR v.taxaEntrega = 0))
                )
                AND (
                  :taxaServico IS NULL OR
                  (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0) OR
                  (:taxaServico = false AND (v.valorServico IS NULL OR v.valorServico = 0))
                )
                AND (
                  :desconto IS NULL OR
                  (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0) OR
                  (:desconto = false AND (v.desconto IS NULL OR v.desconto = 0))
                )

                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )

                AND (
                  (:pix = true AND vp.pix IS NOT NULL AND vp.pix > 0) OR
                  (:debito = true AND vp.debito IS NOT NULL AND vp.debito > 0) OR
                  (:credito = true AND vp.credito IS NOT NULL AND vp.credito > 0) OR
                  (:dinheiro = true AND vp.dinheiro IS NOT NULL AND vp.dinheiro > 0) OR
                  (:pix IS NULL AND :debito IS NULL AND :credito IS NULL AND :dinheiro IS NULL)
                )

            """)
    GraficoPeriodoVenda gerarGraficoPeriodoTotal(
            @Param("matrizId") Long matrizId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("clienteId") Long clienteId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("pix") Boolean pix,
            @Param("debito") Boolean debito,
            @Param("credito") Boolean credito,
            @Param("dinheiro") Boolean dinheiro,
            @Param("periodoDia") String periodoDia
    );
}
