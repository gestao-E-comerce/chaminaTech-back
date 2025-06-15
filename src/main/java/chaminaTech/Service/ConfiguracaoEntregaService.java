package chaminaTech.Service;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoEntregaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Configuracao.ConfiguracaoEntrega;
import chaminaTech.Entity.TaxaEntregaKm;
import chaminaTech.Repository.ConfiguracaoEntregaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConfiguracaoEntregaService {
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private ConfiguracaoEntregaRepository configuracaoEntregaRepository;
    @Autowired
    private AuditoriaService auditoriaService;

    public ConfiguracaoEntregaDTO buscarConfiguracaoEntrega(Long matrizId) {
        ConfiguracaoEntrega configuracaoEntrega = configuracaoEntregaRepository.findByMatrizId(matrizId);

        return configuracaoEntrega != null ? entityToDTO.configuracaoEntregaToDTO(configuracaoEntrega) : null;
    }

    @Transactional
    public MensagemDTO salvarConfiguracaoEntrega(ConfiguracaoEntregaDTO configuracaoEntregaDTO) {
        PermissaoUtil.validarOuLancar("editarConfiguracoes");

        ConfiguracaoEntrega configuracaoEntrega = dtoToEntity.DTOToConfiguracaoEntrega(configuracaoEntregaDTO);

        // ✅ Remove duplicados e associa à configuração
        Set<Integer> kmsVistos = new HashSet<>();
        List<TaxaEntregaKm> taxasFiltradas = new ArrayList<>();

        for (TaxaEntregaKm taxa : configuracaoEntrega.getTaxasEntregaKm()) {
            if (taxa.getKm() != null && !kmsVistos.contains(taxa.getKm())) {
                kmsVistos.add(taxa.getKm());
                taxa.setConfiguracaoEntrega(configuracaoEntrega);
                taxasFiltradas.add(taxa);
            }
        }

        configuracaoEntrega.setTaxasEntregaKm(taxasFiltradas);

        configuracaoEntregaRepository.save(configuracaoEntrega);

        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CONFIGURACAO",
                "Editou configurações de entrega",
                PermissaoUtil.getUsuarioLogado().getNome(),
                configuracaoEntrega.getMatriz().getId()
        );

        return new MensagemDTO("Configurações de entrega atualizadas com sucesso", HttpStatus.OK);
    }
}
