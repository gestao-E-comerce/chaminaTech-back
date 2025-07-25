package chaminaTech.Repository.DadosFiscais;

import chaminaTech.Entity.DadosFiscais.Origem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrigemRepository extends JpaRepository<Origem, Long> {

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT COUNT(o) > 0
                FROM Origem o
                WHERE o.codigo = :codigo AND o.id <> :id
            """)
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}