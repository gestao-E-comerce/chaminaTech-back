package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}