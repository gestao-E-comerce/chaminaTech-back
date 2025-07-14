package chaminaTech.Repository;

import chaminaTech.Entity.AppImpressaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppImpressaoTokenRepository extends JpaRepository<AppImpressaoToken, Long> {
    boolean existsByTokenAndMatrizIdAndAtivoTrue(String token, Long matrizId);
}
