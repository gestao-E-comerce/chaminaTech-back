package chaminaTech.Service.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSOSNDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.DadosFiscais.CSOSN;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.DadosFiscais.CSOSNRepository;
import chaminaTech.Service.AuditoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSOSNService {
    @Autowired
    private CSOSNRepository csosnRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<CSOSNDTO> listarCsosn() {
        return csosnRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo")).stream()
                .map(entityToDTO::csosnToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarCSOSN(CSOSNDTO csosnDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDadosFiscal");
        CSOSN csosn = dtoToEntity.DTOToCSOSN(csosnDTO);

        if (csosnRepository.existsByCodigo(csosn.getCodigo())) {
            throw new IllegalStateException("Já existe uma csosn com esse código!");
        }

        csosnRepository.save(csosn);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CSOSN",
                "Cadastrou a csosn: " + csosn.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSOSN cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarCSOSN(Long id, CSOSNDTO csosnDTO) {
        PermissaoUtil.validarOuLancar("editarDadosFiscal");
        csosnDTO.setId(id);
        CSOSN csosn = dtoToEntity.DTOToCSOSN(csosnDTO);

        if (csosnRepository.existsByCodigoAndIdNot(csosn.getCodigo(), csosn.getId())) {
            throw new IllegalStateException("Já existe uma csosn com esse código!");
        }

        csosnRepository.save(csosn);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CSOSN",
                "Editou a csosn: " + csosn.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSOSN atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCSOSN(Long id) {
        PermissaoUtil.validarOuLancar("deletarDadosFiscal");
        CSOSN csosnBanco = csosnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CSOSN com ID " + id + " não existe!"));
        csosnRepository.deleteById(id);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CSOSN",
                "Deletou a csosn: " + csosnBanco.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSOSN deletada com sucesso!", HttpStatus.CREATED);
    }
}