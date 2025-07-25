package chaminaTech.Repository.DadosFiscais;

import chaminaTech.Entity.DadosFiscais.CSTIPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CSTIPIRepository extends JpaRepository<CSTIPI, Long> {

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT COUNT(c) > 0
                FROM CSTIPI c
                WHERE c.codigo = :codigo AND c.id <> :id
            """)
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}