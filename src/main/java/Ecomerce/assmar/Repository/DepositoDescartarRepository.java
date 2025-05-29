package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.DepositoDescartar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepositoDescartarRepository extends JpaRepository<DepositoDescartar, Long> {
    @Query("""
                SELECT d FROM DepositoDescartar d
                WHERE d.matriz.id = :matrizId
                  AND (:materiaNome IS NULL OR CAST(d.materia.nome AS string) LIKE %:materiaNome%)
                ORDER BY d.id ASC
            """)
    List<DepositoDescartar> listarDepositosDescartados(
            @Param("matrizId") Long matrizId,
            @Param("materiaNome") String materiaNome
    );
}
