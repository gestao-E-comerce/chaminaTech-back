package chaminaTech.Repository;

import chaminaTech.Entity.Configuracao.ConfiguracaoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoEntregaRepository extends JpaRepository<ConfiguracaoEntrega, Long>  {
    ConfiguracaoEntrega findByMatrizId(Long matrizId);

}
