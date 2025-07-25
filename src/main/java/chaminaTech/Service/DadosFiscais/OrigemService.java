package chaminaTech.Service.DadosFiscais;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.DadosFiscais.OrigemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.DadosFiscais.Origem;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.DadosFiscais.OrigemRepository;
import chaminaTech.Service.AuditoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrigemService {
    @Autowired
    private OrigemRepository origemRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<OrigemDTO> listarOrigens() {
        return origemRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo")).stream()
                .map(entityToDTO::origemToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarOrigem(OrigemDTO origemDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDadosFiscal");
        Origem origem = dtoToEntity.DTOToOrigem(origemDTO);

        if (origemRepository.existsByCodigo(origem.getCodigo())) {
            throw new IllegalStateException("Já existe uma origem com esse código!");
        }

        origemRepository.save(origem);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "ORIGEM",
                "Cadastrou a origem: " + origem.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Origem cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarOrigem(Long id, OrigemDTO origemDTO) {
        PermissaoUtil.validarOuLancar("editarDadosFiscal");
        origemDTO.setId(id);
        Origem origem = dtoToEntity.DTOToOrigem(origemDTO);

        if (origemRepository.existsByCodigoAndIdNot(origem.getCodigo(), origem.getId())) {
            throw new IllegalStateException("Já existe uma origem com esse código!");
        }

        origemRepository.save(origem);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "ORIGEM",
                "Editou a origem: " + origem.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Origem atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarOrigem(Long id) {
        PermissaoUtil.validarOuLancar("deletarDadosFiscal");
        Origem origemBanco = origemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Origem com ID " + id + " não existe!"));
        origemRepository.deleteById(id);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "ORIGEM",
                "Deletou a origem: " + origemBanco.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Origem deletada com sucesso!", HttpStatus.CREATED);
    }
}