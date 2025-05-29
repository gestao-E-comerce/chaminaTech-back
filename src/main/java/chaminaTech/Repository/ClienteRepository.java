package chaminaTech.Repository;

import chaminaTech.Entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByMatrizIdAndAtivoOrderByIdAsc(Long matrizId, boolean ativo);

    @Query("""
            SELECT COUNT(c) > 0 FROM Cliente c
            WHERE c.matriz.id = :matrizId
            AND c.nome = :nome
            AND c.ativo = :ativo
            """)
    boolean existsByNomeAndMatrizIdAndAtivo(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome,
            @Param("ativo") boolean ativo
    );

    @Query("""
            SELECT COUNT(c) > 0 FROM Cliente c
            WHERE c.matriz.id = :matrizId
            AND c.nome = :nome
            AND c.ativo = :ativo
            AND c.id != :clienteId
            """)
    boolean existsByNomeAndMatrizIdAndAtivoAndNotId(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome,
            @Param("ativo") boolean ativo,
            @Param("clienteId") Long clienteId
    );


    @Query("""
                SELECT c FROM Cliente c
                WHERE c.ativo = :ativo
                  AND c.matriz.id = :matrizId
                  AND CAST(c.nome AS string) LIKE %:termoPesquisa%
                ORDER BY c.id ASC
            """)
    List<Cliente> buscarClientesPorNome(
            @Param("matrizId") Long matrizId,
            @Param("ativo") boolean ativo,
            @Param("termoPesquisa") String termoPesquisa
    );

    @Query("""
                SELECT c FROM Cliente c
                WHERE c.ativo = :ativo
                  AND c.matriz.id = :matrizId
                  AND CAST(c.cpf AS string) LIKE %:termoPesquisa%
                ORDER BY c.id ASC
            """)
    List<Cliente> buscarClientesPorCpf(
            @Param("matrizId") Long matrizId,
            @Param("ativo") boolean ativo,
            @Param("termoPesquisa") String termoPesquisa
    );

    @Query("""
                SELECT c FROM Cliente c
                WHERE c.ativo = :ativo
                  AND c.matriz.id = :matrizId
                  AND CAST(c.celular AS string) LIKE %:termoPesquisa%
                ORDER BY c.id ASC
            """)
    List<Cliente> buscarClientesPorCelular(
            @Param("matrizId") Long matrizId,
            @Param("ativo") boolean ativo,
            @Param("termoPesquisa") String termoPesquisa
    );

    @Query("""
                SELECT c FROM Cliente c
                JOIN c.enderecos e
                WHERE c.ativo = :ativo
                  AND c.matriz.id = :matrizId
                  AND e.cep LIKE %:termoPesquisa%
                ORDER BY c.id ASC
            """)
    List<Cliente> buscarClientesPorCep(
            @Param("matrizId") Long matrizId,
            @Param("ativo") boolean ativo,
            @Param("termoPesquisa") String termoPesquisa
    );

    @Query("SELECT COUNT(v) > 0 FROM Venda v " +
            "WHERE v.endereco.id = :enderecoId " +
            "AND v.ativo = true " +
            "AND v.matriz.id = :matrizId")
    boolean existsByEnderecoEmVendaAtiva(@Param("enderecoId") Long enderecoId,
                                         @Param("matrizId") Long matrizId);

    @Query("SELECT COUNT(v) > 0 FROM Venda v " +
            "WHERE v.cliente.id = :clienteId " +
            "AND v.ativo = true " +
            "AND v.matriz.id = :matrizId")
    boolean existsByClienteEmVendaAtiva(@Param("clienteId") Long clienteId,
                                        @Param("matrizId") Long matrizId);
}