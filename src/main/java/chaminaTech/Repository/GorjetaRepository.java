package chaminaTech.Repository;

import chaminaTech.Entity.Gorjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface GorjetaRepository extends JpaRepository<Gorjeta, Long> {
    @Query("""
                SELECT
                    COALESCE(SUM(COALESCE(g.dinheiro, 0)), 0) +
                    COALESCE(SUM(COALESCE(g.debito, 0)), 0) +
                    COALESCE(SUM(COALESCE(g.credito, 0)), 0) +
                    COALESCE(SUM(COALESCE(g.pix, 0)), 0)
                FROM Gorjeta g
                WHERE g.caixa.id = :caixaId
            """)
    BigDecimal findTotalGorjetasByCaixaId(@Param("caixaId") Long caixaId);

    @Query("""
                SELECT COALESCE(SUM(g.dinheiro), 0)
                FROM Gorjeta g
                WHERE g.caixa.id = :caixaId
            """)
    BigDecimal findTotalGorjetasDinheiroByCaixaId(@Param("caixaId") Long caixaId);
}
