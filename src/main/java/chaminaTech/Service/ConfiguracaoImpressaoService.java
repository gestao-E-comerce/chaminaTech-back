package chaminaTech.Service;

import chaminaTech.DTO.ConfiguracaoDTO.ConfiguracaoImpressaoDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Configuracao.ConfiguracaoImpressao;
import chaminaTech.Entity.Impressora;
import chaminaTech.Entity.Produto;
import chaminaTech.Repository.ConfiguracaoImpressaoRepository;
import chaminaTech.Repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfiguracaoImpressaoService {
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private ConfiguracaoImpressaoRepository configuracaoImpressaoRepository;
    @Autowired
    private AuditoriaService auditoriaService;
    @Autowired
    private ProdutoRepository produtoRepository;

    public ConfiguracaoImpressaoDTO buscarConfiguracaoImpressao(Long matrizId) {
        ConfiguracaoImpressao configuracaoImpressao = configuracaoImpressaoRepository.findByMatrizId(matrizId);
        return configuracaoImpressao != null ? entityToDTO.configuracaoImpressaoToDTO(configuracaoImpressao) : null;
    }

    @Transactional
    public MensagemDTO salvarConfiguracaoImpressao(ConfiguracaoImpressaoDTO configuracaoImpressaoDTO) {
        PermissaoUtil.validarOuLancar("editarConfiguracoes");
        ConfiguracaoImpressao configuracaoImpressao = dtoToEntity.DTOToConfiguracaoImpressao(configuracaoImpressaoDTO);

        if (configuracaoImpressao.getImpressoras() != null) {
            for (int i = 0; i < configuracaoImpressao.getImpressoras().size(); i++) {
                configuracaoImpressao.getImpressoras().get(i).setConfiguracaoImpressao(configuracaoImpressao);
            }
        }
        if (configuracaoImpressao.getIdentificador() != null) {
            for (int i = 0; i < configuracaoImpressao.getIdentificador().size(); i++) {
                configuracaoImpressao.getIdentificador().get(i).setConfiguracaoImpressao(configuracaoImpressao);
            }
        }
        ConfiguracaoImpressao configuracaoImpressaoExistente = configuracaoImpressaoRepository.findById(configuracaoImpressao.getId())
                .orElse(null);  // Caso não exista, não faz nada

        // Se houver ConfiguracaoImpressao existente, verificar impressoras removidas
        if (configuracaoImpressaoExistente != null) {
            List<Impressora> impressorasAtuais = configuracaoImpressaoExistente.getImpressoras();
            List<Impressora> impressorasNovas = configuracaoImpressao.getImpressoras();

            // Encontra impressoras removidas
            List<Impressora> impressorasRemovidas = new ArrayList<>();
            for (Impressora impressoraAtual : impressorasAtuais) {
                boolean impressoraExiste = false;
                for (Impressora novaImpressora : impressorasNovas) {
                    if (impressoraAtual.getId().equals(novaImpressora.getId())) {
                        impressoraExiste = true;
                        break;
                    }
                }
                if (!impressoraExiste) {
                    impressorasRemovidas.add(impressoraAtual);
                }
            }

            // Verifica se alguma das impressoras removidas está associada a produtos
            for (Impressora impressoraRemovida : impressorasRemovidas) {
                List<Produto> produtosRelacionados = produtoRepository.findProdutosByImpressoraAndMatriz(impressoraRemovida, configuracaoImpressao.getMatriz().getId());

                if (!produtosRelacionados.isEmpty()) {
                    for (Produto produto : produtosRelacionados) {
                        produto.getImpressoras().remove(impressoraRemovida);  // Remove a impressora dos produtos
                        produtoRepository.save(produto);  // Salva o produto atualizado
                    }
                }
            }
        }

        configuracaoImpressaoRepository.save(configuracaoImpressao);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CONFIGURACAO",
                "Editou configurações de impressão",
                PermissaoUtil.getUsuarioLogado().getNome(),
                configuracaoImpressao.getMatriz().getId()
        );

        return new MensagemDTO("Configurações de impressão atualizadas com sucesso", HttpStatus.OK);
    }
}
