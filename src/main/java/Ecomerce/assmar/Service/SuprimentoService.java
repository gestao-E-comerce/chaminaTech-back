package Ecomerce.assmar.Service;

import Ecomerce.assmar.DTO.*;
import Ecomerce.assmar.DTOService.*;
import Ecomerce.assmar.Entity.*;
import Ecomerce.assmar.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class SuprimentoService {
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private CaixaRepository caixaRepository;
    @Autowired
    private SuprimentoRepository suprimentoRepository;
    @Autowired
    private ProcessarImpressaoService processarImpressaoService;
    @Autowired
    private AuditoriaService auditoriaService;

    public MensagemDTO cadastrarSuprimento(SuprimentoDTO suprimentoDTO) {
        PermissaoUtil.validarOuLancar("cadastrarSuprimento");
        Suprimento suprimento = dtoToEntity.DTOToSuprimento(suprimentoDTO);

        suprimento.setDataSuprimento(new Timestamp(System.currentTimeMillis()));

        suprimentoRepository.save(suprimento);
        if (suprimento.getNomeImpressora() != null) {
            processarImpressaoService.processarConteudoSuprimento(suprimento);
        }
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "SUPRIMENTO",
                "Cadastrou suprimento de R$ " + suprimento.getValor()
                        + " motivo: " + suprimento.getMotivo(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                suprimento.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Suprimento adicionado com sucesso!", HttpStatus.OK);
    }

    public MensagemDTO editarSuprimento(Long id, SuprimentoDTO suprimentoDTO) {
        PermissaoUtil.validarOuLancar("editarSuprimento");
        Suprimento suprimentoExistente = suprimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suprimento com ID " + suprimentoDTO.getId() + " não encontrada!"));

        suprimentoDTO.setId(id);
        Suprimento suprimento = dtoToEntity.DTOToSuprimento(suprimentoDTO);

        suprimento.setCaixa(suprimentoExistente.getCaixa());
        suprimentoRepository.save(suprimento);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "SUPRIMENTO",
                "Editou suprimento para R$ " + suprimento.getValor()
                        + " motivo: " + suprimento.getMotivo(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                suprimento.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Suprimento atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarSuprimento(Long id) {
        PermissaoUtil.validarOuLancar("deletarSuprimento");
        Suprimento suprimentoBanco = suprimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suprimento com ID " + id + " não existe!"));
        desativarSuprimento(suprimentoBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "SUPRIMENTO",
                "Deletou suprimento de R$ " + suprimentoBanco.getValor()
                        + " motivo: " + suprimentoBanco.getMotivo(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                suprimentoBanco.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Suprimento deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarSuprimento(Suprimento suprimento) {
        suprimento.setAtivo(false);
        suprimentoRepository.save(suprimento);
    }
}