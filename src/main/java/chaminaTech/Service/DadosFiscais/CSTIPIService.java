package chaminaTech.Service.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTIPIDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.DadosFiscais.CSTIPI;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.DadosFiscais.CSTIPIRepository;
import chaminaTech.Service.AuditoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSTIPIService {
    @Autowired
    private CSTIPIRepository cstipiRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<CSTIPIDTO> listarCSTIPIS() {
        return cstipiRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo")).stream()
                .map(entityToDTO::cstipiToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarCSTIPI(CSTIPIDTO cstipiDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDadosFiscal");
        CSTIPI cstipi = dtoToEntity.DTOToCSTIPI(cstipiDTO);

        if (cstipiRepository.existsByCodigo(cstipi.getCodigo())) {
            throw new IllegalStateException("Já existe uma cstipi com esse código!");
        }

        cstipiRepository.save(cstipi);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CSTIPI",
                "Cadastrou a cstipi: " + cstipi.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTIPI cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarCSTIPI(Long id, CSTIPIDTO cstipiDTO) {
        PermissaoUtil.validarOuLancar("editarDadosFiscal");
        cstipiDTO.setId(id);
        CSTIPI cstipi = dtoToEntity.DTOToCSTIPI(cstipiDTO);

        if (cstipiRepository.existsByCodigoAndIdNot(cstipi.getCodigo(), cstipi.getId())) {
            throw new IllegalStateException("Já existe uma cstipi com esse código!");
        }

        cstipiRepository.save(cstipi);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CSTIPI",
                "Editou a cstipi: " + cstipi.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTIPI atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCSTIPI(Long id) {
        PermissaoUtil.validarOuLancar("deletarDadosFiscal");
        CSTIPI cstipiBanco = cstipiRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CSTIPI com ID " + id + " não existe!"));
        cstipiRepository.deleteById(id);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CSTIPI",
                "Deletou a cstipi: " + cstipiBanco.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTIPI deletada com sucesso!", HttpStatus.CREATED);
    }
}