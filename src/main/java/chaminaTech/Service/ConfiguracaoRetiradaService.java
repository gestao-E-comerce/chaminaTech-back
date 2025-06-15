package chaminaTech.Service;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoRetiradaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Configuracao.ConfiguracaoRetirada;
import chaminaTech.Repository.ConfiguracaoRetiradaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracaoRetiradaService {
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private ConfiguracaoRetiradaRepository configuracaoRetiradaRepository;
    @Autowired
    private AuditoriaService auditoriaService;

    public ConfiguracaoRetiradaDTO buscarConfiguracaoRetirada(Long matrizId) {
        ConfiguracaoRetirada configuracaoRetirada = configuracaoRetiradaRepository.findByMatrizId(matrizId);
        return configuracaoRetirada != null ? entityToDTO.configuracaoRetiradaToDTO(configuracaoRetirada) : null;
    }

    @Transactional
    public MensagemDTO salvarConfiguracaoRetirada(ConfiguracaoRetiradaDTO configuracaoRetiradaDTO) {
        PermissaoUtil.validarOuLancar("editarConfiguracoes");
        ConfiguracaoRetirada configuracaoRetirada = dtoToEntity.DTOToConfiguracaoRetirada(configuracaoRetiradaDTO);

        configuracaoRetiradaRepository.save(configuracaoRetirada);

        // Registrar a auditoria
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CONFIGURACAO",
                "Editou configurações de retirada",
                PermissaoUtil.getUsuarioLogado().getNome(),
                configuracaoRetirada.getMatriz().getId()
        );

        return new MensagemDTO("Configurações de retirada atualizadas com sucesso", HttpStatus.OK);
    }
}
