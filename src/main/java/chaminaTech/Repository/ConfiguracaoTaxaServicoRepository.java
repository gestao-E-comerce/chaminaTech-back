package chaminaTech.Repository;

import chaminaTech.Entity.Configuracao.ConfiguracaoTaxaServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoTaxaServicoRepository extends JpaRepository<ConfiguracaoTaxaServico, Long> {
    ConfiguracaoTaxaServico findByMatrizId(Long matrizId);
}
