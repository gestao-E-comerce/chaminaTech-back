package chaminaTech.Service;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoTaxaServicoDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Configuracao.ConfiguracaoTaxaServico;
import chaminaTech.Repository.ConfiguracaoTaxaServicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracaoTaxaServicoService {
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private ConfiguracaoTaxaServicoRepository configuracaoTaxaServicoRepository;
    @Autowired
    private AuditoriaService auditoriaService;

    public ConfiguracaoTaxaServicoDTO buscarConfiguracaoTaxaServicio(Long matrizId) {
        ConfiguracaoTaxaServico configuracaoTaxaServico = configuracaoTaxaServicoRepository.findByMatrizId(matrizId);
        return configuracaoTaxaServico != null ?entityToDTO.configuracaoTaxaServicioToDTO(configuracaoTaxaServico) : null;
    }

    @Transactional
    public MensagemDTO salvarConfiguracaoTaxaServicio(ConfiguracaoTaxaServicoDTO configuracaoTaxaServicoDTO) {
        PermissaoUtil.validarOuLancar("editarConfiguracoes");
        ConfiguracaoTaxaServico configuracaoTaxaServico = dtoToEntity.DTOToConfiguracaoTaxaServicio(configuracaoTaxaServicoDTO);

        configuracaoTaxaServicoRepository.save(configuracaoTaxaServico);

        // Registrar a auditoria
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CONFIGURACAO",
                "Editou configurações de taxa de serviço",
                PermissaoUtil.getUsuarioLogado().getNome(),
                configuracaoTaxaServico.getMatriz().getId()
        );

        return new MensagemDTO("Configurações de taxa de serviço atualizadas com sucesso", HttpStatus.OK);
    }
}
