package chaminaTech.Service.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.DadosFiscais.CST;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.DadosFiscais.CSTRepository;
import chaminaTech.Service.AuditoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSTService {
    @Autowired
    private CSTRepository cstRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<CSTDTO> listarCSTS() {
        return cstRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo")).stream()
                .map(entityToDTO::cstToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarCST(CSTDTO cstDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDadosFiscal");
        CST cst = dtoToEntity.DTOToCST(cstDTO);

        if (cstRepository.existsByCodigo(cst.getCodigo())) {
            throw new IllegalStateException("Já existe uma cst com esse código!");
        }

        cstRepository.save(cst);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CST",
                "Cadastrou a cst: " + cst.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CST cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarCST(Long id, CSTDTO cstDTO) {
        PermissaoUtil.validarOuLancar("editarDadosFiscal");
        cstDTO.setId(id);
        CST cst = dtoToEntity.DTOToCST(cstDTO);

        if (cstRepository.existsByCodigoAndIdNot(cst.getCodigo(), cst.getId())) {
            throw new IllegalStateException("Já existe uma cst com esse código!");
        }

        cstRepository.save(cst);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CST",
                "Editou a cst: " + cst.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CST atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCST(Long id) {
        PermissaoUtil.validarOuLancar("deletarDadosFiscal");
        CST cstBanco = cstRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CST com ID " + id + " não existe!"));
        cstRepository.deleteById(id);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CST",
                "Deletou a cst: " + cstBanco.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CST deletada com sucesso!", HttpStatus.CREATED);
    }
}