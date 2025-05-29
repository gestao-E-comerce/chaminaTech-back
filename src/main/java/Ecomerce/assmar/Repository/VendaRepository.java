package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    @Query("SELECT DISTINCT v FROM Venda v " +
            "WHERE v.mesa = :mesa " +
            "AND v.ativo = true " +
            "AND v.matriz.id = :matrizId"
    )
    Optional<Venda> buscarMesaAtivaByMatrizId(@Param("mesa") Integer mesa, @Param("matrizId") Long matrizId);

    @Query("SELECT v.mesa, v.statusEmAberto, v.statusEmPagamento " +
            "FROM Venda v " +
            "WHERE v.ativo = true AND v.deletado = false AND v.mesa IS NOT NULL AND v.matriz.id = :matrizId " +
            "ORDER BY v.mesa ASC")
    List<Object[]> buscarNumeroMesasByMatrizId(@Param("matrizId") Long matrizId);

    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v " +
            "WHERE v.ativo = true " +
            "AND v.deletado = false " +
            "AND v.matriz.id = :matrizId " +
            "AND ( " +
            "  (:tipoVenda = 'mesa' AND v.mesa IS NOT NULL) " +  // Para 'mesa', verificar se 'v.mesa' não é null
            "  OR (:tipoVenda = 'entrega' AND v.entrega = true) " +  // Para 'entrega', verificar se 'v.entrega' é true
            "  OR (:tipoVenda = 'retirada' AND v.retirada = true) " +  // Para 'retirada', verificar se 'v.retirada' é true
            ")")
    Double buscarTotalVendaPorMatriz(@Param("matrizId") Long matrizId, @Param("tipoVenda") String tipoVenda);

    @Query("""
                SELECT v FROM Venda v
                WHERE v.ativo = true
                AND (v.retirada = true OR v.entrega = true)
                AND v.tempoEstimado IS NOT NULL
                AND v.matriz.id = :matrizId
            """)
    List<Venda> buscarVendasComTempoEntregaPorMatriz(@Param("matrizId") Long matrizId);
}