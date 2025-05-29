package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.Estoque;
import Ecomerce.assmar.Entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    @Query("""
                SELECT e FROM Estoque e
                WHERE e.matriz.id = :matrizId
                AND e.deletado = :deletado
                  AND (:ativo IS NULL OR e.ativo = :ativo)
                  AND (:produtoNome IS NULL OR CAST(e.produto.nome AS string) LIKE %:produtoNome%)
                  ORDER BY e.id ASC
            """)
    List<Estoque> listarEstoques(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("ativo") Boolean ativo,
            @Param("produtoNome") String produtoNome
    );
    @Query("SELECT SUM(e.quantidade - e.quantidadeVendido) FROM Estoque e WHERE e.ativo = true AND e.deletado = false AND e.produto.id = :produtoId AND e.matriz.id = :matrizId")
    BigDecimal findTotalQuantidadeDisponivelByProdutoAndMatriz(@Param("produtoId") Long produtoId, @Param("matrizId") Long matrizId);
    @Query("SELECT e FROM Estoque e WHERE e.produto = :produto AND e.matriz.id = :matrizId AND e.ativo = false AND e.deletado = false ORDER BY e.dataCadastrar DESC")
    List<Estoque> findByProdutoAndMatrizIdAndAtivoFalseOrderByDataCadastrarDesc(@Param("produto") Produto produto, @Param("matrizId") Long matrizId);
    @Query("SELECT e FROM Estoque e WHERE e.produto = :produto AND e.matriz.id = :matrizId AND e.ativo = true AND e.deletado = false ORDER BY e.dataCadastrar ASC")
    List<Estoque> findByProdutoAndMatrizIdAndAtivoTrueOrderByDataAsc(@Param("produto") Produto produto,@Param("matrizId") Long matrizId);
    @Query("SELECT e FROM Estoque e WHERE e.produto = :produto AND e.matriz.id = :matrizId AND e.ativo = true AND e.deletado = false")
    List<Estoque> findByProdutoAndMatrizIdAndAtivoTrue(@Param("produto") Produto produto, @Param("matrizId") Long matrizId);

}