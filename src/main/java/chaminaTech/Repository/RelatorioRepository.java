package chaminaTech.Repository;

import chaminaTech.Entity.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}