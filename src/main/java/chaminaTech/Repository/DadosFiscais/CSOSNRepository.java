package chaminaTech.Repository.DadosFiscais;

import chaminaTech.Entity.DadosFiscais.CSOSN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CSOSNRepository extends JpaRepository<CSOSN, Long> {

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT COUNT(c) > 0
                FROM CSOSN c
                WHERE c.codigo = :codigo AND c.id <> :id
            """)
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}