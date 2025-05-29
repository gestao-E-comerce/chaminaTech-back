package chaminaTech.Repository;

import chaminaTech.Entity.Suprimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SuprimentoRepository extends JpaRepository<Suprimento, Long> {
    @Query("SELECT COALESCE(SUM(s.valor), 0) FROM Suprimento s WHERE s.caixa.id = :caixaId")
    Double findTotalSuprimentosByCaixaId(@Param("caixaId") Long caixaId);
}
