package chaminaTech.Repository;

import chaminaTech.Entity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
    @Query("FROM Permissao p WHERE p.usuario.id = :usuarioId ORDER BY p.id ASC")
    List<Permissao> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("""
                SELECT p FROM Permissao p
                WHERE p.usuario.id = :usuarioId AND p.nome = :nome
            """)
    Optional<Permissao> findByNomeAndUsuarioId(@Param("nome") String nome, @Param("usuarioId") Long usuarioId);

    @Query("""
                SELECT p FROM Permissao p
                WHERE p.usuario.id = :usuarioId AND p.nome = :nome AND p.id <> :id
            """)
    Optional<Permissao> findByNomeAndUsuarioIdAndIdNot(
            @Param("nome") String nome,
            @Param("usuarioId") Long usuarioId,
            @Param("id") Long id
    );

    @Query("""
                SELECT COUNT(u) > 0 FROM Usuario u
                WHERE u.permissao.id = :permissaoId
                AND u.deletado = false
            """)
    boolean existeUsuarioComPermissao(@Param("permissaoId") Long permissaoId);
}