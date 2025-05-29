package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Usuario, Long> {

    @Query("FROM Usuario WHERE username = :login")
    public Optional<Usuario> findByUsername(String login);

    @Query(value = "SELECT password FROM usuario WHERE id = :id",nativeQuery = true)
    String findSenhaById(Long id);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.username = :username AND u.id <> :id")
    boolean existsByUsernameExcludingId(@Param("username") String username, @Param("id") Long id);

}
