package chaminaTech.Repository;

import chaminaTech.Entity.Sangria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface SangriaRepository extends JpaRepository<Sangria, Long> {
    @Query("SELECT COALESCE(SUM(s.valor), 0) FROM Sangria s WHERE s.caixa.id = :caixaId")
    BigDecimal findTotalSangriasByCaixaId(@Param("caixaId") Long caixaId);
}
