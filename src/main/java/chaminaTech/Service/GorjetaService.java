package chaminaTech.Service;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.GorjetaDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Gorjeta;
import chaminaTech.Repository.GorjetaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Service
public class GorjetaService {
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private GorjetaRepository gorjetaRepository;
    @Autowired
    private ProcessarImpressaoService processarImpressaoService;
    @Autowired
    private AuditoriaService auditoriaService;

    public MensagemDTO cadastrarGorjeta(GorjetaDTO gorjetaDTO) {
        PermissaoUtil.validarOuLancar("cadastrarGorjeta");
        Gorjeta gorjeta = dtoToEntity.DTOToGorjeta(gorjetaDTO);

        gorjeta.setDataGorjeta(new Timestamp(System.currentTimeMillis()));

        gorjetaRepository.save(gorjeta);
        if (gorjeta.getNomeImpressora() != null) {
            processarImpressaoService.processarConteudoGorjeta(gorjeta);
        }
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "GORJETA",
                "Cadastrou gorjeta: " + gerarTextoValores(gorjeta),
                PermissaoUtil.getUsuarioLogado().getNome(),
                gorjeta.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Gorjeta adicionado com sucesso!", HttpStatus.OK);
    }

    public MensagemDTO editarGorjeta(Long id, GorjetaDTO gorjetaDTO) {
        PermissaoUtil.validarOuLancar("editarGorjeta");
        Gorjeta gorjetaExistente = gorjetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gorjeta com ID " + gorjetaDTO.getId() + " não encontrada!"));

        gorjetaDTO.setId(id);
        Gorjeta gorjeta = dtoToEntity.DTOToGorjeta(gorjetaDTO);

        gorjeta.setCaixa(gorjetaExistente.getCaixa());
        gorjetaRepository.save(gorjeta);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "GORJETA",
                "Editou gorjeta: " + gerarTextoValores(gorjeta),
                PermissaoUtil.getUsuarioLogado().getNome(),
                gorjeta.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Gorjeta atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarGorjeta(Long id) {
        PermissaoUtil.validarOuLancar("deletarGorjeta");
        Gorjeta gorjetaBanco = gorjetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gorjeta com ID " + id + " não existe!"));
        desativarGorjeta(gorjetaBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "GORJETA",
                "Deletou gorjeta: " + gerarTextoValores(gorjetaBanco),
                PermissaoUtil.getUsuarioLogado().getNome(),
                gorjetaBanco.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Gorjeta deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarGorjeta(Gorjeta gorjeta) {
        gorjeta.setAtivo(false);
        gorjetaRepository.save(gorjeta);
    }

    private String gerarTextoValores(Gorjeta gorjeta) {
        StringBuilder texto = new StringBuilder();
        if (gorjeta.getDinheiro() != null && gorjeta.getDinheiro().compareTo(BigDecimal.ZERO) > 0) {
            texto.append("R$").append(gorjeta.getDinheiro().setScale(2, java.math.RoundingMode.HALF_UP)).append(" em dinheiro; ");
        }
        if (gorjeta.getDebito() != null && gorjeta.getDebito().compareTo(BigDecimal.ZERO) > 0) {
            texto.append("R$").append(gorjeta.getDebito().setScale(2, java.math.RoundingMode.HALF_UP)).append(" em débito; ");
        }
        if (gorjeta.getCredito() != null && gorjeta.getCredito().compareTo(BigDecimal.ZERO) > 0) {
            texto.append("R$").append(gorjeta.getCredito().setScale(2, java.math.RoundingMode.HALF_UP)).append(" em crédito; ");
        }
        if (gorjeta.getPix() != null && gorjeta.getPix().compareTo(BigDecimal.ZERO) > 0) {
            texto.append("R$").append(gorjeta.getPix().setScale(2, java.math.RoundingMode.HALF_UP)).append(" em pix; ");
        }
        return texto.toString().trim();
    }
}