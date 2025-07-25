package chaminaTech.Repository.DadosFiscais;

import chaminaTech.Entity.DadosFiscais.CSTPIS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CSTPISRepository extends JpaRepository<CSTPIS, Long> {

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT COUNT(c) > 0
                FROM CSTPIS c
                WHERE c.codigo = :codigo AND c.id <> :id
            """)
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}