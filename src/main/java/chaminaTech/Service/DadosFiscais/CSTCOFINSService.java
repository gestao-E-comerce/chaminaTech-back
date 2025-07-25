package chaminaTech.Service.DadosFiscais;

import chaminaTech.DTO.DadosFiscais.CSTCOFINSDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.DadosFiscais.CSTCOFINS;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.DadosFiscais.CSTCOFINSRepository;
import chaminaTech.Service.AuditoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSTCOFINSService {
    @Autowired
    private CSTCOFINSRepository cstcofinsRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<CSTCOFINSDTO> listarCSTCOFINSS() {
        return cstcofinsRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo")).stream()
                .map(entityToDTO::cstcofinsToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarCSTCOFINS(CSTCOFINSDTO cstcofinsDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDadosFiscal");
        CSTCOFINS cstcofins = dtoToEntity.DTOToCSTCOFINS(cstcofinsDTO);

        if (cstcofinsRepository.existsByCodigo(cstcofins.getCodigo())) {
            throw new IllegalStateException("Já existe uma cstcofins com esse código!");
        }

        cstcofinsRepository.save(cstcofins);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CSTCOFINS",
                "Cadastrou a cstcofins: " + cstcofins.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTCOFINS cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarCSTCOFINS(Long id, CSTCOFINSDTO cstcofinsDTO) {
        PermissaoUtil.validarOuLancar("editarDadosFiscal");
        cstcofinsDTO.setId(id);
        CSTCOFINS cstcofins = dtoToEntity.DTOToCSTCOFINS(cstcofinsDTO);

        if (cstcofinsRepository.existsByCodigoAndIdNot(cstcofins.getCodigo(), cstcofins.getId())) {
            throw new IllegalStateException("Já existe uma cstcofins com esse código!");
        }

        cstcofinsRepository.save(cstcofins);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CSTCOFINS",
                "Editou a cstcofins: " + cstcofins.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTCOFINS atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCSTCOFINS(Long id) {
        PermissaoUtil.validarOuLancar("deletarDadosFiscal");
        CSTCOFINS cstcofinsBanco = cstcofinsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CSTCOFINS com ID " + id + " não existe!"));
        cstcofinsRepository.deleteById(id);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CSTCOFINS",
                "Deletou a cstcofins: " + cstcofinsBanco.getCodigo(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("CSTCOFINS deletada com sucesso!", HttpStatus.CREATED);
    }
}