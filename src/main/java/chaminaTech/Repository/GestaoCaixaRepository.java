package chaminaTech.Repository;

import chaminaTech.Entity.GestaoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GestaoCaixaRepository extends JpaRepository<GestaoCaixa, Long> {
    boolean existsByMatrizIdAndAtivoIsNull(Long matrizId);

    boolean existsByMatrizIdAndAtivoTrue(Long matrizId);

    @Query("SELECT gc FROM GestaoCaixa gc WHERE gc.venda.id = :vendaId")
    Optional<GestaoCaixa> findByVendaId(@Param("vendaId") Long vendaId);

    Optional<GestaoCaixa> findTopByMatrizIdOrderByIdDesc(Long matrizId);

    @Query("SELECT g FROM GestaoCaixa g " +
            "LEFT JOIN FETCH g.venda v " +
            "WHERE g.cupom = :numeroCupom " +
            "AND g.ativo = true " +
            "AND v.entrega = true " +
            "AND v.matriz.id = :matrizId")
    Optional<GestaoCaixa> findByCupomAndAtivoAndEntregaAndMatrizId(@Param("numeroCupom") Integer numeroCupom, @Param("matrizId") Long matrizId);

    @Query("SELECT g FROM GestaoCaixa g " +
            "LEFT JOIN FETCH g.venda v " +
            "WHERE g.cupom = :numeroCupom " +
            "AND g.ativo = true " +
            "AND v.retirada = true " +
            "AND v.matriz.id = :matrizId")
    Optional<GestaoCaixa> findByCupomAndAtivoAndRetiradaAndMatrizId(@Param("numeroCupom") Integer numeroCupom, @Param("matrizId") Long matrizId);

//    @Query("SELECT g.cupom, g.venda.statusEmAberto, g.venda.statusEmPagamento, g.venda.cliente.nome, g.venda.id, g.venda.dataVenda, g.venda.tempoEstimado " +
//            "FROM GestaoCaixa g " +
//            "WHERE g.ativo = true AND g.venda.entrega = true AND g.matriz.id = :matrizId " +
//            "ORDER BY g.cupom ASC")
//    List<Object[]> buscarCuponsEntregaByMatrizId(@Param("matrizId") Long matrizId);

//    @Query("SELECT g.cupom, g.venda.statusEmAberto, g.venda.statusEmPagamento, g.venda.cliente.nome, g.venda.id, g.venda.dataVenda, g.venda.tempoEstimado " +
//            "FROM GestaoCaixa g " +
//            "WHERE g.ativo = true AND g.venda.retirada = true AND g.matriz.id = :matrizId " +
//            "ORDER BY g.cupom ASC")
//    List<Object[]> buscarCuponsRetiradaByMatrizId(@Param("matrizId") Long matrizId);

    @Query("""
                SELECT g.cupom, g.venda.statusEmAberto, g.venda.statusEmPagamento, g.venda.cliente.nome,
                       g.venda.id, g.venda.dataVenda, g.venda.tempoEstimado
                FROM GestaoCaixa g
                WHERE g.ativo = true
                  AND g.venda.entrega = true
                  AND g.matriz.id = :matrizId
                  AND (:nome IS NULL OR CAST(g.venda.cliente.nome AS string) LIKE %:nome%)
                ORDER BY g.cupom ASC
            """)
    List<Object[]> buscarCuponsEntregaByMatrizId(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome
    );

    @Query("""
                SELECT g.cupom, g.venda.statusEmAberto, g.venda.statusEmPagamento, g.venda.cliente.nome,
                       g.venda.id, g.venda.dataVenda, g.venda.tempoEstimado
                FROM GestaoCaixa g
                WHERE g.ativo = true
                  AND g.venda.retirada = true
                  AND g.matriz.id = :matrizId
                  AND (:nome IS NULL OR CAST(g.venda.cliente.nome AS string) LIKE %:nome%)
                ORDER BY g.cupom ASC
            """)
    List<Object[]> buscarCuponsRetiradaByMatrizId(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome
    );


    @Query("""
                SELECT g FROM GestaoCaixa g
                WHERE g.ativo = false
                  AND g.matriz.id = :matrizId
                  AND g.venda.consumoInterno = false
                  AND (:tipo IS NULL OR
                      (:tipo = 'mesa' AND g.venda.mesa IS NOT NULL) OR
                      (:tipo = 'retirada' AND g.venda.retirada = true) OR
                      (:tipo = 'entrega' AND g.venda.entrega = true) OR
                      (:tipo = 'balcao' AND g.venda.balcao = true))
                  AND (:cupom IS NULL OR CAST(g.cupom AS string) LIKE %:cupom%)
            """)
    List<GestaoCaixa> buscarHistoricoComFiltro(
            @Param("matrizId") Long matrizId,
            @Param("tipo") String tipo,
            @Param("cupom") String cupom
    );

    @Query("""
                SELECT g FROM GestaoCaixa g
                WHERE g.ativo = false
                  AND g.matriz.id = :matrizId
                  AND g.venda.consumoInterno = true
                  AND (:tipo IS NULL OR
                      (:tipo = 'mesa' AND g.venda.mesa IS NOT NULL) OR
                      (:tipo = 'retirada' AND g.venda.retirada = true) OR
                      (:tipo = 'entrega' AND g.venda.entrega = true) OR
                      (:tipo = 'balcao' AND g.venda.balcao = true))
                  AND (:cupom IS NULL OR CAST(g.cupom AS string) LIKE %:cupom%)
            """)
    List<GestaoCaixa> buscarConsumosHistoricoComFiltro(
            @Param("matrizId") Long matrizId,
            @Param("tipo") String tipo,
            @Param("cupom") String cupom
    );
}