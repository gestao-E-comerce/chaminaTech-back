package chaminaTech.Repository;

import chaminaTech.Entity.Configuracao.ConfiguracaoRetirada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoRetiradaRepository extends JpaRepository<ConfiguracaoRetirada, Long>  {
    ConfiguracaoRetirada findByMatrizId(Long matrizId);
}
