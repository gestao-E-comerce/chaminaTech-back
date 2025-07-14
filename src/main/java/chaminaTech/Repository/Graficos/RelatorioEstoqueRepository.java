package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Estoque;
import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoEstoque.GraficoResumoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioEstoqueRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT e FROM Estoque e
                WHERE e.matriz.id = :matrizId
                AND (:produtoId IS NULL OR e.produto.id = :produtoId)
                AND (:ativo IS NULL OR e.ativo = :ativo)
                AND (:deletado IS NULL OR e.deletado = :deletado)
                AND e.dataCadastrar >= COALESCE(:dataInicio, e.dataCadastrar)
                AND e.dataCadastrar <= COALESCE(:dataFim, e.dataCadastrar)
            """)
    Page<Estoque> gerarRelatorioEstoque(
            @Param("matrizId") Long matrizId,
            @Param("produtoId") Long produtoId,
            @Param("ativo") Boolean ativo,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoEstoque.GraficoResumoEstoque(
                    e.produto.nome,
                    SUM(COALESCE(e.quantidade, 0)),
                    SUM(COALESCE(e.quantidadeVendido, 0)),
                    SUM(COALESCE(e.quantidade, 0)) - SUM(COALESCE(e.quantidadeVendido, 0)),
                    SUM(COALESCE(e.valorTotal, 0))
                )
                FROM Estoque e
                WHERE e.matriz.id = :matrizId
                AND (:produtoId IS NULL OR e.produto.id = :produtoId)
                AND (:ativo IS NULL OR e.ativo = :ativo)
                AND (:deletado IS NULL OR e.deletado = :deletado)
                AND e.dataCadastrar >= COALESCE(:dataInicio, e.dataCadastrar)
                AND e.dataCadastrar <= COALESCE(:dataFim, e.dataCadastrar)
                GROUP BY e.produto.nome
                ORDER BY e.produto.nome
            """)
    List<GraficoResumoEstoque> gerarGraficoResumoEstoque(
            @Param("matrizId") Long matrizId,
            @Param("produtoId") Long produtoId,
            @Param("ativo") Boolean ativo,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
