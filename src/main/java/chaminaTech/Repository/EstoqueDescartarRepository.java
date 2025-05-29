package chaminaTech.Repository;

import chaminaTech.Entity.EstoqueDescartar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstoqueDescartarRepository extends JpaRepository<EstoqueDescartar, Long> {
    @Query("""
                SELECT e FROM EstoqueDescartar e
                WHERE e.matriz.id = :matrizId
                  AND (:produtoNome IS NULL OR CAST(e.produto.nome AS string) LIKE %:produtoNome%)
                  ORDER BY e.id ASC
            """)
    List<EstoqueDescartar> listarEstoquesDescartados(
            @Param("matrizId") Long matrizId,
            @Param("produtoNome") String produtoNome
    );
}