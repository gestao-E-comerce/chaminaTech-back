package chaminaTech.Repository;

import chaminaTech.Entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    @Query("""
                SELECT m FROM Materia m
                WHERE m.matriz.id = :matrizId
                  AND m.deletado = false
                  AND (:termoPesquisa IS NULL OR CAST(m.nome AS string) LIKE %:termoPesquisa%)
                  AND (:ativo IS NULL OR m.ativo = :ativo)
                  ORDER BY m.id ASC
            """)
    List<Materia> listarMaterias(
            @Param("matrizId") Long matrizId,
            @Param("termoPesquisa") String termoPesquisa,
            @Param("ativo") Boolean ativo
    );

    @Query("""
                SELECT m, COALESCE(SUM(d.quantidade - d.quantidadeVendido), 0)
                FROM Materia m
                LEFT JOIN Deposito d ON d.materia = m AND d.deletado = false AND d.ativo = true AND d.matriz.id = m.matriz.id
                WHERE m.matriz.id = :matrizId
                  AND m.deletado = false
                  AND (:termoPesquisa IS NULL OR CAST(m.nome AS string) LIKE %:termoPesquisa%)
                GROUP BY m
            """)
    List<Object[]> listarMateriasDepositos(@Param("matrizId") Long matrizId, @Param("termoPesquisa") String termoPesquisa);

    @Query("""
                SELECT m, COALESCE(SUM(COALESCE(d.quantidade, 0)), 0)
                FROM Materia m
                LEFT JOIN DepositoDescartar d ON d.materia.id = m.id AND d.matriz.id = m.matriz.id
                WHERE m.matriz.id = :matrizId
                  AND m.deletado = false
                  AND (:termoPesquisa IS NULL OR CAST(m.nome AS string) LIKE %:termoPesquisa%)
                GROUP BY m.id
            """)
    List<Object[]> listarMateriasDepositosDescartados(
            @Param("matrizId") Long matrizId,
            @Param("termoPesquisa") String termoPesquisa
    );


    @Query("""
            SELECT COUNT(m) > 0 FROM Materia m
            WHERE m.matriz.id = :matrizId
            AND m.nome = :nome
            AND m.deletado = :deletado
            """)
    boolean existsByNomeAndMatrizIdAndDeletado(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome,
            @Param("deletado") Boolean deletado
    );

    @Query("""
            SELECT COUNT(m) > 0 FROM Materia m
            WHERE m.matriz.id = :matrizId
            AND m.nome = :nome
            AND m.deletado = :deletado
            AND m.id != :materiaId
            """)
    boolean existsByNomeAndMatrizIdAndDeletadoAndNotId(
            @Param("matrizId") Long matrizId,
            @Param("nome") String nome,
            @Param("deletado") Boolean deletado,
            @Param("materiaId") Long materiaId
    );


    @Query("SELECT COUNT(p) > 0 FROM ProdutoMateria p " +
            "WHERE p.materia.id = :materiaId")
    boolean existsByMateriaEmProdutoMateria(@Param("materiaId") Long materiaId);

    @Query("SELECT COUNT(d) > 0 FROM Deposito d " +
            "WHERE d.materia.id = :materiaId " +
            "AND d.ativo = true" )
    boolean existsByMateriaEmDepositoAtivo(@Param("materiaId") Long materiaId);

    @Query("SELECT COUNT(om) > 0 FROM ObservacaoMateria om " +
            "WHERE om.materia.id = :materiaId")
    boolean existsByMateriaEmObservacao(@Param("materiaId") Long materiaId);
}