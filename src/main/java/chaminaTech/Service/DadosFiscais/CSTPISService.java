package chaminaTech.Service.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTPISDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.DadosFiscais.CSTPIS;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.DadosFiscais.CSTPISRepository;
import chaminaTech.Service.AuditoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSTPISService {
    @Autowired
    private CSTPISRepository cstpisRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<CSTPISDTO> listarCSTPISS() {
        return cstpisRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo")).stream()
                .map(entityToDTO::cstpisToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarCSTPIS(CSTPISDTO cstpisDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDadosFiscal");
        CSTPIS cstpis = dtoToEntity.DTOToCSTPIS(cstpisDTO);

        if (cstpisRepository.existsByCodigo(cstpis.getCodigo())) {
            throw new IllegalStateException("Já existe uma cstpis com esse código!");
        }

        cstpisRepository.save(cstpis);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CSTPIS",
                "Cadastrou a cstpis: " + cstpis.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTPIS cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarCSTPIS(Long id, CSTPISDTO cstpisDTO) {
        PermissaoUtil.validarOuLancar("editarDadosFiscal");
        cstpisDTO.setId(id);
        CSTPIS cstpis = dtoToEntity.DTOToCSTPIS(cstpisDTO);

        if (cstpisRepository.existsByCodigoAndIdNot(cstpis.getCodigo(), cstpis.getId())) {
            throw new IllegalStateException("Já existe uma cstpis com esse código!");
        }

        cstpisRepository.save(cstpis);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CSTPIS",
                "Editou a cstpis: " + cstpis.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTPIS atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCSTPIS(Long id) {
        PermissaoUtil.validarOuLancar("deletarDadosFiscal");
        CSTPIS cstpisBanco = cstpisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CSTPIS com ID " + id + " não existe!"));
        cstpisRepository.deleteById(id);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CSTPIS",
                "Deletou a cstpis: " + cstpisBanco.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTPIS deletada com sucesso!", HttpStatus.CREATED);
    }
}