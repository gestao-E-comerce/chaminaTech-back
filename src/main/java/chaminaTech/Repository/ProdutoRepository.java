package chaminaTech.Repository;

import chaminaTech.Entity.Impressora;
import chaminaTech.Entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Query("""
                SELECT p FROM Produto p
                WHERE p.matriz.id = :matrizId
                AND p.deletado = false
                  AND (:ativo IS NULL OR p.ativo = :ativo)
                  AND (:cardapio IS NULL OR p.cardapio = :cardapio)
                  AND (:estocavel IS NULL OR p.estocavel = :estocavel)
                  AND (:validarExestencia IS NULL OR p.validarExestencia = :validarExestencia)
                  AND (:categoriaNome IS NULL OR CAST(p.categoria.nome AS string) LIKE %:categoriaNome%)
                  AND (:nome IS NULL OR CAST(p.nome AS string) LIKE %:nome%)
               ORDER BY p.codigo ASC
            """)
    List<Produto> listarProdutos(
            @Param("matrizId") Long matrizId,
            @Param("ativo") Boolean ativo,
            @Param("cardapio") Boolean cardapio,
            @Param("estocavel") Boolean estocavel,
            @Param("validarExestencia") Boolean validarExestencia,
            @Param("categoriaNome") String categoriaNome,
            @Param("nome") String nome
    );

    @Query("""
                SELECT p, COALESCE(SUM(e.quantidade - e.quantidadeVendido), 0)
                FROM Produto p
                LEFT JOIN Estoque e ON e.produto = p AND e.deletado = false AND e.ativo = true AND e.matriz.id = p.matriz.id
                WHERE p.matriz.id = :matrizId
                  AND p.deletado = false
                  AND p.estocavel = true
                  AND (:termoPesquisa IS NULL OR CAST(p.nome AS string) LIKE %:termoPesquisa%)
                GROUP BY p
                ORDER BY p.codigo ASC
            """)
    List<Object[]> listarProdutosEstoques(@Param("matrizId") Long matrizId, @Param("termoPesquisa") String termoPesquisa);

    @Query("""
                SELECT p, COALESCE(SUM(COALESCE(e.quantidade, 0)), 0)
                FROM Produto p
                LEFT JOIN EstoqueDescartar e ON e.produto = p AND e.matriz.id = p.matriz.id
                WHERE p.matriz.id = :matrizId
                  AND p.deletado = false
                  AND p.estocavel = true
                  AND (:termoPesquisa IS NULL OR CAST(p.nome AS string) LIKE %:termoPesquisa%)
                GROUP BY p
                ORDER BY p.codigo ASC
            """)
    List<Object[]> listarProdutosEstoquesDescartados(@Param("matrizId") Long matrizId, @Param("termoPesquisa") String termoPesquisa);

//    @Query("SELECT p FROM Produto p WHERE p.matriz.id = :matrizId AND p.ativo = :ativo AND p.validarExestencia = true AND p.estocavel = true")
//    List<Produto> findByMatrizIdAndAtivoAndValidarExestenciaTrue(@Param("matrizId") Long matrizId, @Param("ativo") Boolean ativo);

    @Query("SELECT p FROM Produto p WHERE p.codigo = :codigo AND p.ativo = true AND p.matriz.id = :matrizId AND p.deletado = false AND p.cardapio = true")
    Optional<Produto> findByCodigoAndMatrizIdAndAtivo(@Param("codigo") Integer codigo, @Param("matrizId") Long matrizId);

    @Query("""
            SELECT COUNT(p) > 0 FROM Produto p
            WHERE p.matriz.id = :matrizId
            AND p.nome = :nome
            AND p.deletado = :deletado
            """)
    boolean existsByNomeAndMatrizIdAndDeletado(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome,
            @Param("deletado") Boolean deletado
    );

    @Query("""
            SELECT COUNT(p) > 0 FROM Produto p
            WHERE p.matriz.id = :matrizId
            AND p.nome = :nome
            AND p.deletado = :deletado
            AND p.id != :produtoId
            """)
    boolean existsByNomeAndMatrizIdAndDeletadoAndNotId(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome,
            @Param("deletado") Boolean deletado,
            @Param("produtoId") Long produtoId
    );

    @Query("""
            SELECT COUNT(p) > 0 FROM Produto p
            WHERE p.matriz.id = :matrizId
            AND p.codigo = :codigo
            AND p.deletado = :deletado
            """)
    boolean existsByCodigoAndMatrizIdAndDeletado(
            @Param("matrizId") Long matrizId,
            @Param("codigo") Integer codigo,
            @Param("deletado") Boolean deletado
    );

    @Query("""
            SELECT COUNT(p) > 0 FROM Produto p
            WHERE p.matriz.id = :matrizId
            AND p.codigo = :codigo
            AND p.deletado = :deletado
            AND p.id != :produtoId
            """)
    boolean existsByCodigoAndMatrizIdAndDeletadoAndNotId(
            @Param("matrizId") Long matrizId,
            @Param("codigo") Integer codigo,
            @Param("deletado") Boolean deletado,
            @Param("produtoId") Long produtoId
    );

    @Query("""
                SELECT COUNT(v) > 0
                FROM Venda v
                JOIN v.produtoVendas pv
                WHERE v.ativo = true
                AND pv.produto.id = :produtoId
            """)
    boolean existsEmVendaAtiva(@Param("produtoId") Long produtoId);

    @Query("SELECT COUNT(e) > 0 FROM Estoque e WHERE e.produto.id = :produtoId AND e.ativo = true")
    boolean existsEmEstoqueAtivo(@Param("produtoId") Long produtoId);

    // 3. Produto em observação
    @Query("SELECT COUNT(op) > 0 FROM ObservacaoProduto op WHERE op.produto.id = :produtoId")
    boolean existsEmObservacao(@Param("produtoId") Long produtoId);

    // 4. Produto em ProdutoMateria
    @Query("SELECT COUNT(pm) > 0 FROM ProdutoMateria pm WHERE pm.produto.id = :produtoId")
    boolean existsEmProdutoMateria(@Param("produtoId") Long produtoId);

    // 5. Produto em ProdutoComposto
    @Query("SELECT COUNT(pc) > 0 FROM ProdutoComposto pc WHERE pc.produtoComposto.id = :produtoId")
    boolean existsEmProdutoComposto(@Param("produtoId") Long produtoId);

    // regra matriz
    @Query("SELECT p FROM Produto p JOIN p.impressoras i WHERE i = :impressora AND p.matriz.id = :matrizId")
    List<Produto> findProdutosByImpressoraAndMatriz(@Param("impressora") Impressora impressora, @Param("matrizId") Long matriz);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.categoria.id = :categoriaId")
    List<Produto> findByCategoriaId(@Param("categoriaId") Long categoriaId);

}