package chaminaTech.Repository;

import chaminaTech.Entity.Caixa;
import chaminaTech.Entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {
    @Query("SELECT c FROM Caixa c WHERE c.funcionario.id = :funcionarioId AND c.ativo = true")
    Optional<Caixa> findCaixaAtivaByFuncionarioId(@Param("funcionarioId") Long funcionarioId);

    @Query("""
                SELECT c FROM Caixa c
                WHERE (:nome IS NULL OR c.funcionario.nome LIKE %:nome%)
                AND c.deletado = false
                AND c.matriz.id = :matrizId
                AND (:tipo IS NULL OR
                     (:tipo = 'aberta' AND c.ativo = true) OR
                     (:tipo = 'fechada' AND c.ativo = false))
                ORDER BY c.id ASC
            """)
    List<Caixa> findCaixasByNomeAndMatrizId(
            @Param("nome") String nome,
            @Param("matrizId") Long matrizId,
            @Param("tipo") String tipo  // O tipo deve ser String para poder aceitar 'aberto' ou 'fechado'
    );

    @Query("SELECT v FROM Venda v WHERE v.ativo = true AND v.matriz.id = :matrizId")
    List<Venda> findVendasAtivasByMatrizId(@Param("matrizId") Long matrizId);

    @Query("SELECT SUM(v.valorBruto) FROM Venda v WHERE v.caixa.id = :caixaId")
    BigDecimal findTotalVendasBrutoByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.pix) FROM VendaPagamento vp JOIN vp.venda v WHERE v.caixa.id = :caixaId")
    BigDecimal findTotalPixByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.dinheiro) FROM VendaPagamento vp WHERE vp.venda.caixa.id = :caixaId")
    BigDecimal findTotalDinheiroByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.debito) FROM VendaPagamento vp JOIN vp.venda v WHERE v.caixa.id = :caixaId")
    BigDecimal findTotalDebitoByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.credito) FROM VendaPagamento vp JOIN vp.venda v WHERE v.caixa.id = :caixaId")
    BigDecimal findTotalCreditoByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(v.valorServico) FROM Venda v WHERE v.caixa.id = :caixaId")
    BigDecimal findTotalServiciosByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(v.desconto) FROM Venda v WHERE v.caixa.id = :caixaId")
    BigDecimal findTotalDescontosByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(s.valor) FROM Sangria s WHERE s.caixa.id = :caixaId")
    BigDecimal findTotalSangriasByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(s.valor) FROM Suprimento s WHERE s.caixa.id = :caixaId")
    BigDecimal findTotalSuprimentosByCaixaId(@Param("caixaId") Long caixaId);

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
                SELECT COALESCE(SUM(vp.servicoDinheiro), 0)
                FROM VendaPagamento vp
                WHERE vp.venda.caixa.id = :caixaId
            """)
    BigDecimal findTotalServicoDinheiroByCaixaId(@Param("caixaId") Long caixaId);

    @Query("""
                SELECT COALESCE(SUM(vp.descontoDinheiro), 0)
                FROM VendaPagamento vp
                WHERE vp.venda.caixa.id = :caixaId
            """)
    BigDecimal findTotalDescontoDinheiroByCaixaId(@Param("caixaId") Long caixaId);
}