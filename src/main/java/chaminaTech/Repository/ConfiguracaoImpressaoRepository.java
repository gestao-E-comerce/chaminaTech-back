package chaminaTech.Repository;

import chaminaTech.Entity.Configuracao.ConfiguracaoImpressao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoImpressaoRepository extends JpaRepository<ConfiguracaoImpressao, Long>  {
    ConfiguracaoImpressao findByMatrizId(Long matrizId);
}
