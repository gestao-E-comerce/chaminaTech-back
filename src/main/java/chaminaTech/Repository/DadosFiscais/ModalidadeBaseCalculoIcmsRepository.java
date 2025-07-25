package chaminaTech.Repository.DadosFiscais;

import chaminaTech.Entity.DadosFiscais.ModalidadeBaseCalculoIcms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ModalidadeBaseCalculoIcmsRepository extends JpaRepository<ModalidadeBaseCalculoIcms, Long> {

    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT COUNT(m) > 0
                FROM ModalidadeBaseCalculoIcms m
                WHERE m.codigo = :codigo AND m.id <> :id
            """)
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}