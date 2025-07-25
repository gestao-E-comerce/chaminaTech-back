package chaminaTech.Repository.DadosFiscais;

import chaminaTech.Entity.DadosFiscais.CST;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CSTRepository extends JpaRepository<CST, Long> {

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT COUNT(c) > 0
                FROM CST c
                WHERE c.codigo = :codigo AND c.id <> :id
            """)
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}