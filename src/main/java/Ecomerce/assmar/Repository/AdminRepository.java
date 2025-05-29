package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.Admin;
import Ecomerce.assmar.Entity.AdminFuncionario;
import Ecomerce.assmar.Entity.Matriz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Query("""
                SELECT m FROM Matriz m
                WHERE m.matriz IS NULL
                  AND (:deletado IS NULL OR m.deletado = :deletado)
                  AND (:termoPesquisa IS NULL OR CAST(m.nome AS string) LIKE %:termoPesquisa%)
                  AND (:ativo IS NULL OR m.ativo = :ativo)
                ORDER BY m.id ASC
            """)
    List<Matriz> findAllMatrizes(
            @Param("deletado") Boolean deletado,
            @Param("termoPesquisa") String termoPesquisa,
            @Param("ativo") Boolean ativo
    );

    @Query("""
                SELECT m FROM Matriz m
                WHERE m.matriz IS NOT NULL
                  AND (:deletado IS NULL OR m.deletado = :deletado)
                  AND (:termoPesquisa IS NULL OR CAST(m.nome AS string) LIKE %:termoPesquisa%)
                  AND (:ativo IS NULL OR m.ativo = :ativo)
                ORDER BY m.id ASC
            """)
    List<Matriz> findAllFilhos(
            @Param("deletado") Boolean deletado,
            @Param("termoPesquisa") String termoPesquisa,
            @Param("ativo") Boolean ativo
    );

    Optional<Admin> findByRole(String role);
}
