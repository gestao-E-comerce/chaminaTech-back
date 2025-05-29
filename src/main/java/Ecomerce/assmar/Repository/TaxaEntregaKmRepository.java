package Ecomerce.assmar.Repository;

import Ecomerce.assmar.Entity.TaxaEntregaKm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxaEntregaKmRepository extends JpaRepository<TaxaEntregaKm, Long> {
    List<TaxaEntregaKm> findByMatrizIdOrderByKmAsc(Long matrizId);

}
