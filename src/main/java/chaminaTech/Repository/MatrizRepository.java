package chaminaTech.Repository;

import chaminaTech.Entity.Matriz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatrizRepository extends JpaRepository<Matriz, Long> {
    @Query("FROM Matriz WHERE matriz IS NULL and ativo = true ORDER BY id ASC")
    List<Matriz> findAllMatrizes();

    @Query("FROM Matriz WHERE matriz IS NOT NULL and ativo = true ORDER BY id ASC")
    List<Matriz> findAllFilhos();

    List<Matriz> findByMatrizIdAndAtivo(Long matrizId, boolean ativo);

    boolean existsByNomeAndDeletado(String nome, boolean deletado);

    @Query("SELECT COUNT(m) > 0 FROM Matriz m WHERE m.nome = :nome AND m.deletado = false AND m.id <> :idAtual")
    boolean existsByNomeAndDeletadoAndNotId(@Param("nome") String nome, @Param("idAtual") Long idAtual);

}
