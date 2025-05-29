package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.Deposito;
import Ecomerce.assmar.Entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DepositoRepository extends JpaRepository<Deposito, Long> {
    @Query("""
                SELECT d FROM Deposito d
                WHERE d.matriz.id = :matrizId
                AND d.deletado = :deletado
                  AND (:ativo IS NULL OR d.ativo = :ativo)
                  AND (:materiaNome IS NULL OR CAST(d.materia.nome AS string) LIKE %:materiaNome%)
                  ORDER BY d.id ASC
            """)
    List<Deposito> listarDepositos(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("ativo") Boolean ativo,
            @Param("materiaNome") String materiaNome
    );

    @Query("SELECT SUM(d.quantidade - d.quantidadeVendido) FROM Deposito d WHERE d.ativo = true AND d.deletado = false AND d.materia.id = :materiaId AND d.matriz.id = :matrizId")
    BigDecimal findTotalQuantidadeDisponivelByMateriaAndMatriz(@Param("materiaId") Long materiaId, @Param("matrizId") Long matrizId);

    @Query("SELECT d FROM Deposito d WHERE d.materia = :materia AND d.matriz.id = :matrizId AND d.ativo = true AND d.deletado = false ORDER BY d.dataCadastrar ASC")
    List<Deposito> findByMateriaAndMatrizIdAndAtivoTrueOrderByDataAsc(@Param("materia") Materia materia, @Param("matrizId") Long matrizId);

    @Query("SELECT d FROM Deposito d WHERE d.materia = :materia AND d.matriz.id = :matrizId AND d.ativo = false AND d.deletado = false ORDER BY d.dataCadastrar DESC")
    List<Deposito> findByMateriaAndMatrizIdAndAtivoFalseOrderByDataCadastrarDesc(@Param("materia") Materia materia, @Param("matrizId") Long matrizId);
}