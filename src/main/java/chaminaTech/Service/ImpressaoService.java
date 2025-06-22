package chaminaTech.Service;

import chaminaTech.DTO.*;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.*;
import chaminaTech.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImpressaoService {
    @Autowired
    private ProcessarImpressaoService processarImpressaoService;
    @Autowired
    private ImpressaoRepository impressaoRepository;
    @Autowired
    private GestaoCaixaRepository gestaoCaixaRepository;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private SangriaRepository sangriaRepository;
    @Autowired
    private SuprimentoRepository suprimentoRepository;
    @Autowired
    private CaixaRepository caixaRepository;
    @Autowired
    private AuditoriaService auditoriaService;
    @Autowired
    private GorjetaRepository gorjetaRepository;

    public MensagemDTO imprimirProdutos(ImpressaoDTO impressaoDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        GestaoCaixa gestaoCaixa = gestaoCaixaRepository.findByVendaId(impressaoDTO.getVenda().getId()).orElse(null);
        int cupomExistente = (gestaoCaixa != null) ? gestaoCaixa.getCupom() : 0;

        if (gestaoCaixa != null) {
            cupomExistente = gestaoCaixa.getCupom();
        }
        Venda venda = dtoToEntity.DTOToVenda(impressaoDTO.getVenda());
        List<ProdutoVenda> produtoVenda = dtoToEntity.DTOToProdutoVendaList(venda, impressaoDTO.getProdutos());
        try {
            processarImpressaoService.processarImpressaoProdutos(venda, produtoVenda, cupomExistente, false);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu produtos da venda",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    venda.getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir os produtos", e);
        }
    }

    public MensagemDTO imprimirConta(ImpressaoDTO impressaoDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        Integer quantedade = impressaoDTO.getQuantedade();
        Venda venda = dtoToEntity.DTOToVenda(impressaoDTO.getVenda());
        try {
            processarImpressaoService.processarImpressaoConta(venda, quantedade);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu conta da venda para o cliente",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    venda.getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir conta", e);
        }
    }

    public MensagemDTO imprimirConferencia(VendaDTO vendaDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        GestaoCaixa gestaoCaixa = gestaoCaixaRepository.findByVendaId(vendaDTO.getId()).orElse(null);
        int cupomExistente = (gestaoCaixa != null) ? gestaoCaixa.getCupom() : 0;

        if (gestaoCaixa != null) {
            cupomExistente = gestaoCaixa.getCupom();
        }
        Venda venda = dtoToEntity.DTOToVenda(vendaDTO);
        try {
            processarImpressaoService.processarImpressaoConferencia(venda, cupomExistente);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu conferência da venda",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    venda.getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir conta", e);
        }
    }

    public MensagemDTO imprimirComprovante(VendaDTO vendaDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        GestaoCaixa gestaoCaixa = gestaoCaixaRepository.findByVendaId(vendaDTO.getId()).orElse(null);
        int cupomExistente = (gestaoCaixa != null) ? gestaoCaixa.getCupom() : 0;

        if (gestaoCaixa != null) {
            cupomExistente = gestaoCaixa.getCupom();
        }
        Venda venda = dtoToEntity.DTOToVenda(vendaDTO);
        try {
            processarImpressaoService.processarImpressaoComprovanteRecebimento(venda, cupomExistente);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu comprovante de recebimento da venda",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    venda.getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir conta", e);
        }
    }

    public MensagemDTO imprimirSangria(SangriaDTO sangriaDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        Sangria sangria = sangriaRepository.findById(sangriaDTO.getId()).orElse(null);
        if (sangria != null) {
            sangria.setNomeImpressora(sangriaDTO.getNomeImpressora());
        }
        try {
            processarImpressaoService.processarConteudoSangria(sangria);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu comprovante de sangria",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    sangria.getCaixa().getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir sangria", e);
        }
    }

    public MensagemDTO imprimirSuprimento(SuprimentoDTO suprimentoDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        Suprimento suprimento = suprimentoRepository.findById(suprimentoDTO.getId()).orElse(null);
        if (suprimento != null) {
            suprimento.setNomeImpressora(suprimentoDTO.getNomeImpressora());
        }
        try {
            processarImpressaoService.processarConteudoSuprimento(suprimento);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu comprovante de suprimento",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    suprimento.getCaixa().getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir suprimento", e);
        }
    }

    public MensagemDTO imprimirGorjeta(GorjetaDTO gorjetaDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        Gorjeta gorjeta = gorjetaRepository.findById(gorjetaDTO.getId()).orElse(null);
        if (gorjeta != null) {
            gorjeta.setNomeImpressora(gorjetaDTO.getNomeImpressora());
        }
        try {
            processarImpressaoService.processarConteudoGorjeta(gorjeta);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu comprovante de gorjeta",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    gorjeta.getCaixa().getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir gorjeta", e);
        }
    }

    public MensagemDTO imprimirAbertura(CaixaDTO caixaDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        Caixa caixa = caixaRepository.findById(caixaDTO.getId()).orElse(null);
        if (caixa != null) {
            caixa.setNomeImpressora(caixaDTO.getNomeImpressora());
        }
        try {
            processarImpressaoService.processarConteudoCaixaAbertura(caixa);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu abertura do caixa",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    caixa.getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir abertura", e);
        }
    }

    public MensagemDTO imprimirConferenciaCaixa(CaixaDTO caixaDTO) {
        PermissaoUtil.validarOuLancar("imprimir");
        Caixa caixa = caixaRepository.findById(caixaDTO.getId()).orElse(null);
        if (caixa != null) {
            caixa.setNomeImpressora(caixaDTO.getNomeImpressora());
        }
        try {
            processarImpressaoService.processarConteudoCaixaConferencia(caixa);
            auditoriaService.salvarAuditoria(
                    "IMPRIMIR",
                    "IMPRESSAO",
                    "Imprimiu conferência do caixa",
                    PermissaoUtil.getUsuarioLogado().getNome(),
                    caixa.getMatriz().getId()
            );
            return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar imprimir conferencia de caixa", e);
        }
    }

//    public MensagemDTO imprimirNotaFiscal(VendaDTO vendaDTO) {
//        PermissaoUtil.validarOuLancar("imprimir");
//        GestaoCaixa gestaoCaixa = gestaoCaixaRepository.findByVendaId(vendaDTO.getId()).orElse(null);
//        int cupomExistente = (gestaoCaixa != null) ? gestaoCaixa.getCupom() : 0;
//
//        if (gestaoCaixa != null) {
//            cupomExistente = gestaoCaixa.getCupom();
//        }
//        Venda venda = dtoToEntity.DTOToVenda(vendaDTO);
//        try {
//            processarImpressaoService.processarImpressaoComprovanteRecebimento(venda, cupomExistente);
//            auditoriaService.salvarAuditoria(
//                    "IMPRIMIR",
//                    "IMPRESSAO",
//                    "Imprimiu nota fiscal para o cliente",
//                    PermissaoUtil.getUsuarioLogado().getNome(),
//                    venda.getMatriz().getId()
//            );
//        } catch (Exception e) {
//            throw new RuntimeException("Erro ao processar imprimir conta", e);
//        }
//
//        return new MensagemDTO("Impressão enviada com sucesso!", HttpStatus.OK);
//    }

    public List<Impressao> listarPendentes(Long matrizId) {
        return impressaoRepository.findByMatrizIdAndStatus(matrizId, true);
    }

    public void deletar(Long id) {
        impressaoRepository.deleteById(id);
    }
}

