package chaminaTech.Repository.DadosFiscais;

import chaminaTech.Entity.DadosFiscais.CSTCOFINS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CSTCOFINSRepository extends JpaRepository<CSTCOFINS, Long> {

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT COUNT(c) > 0
                FROM CSTCOFINS c
                WHERE c.codigo = :codigo AND c.id <> :id
            """)
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}