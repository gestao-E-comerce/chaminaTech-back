package chaminaTech.Repository;

import chaminaTech.Entity.Impressao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImpressaoRepository extends JpaRepository<Impressao, Long> {
    List<Impressao> findByMatrizIdAndStatus(Long matrizId, Boolean status);
}
