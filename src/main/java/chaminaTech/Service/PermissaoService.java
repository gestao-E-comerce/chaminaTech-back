package chaminaTech.Service;

import chaminaTech.DTO.PermissaoDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Permissao;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.PermissaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissaoService {

    @Autowired
    private PermissaoRepository permissaoRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<PermissaoDTO> listarPermissaosPorUsuarioId(Long usuarioId) {
        PermissaoUtil.validarOuLancar("permissao");
        try {
            Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
            return permissaoRepository.findByUsuarioId(usuarioId).stream()
                    .filter(permissao -> !permissao.getId().equals(usuarioLogado.getPermissao().getId()))
                    .map(entityToDTO::permissaoToDTO)
                    .collect(Collectors.toList());
        } finally {
            PermissaoUtil.limparUsuarioLogado();
        }
    }

    // Cadastrar novo permissao
    public MensagemDTO cadastrarPermissao(PermissaoDTO permissaoDTO) {
        PermissaoUtil.validarOuLancar("cadastrarPermissao");
        Permissao permissao = dtoToEntity.DTOToPermissao(permissaoDTO);

        Long usuarioId = permissao.getUsuario().getId();

        Optional<Permissao> existente = permissaoRepository.findByNomeAndUsuarioId(permissao.getNome(), usuarioId);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma permissão com esse nome!");
        }

        permissaoRepository.save(permissao);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "PERMISSAO",
                "Cadastrou a permissão: " + permissao.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                permissao.getUsuario().getId()
        );

        return new MensagemDTO("Permissao cadastrado com sucesso!", HttpStatus.CREATED);
    }

    // Editar permissao existente
    public MensagemDTO editarPermissao(Long id, PermissaoDTO permissaoDTO) {
        PermissaoUtil.validarOuLancar("editarPermissao");
        permissaoDTO.setId(id);
        Permissao permissao = dtoToEntity.DTOToPermissao(permissaoDTO);

        Long usuarioId = permissao.getUsuario().getId();

        Optional<Permissao> existente = permissaoRepository.findByNomeAndUsuarioIdAndIdNot(
                permissao.getNome(), usuarioId, id
        );
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma permissão com esse nome!");
        }

        permissaoRepository.save(permissao);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "PERMISSAO",
                "Editou a permissão: " + permissao.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                permissao.getUsuario().getId()
        );
        return new MensagemDTO("Permissao atualizado com sucesso!", HttpStatus.OK);
    }

    // Deletar permissao
    public MensagemDTO deletarPermissao(Long id) {
        PermissaoUtil.validarOuLancar("deletarPermissao");
        Permissao permissaoBanco = permissaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permissao com ID " + id + " não existe!"));

        if (permissaoRepository.existeUsuarioComPermissao(id)) {
            throw new IllegalStateException("Não é possível deletar permissão está vinculada a um usuário!");
        }

        permissaoRepository.deleteById(id);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "PERMISSAO",
                "Deletou a permissão: " + permissaoBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                permissaoBanco.getUsuario().getId()
        );
        return new MensagemDTO("Permissao deletado com sucesso!", HttpStatus.OK);
    }
}