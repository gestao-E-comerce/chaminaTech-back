package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Deposito;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoDeposito.GraficoResumoDeposito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioDepositoRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT d FROM Deposito d
                WHERE d.matriz.id = :matrizId
                AND (:materiaId IS NULL OR d.materia.id = :materiaId)
                AND (:ativo IS NULL OR d.ativo = :ativo)
                AND (:deletado IS NULL OR d.deletado = :deletado)
                AND d.dataCadastrar >= COALESCE(:dataInicio, d.dataCadastrar)
                AND d.dataCadastrar <= COALESCE(:dataFim, d.dataCadastrar)
            """)
    Page<Deposito> gerarRelatorioDeposito(
            @Param("matrizId") Long matrizId,
            @Param("materiaId") Long materiaId,
            @Param("ativo") Boolean ativo,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoDeposito.GraficoResumoDeposito(
                    d.materia.nome,
                    SUM(COALESCE(d.quantidade, 0)),
                    SUM(COALESCE(d.quantidadeVendido, 0)),
                    SUM(COALESCE(d.quantidade, 0)) - SUM(COALESCE(d.quantidadeVendido, 0)),
                    SUM(COALESCE(d.valorTotal, 0))
                )
                FROM Deposito d
                WHERE d.matriz.id = :matrizId
                AND (:materiaId IS NULL OR d.materia.id = :materiaId)
                AND (:ativo IS NULL OR d.ativo = :ativo)
                AND (:deletado IS NULL OR d.deletado = :deletado)
                AND d.dataCadastrar >= COALESCE(:dataInicio, d.dataCadastrar)
                AND d.dataCadastrar <= COALESCE(:dataFim, d.dataCadastrar)
                GROUP BY d.materia.nome
                ORDER BY d.materia.nome
            """)
    List<GraficoResumoDeposito> gerarGraficoResumoDeposito(
            @Param("matrizId") Long matrizId,
            @Param("materiaId") Long materiaId,
            @Param("ativo") Boolean ativo,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
