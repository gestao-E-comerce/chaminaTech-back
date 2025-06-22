package chaminaTech.Repository;

import chaminaTech.Entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    List<Funcionario> findByMatrizId(Long matrizId);

    @Query("""
                SELECT f FROM Funcionario f
                WHERE f.matriz.id = :matrizId
                  AND f.deletado = false
                  AND (:termoPesquisa IS NULL OR CAST(f.nome AS string) LIKE %:termoPesquisa%)
                  AND (:ativo IS NULL OR f.ativo = :ativo)
                  ORDER BY f.id ASC
            """)
    List<Funcionario> buscarFuncionarios(
            @Param("matrizId") Long matrizId,
            @Param("termoPesquisa") String termoPesquisa,
            @Param("ativo") Boolean ativo);

    @Query("""
            SELECT COUNT(f) > 0 FROM Funcionario f
            WHERE f.matriz.id = :matrizId
            AND f.nome = :nome
            AND f.deletado = :deletado
            """)
    boolean existsByNomeAndMatrizIdAndDeletado(@Param("matrizId") Long matrizId, @Param("nome") String nome, @Param("deletado") boolean deletado);

    @Query("""
            SELECT COUNT(f) > 0 FROM Funcionario f
            WHERE f.matriz.id = :matrizId
            AND f.nome = :nome
            AND f.deletado = :deletado
            AND f.id != :funcionarioId
            """)
    boolean existsByNomeAndMatrizIdAndDeletadoAndNotId(@Param("matrizId") Long matrizId, @Param("nome") String nome, @Param("deletado") boolean deletado, @Param("funcionarioId") Long funcionarioId);

    @Query("SELECT COUNT(v) > 0 FROM Venda v " +
            "WHERE v.funcionario.id = :funcionarioId " +
            "AND v.ativo = true " +
            "AND v.deletado = false")
    boolean existsByFuncionarioEmVendaAtiva(@Param("funcionarioId") Long funcionarioId);

    @Query("SELECT COUNT(c) > 0 FROM Caixa c " +
            "WHERE c.funcionario.id = :funcionarioId " +
            "AND c.ativo = true " +
            "AND c.deletado = false")
    boolean existsCaixaAtivoPorFuncionario(@Param("funcionarioId") Long funcionarioId);

    @Query("""
                SELECT COUNT(f) FROM Funcionario f
                WHERE f.matriz.id = :matrizId
                AND f.deletado = false
                AND f.ativo = true
            """)
    int contarFuncionariosAtivosPorMatriz(@Param("matrizId") Long matrizId);
}
