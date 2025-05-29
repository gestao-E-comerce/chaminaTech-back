package chaminaTech.Repository;

import chaminaTech.Entity.AdminFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminFuncionarioRepository extends JpaRepository<AdminFuncionario, Long> {
    @Query("""
                SELECT f FROM AdminFuncionario f
                WHERE f.admin.id = :adminId
                  AND f.deletado = :deletado
                  AND (:termoPesquisa IS NULL OR CAST(f.nome AS string) LIKE %:termoPesquisa%)
                  AND (:ativo IS NULL OR f.ativo = :ativo)
                  ORDER BY f.id ASC
            """)
    List<AdminFuncionario> buscarFuncionarios(
            @Param("adminId") Long adminId,
            @Param("deletado") Boolean deletado,
            @Param("termoPesquisa") String termoPesquisa,
            @Param("ativo") Boolean ativo);

    @Query("""
            SELECT COUNT(f) > 0 FROM AdminFuncionario f
            WHERE f.admin.id = :adminId
            AND f.nome = :nome
            AND f.deletado = :deletado
            """)
    boolean existsByNomeAndAdminIdAndDeletado(@Param("adminId") Long adminId, @Param("nome") String nome, @Param("deletado") boolean deletado);

    @Query("""
            SELECT COUNT(f) > 0 FROM AdminFuncionario f
            WHERE f.admin.id = :adminId
            AND f.nome = :nome
            AND f.deletado = :deletado
            AND f.id != :funcionarioId
            """)
    boolean existsByNomeAndAdminIdAndDeletadoAndNotId(@Param("adminId") Long adminId, @Param("nome") String nome, @Param("deletado") boolean deletado, @Param("funcionarioId") Long funcionarioId);
}
