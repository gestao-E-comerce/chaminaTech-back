package chaminaTech.Repository;

import chaminaTech.Entity.Relatorio;
import chaminaTech.Entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {
    @Query("""
                SELECT r FROM Relatorio r
                WHERE r.matriz.id = :matrizId
                ORDER BY r.id ASC
            """)
    List<Relatorio> listarRelatorios(@Param("matrizId") Long matrizId);

    @Query("""
            SELECT COUNT(r) > 0 FROM Relatorio r
            WHERE r.matriz.id = :matrizId
            AND r.nome = :nome
            """)
    boolean existsByNomeAndMatrizId(@Param("matrizId") Long matrizId, @Param("nome") String nome);

    @Query("""
            SELECT COUNT(r) > 0 FROM Relatorio r
            WHERE r.matriz.id = :matrizId
            AND r.nome = :nome
            AND r.id != :produtoId
            """)
    boolean existsByNomeAndMatrizIdAndNotId(@Param("matrizId") Long matrizId, @Param("nome") String nome, @Param("produtoId") Long produtoId);


    @Query("""
                SELECT v FROM Venda v
                WHERE v.consumoInterno = false
                AND (:deletado IS NULL OR v.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (
                    :tiposVenda IS NULL OR (
                        ('BALCAO' IN :tiposVenda AND v.balcao = true)
                        OR ('RETIRADA' IN :tiposVenda AND v.retirada = true)
                        OR ('ENTREGA' IN :tiposVenda AND v.entrega = true)
                        OR ('MESA' IN :tiposVenda AND v.mesa IS NOT NULL AND v.mesa > 0)
                    )
                )
                AND (:dataInicio IS NULL OR v.dataVenda >= :dataInicio)
                AND (:dataFim IS NULL OR v.dataVenda <= :dataFim)
                AND (:taxaEntrega IS NULL OR (:taxaEntrega = true AND v.taxaEntrega IS NOT NULL AND v.taxaEntrega > 0))
                AND (:taxaServico IS NULL OR (:taxaServico = true AND v.valorServico IS NOT NULL AND v.valorServico > 0))
                AND (:desconto IS NULL OR (:desconto = true AND v.desconto IS NOT NULL AND v.desconto > 0))
                AND (:formasPagamento IS NULL OR (
                    ('PIX' IN :formasPagamento AND v.vendaPagamento.pix > 0)
                    OR ('CREDITO' IN :formasPagamento AND v.vendaPagamento.credito > 0)
                    OR ('DINHEIRO' IN :formasPagamento AND v.vendaPagamento.dinheiro > 0)
                    OR ('DEBITO' IN :formasPagamento AND v.vendaPagamento.debito > 0)
                ))
            """)
    Page<Venda> findVendasWithFilters(
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("tiposVenda") List<String> tiposVenda,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("taxaEntrega") Boolean taxaEntrega,
            @Param("taxaServico") Boolean taxaServico,
            @Param("desconto") Boolean desconto,
            @Param("formasPagamento") List<String> formasPagamento,
            Pageable pageable
    );
}
