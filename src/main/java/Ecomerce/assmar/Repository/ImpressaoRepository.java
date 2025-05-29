package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.Impressao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImpressaoRepository extends JpaRepository<Impressao, Long> {
    List<Impressao> findByMatrizIdAndStatus(Long matrizId, boolean status);
}
