package chaminaTech.Service.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.ModalidadeBaseCalculoIcmsDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.DadosFiscais.ModalidadeBaseCalculoIcms;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.DadosFiscais.ModalidadeBaseCalculoIcmsRepository;
import chaminaTech.Service.AuditoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModalidadeBaseCalculoIcmsService {
    @Autowired
    private ModalidadeBaseCalculoIcmsRepository modalidadeBaseCalculoIcmsRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<ModalidadeBaseCalculoIcmsDTO> listarModalidadeBaseCalculoIcmss() {
        return modalidadeBaseCalculoIcmsRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo")).stream()
                .map(entityToDTO::modalidadeBaseCalculoIcmsToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarModalidadeBaseCalculoIcms(ModalidadeBaseCalculoIcmsDTO modalidadeBaseCalculoIcmsDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDadosFiscal");
        ModalidadeBaseCalculoIcms modalidadeBaseCalculoIcms = dtoToEntity.DTOToModalidadeBaseCalculoIcms(modalidadeBaseCalculoIcmsDTO);

        if (modalidadeBaseCalculoIcmsRepository.existsByCodigo(modalidadeBaseCalculoIcms.getCodigo())) {
            throw new IllegalStateException("Já existe uma Modalidade Base Calculo Icms com esse código!");
        }

        modalidadeBaseCalculoIcmsRepository.save(modalidadeBaseCalculoIcms);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "MODBC",
                "Cadastrou a Modalidade Base Calculo Icms: " + modalidadeBaseCalculoIcms.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Modalidade Base Calculo Icms cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarModalidadeBaseCalculoIcms(Long id, ModalidadeBaseCalculoIcmsDTO modalidadeBaseCalculoIcmsDTO) {
        PermissaoUtil.validarOuLancar("editarDadosFiscal");
        modalidadeBaseCalculoIcmsDTO.setId(id);
        ModalidadeBaseCalculoIcms modalidadeBaseCalculoIcms = dtoToEntity.DTOToModalidadeBaseCalculoIcms(modalidadeBaseCalculoIcmsDTO);

        if (modalidadeBaseCalculoIcmsRepository.existsByCodigoAndIdNot(modalidadeBaseCalculoIcms.getCodigo(), modalidadeBaseCalculoIcms.getId())) {
            throw new IllegalStateException("Já existe uma Modalidade Base Calculo Icms com esse código!");
        }

        modalidadeBaseCalculoIcmsRepository.save(modalidadeBaseCalculoIcms);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "MODBC",
                "Editou a Modalidade Base Calculo Icms: " + modalidadeBaseCalculoIcms.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Modalidade Base Calculo Icms atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarModalidadeBaseCalculoIcms(Long id) {
        PermissaoUtil.validarOuLancar("deletarDadosFiscal");
        ModalidadeBaseCalculoIcms modalidadeBaseCalculoIcmsBanco = modalidadeBaseCalculoIcmsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modalidade Base Calculo Icms com ID " + id + " não existe!"));
        modalidadeBaseCalculoIcmsRepository.deleteById(id);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "MODBC",
                "Deletou a Modalidade Base Calculo Icms: " + modalidadeBaseCalculoIcmsBanco.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Modalidade Base Calculo Icms deletada com sucesso!", HttpStatus.CREATED);
    }
}