package chaminaTech.Repository;

import chaminaTech.Entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    @Query("""
                SELECT c FROM Categoria c
                WHERE c.matriz.id = :matrizId
                AND c.deletado = :deletado
                  AND (:ativo IS NULL OR c.ativo = :ativo)
                  AND (:nome IS NULL OR CAST(c.nome AS string) LIKE %:nome%)
                ORDER BY c.id ASC
            """)
    List<Categoria> listarCategorias(@Param("matrizId") Long matrizId, @Param("deletado") Boolean deletado, @Param("ativo") Boolean ativo, @Param("nome") String nome);

    @Query("""
            SELECT COUNT(c) > 0 FROM Categoria c
            WHERE c.matriz.id = :matrizId
            AND c.nome = :nome
            AND c.deletado = :deletado
            """)
    boolean existsByNomeAndMatrizIdAndDeletado(@Param("matrizId") Long matrizId, @Param("nome") String nome, @Param("deletado") Boolean deletado);

    @Query("""
            SELECT COUNT(c) > 0 FROM Categoria c
            WHERE c.matriz.id = :matrizId
            AND c.nome = :nome
            AND c.deletado = :deletado
            AND c.id != :produtoId
            """)
    boolean existsByNomeAndMatrizIdAndDeletadoAndNotId(@Param("matrizId") Long matrizId, @Param("nome") String nome, @Param("deletado") Boolean deletado, @Param("produtoId") Long produtoId);
}