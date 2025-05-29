package chaminaTech.Repository;

import chaminaTech.Entity.Caixa;
import chaminaTech.Entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT SUM(v.valorTotal) FROM Venda v WHERE v.caixa.id = :caixaId")
    Double findTotalVendasByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.pix) FROM VendaPagamento vp JOIN vp.venda v WHERE v.caixa.id = :caixaId")
    Double findTotalPixByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.dinheiro) FROM VendaPagamento vp WHERE vp.venda.caixa.id = :caixaId")
    Double findTotalDinheiroByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.debito) FROM VendaPagamento vp JOIN vp.venda v WHERE v.caixa.id = :caixaId")
    Double findTotalDebitoByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(vp.credito) FROM VendaPagamento vp JOIN vp.venda v WHERE v.caixa.id = :caixaId")
    Double findTotalCreditoByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(s.valor) FROM Sangria s WHERE s.caixa.id = :caixaId")
    Double findTotalSangriasByCaixaId(@Param("caixaId") Long caixaId);

    @Query("SELECT SUM(s.valor) FROM Suprimento s WHERE s.caixa.id = :caixaId")
    Double findTotalSuprimentosByCaixaId(@Param("caixaId") Long caixaId);
}