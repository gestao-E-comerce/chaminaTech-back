package chaminaTech.Service;

import chaminaTech.DTO.CaixaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Caixa;
import chaminaTech.Entity.Venda;
import chaminaTech.Repository.CaixaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CaixaService {
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private CaixaRepository caixaRepository;
    @Autowired
    private ProcessarImpressaoService processarImpressaoService;
    @Autowired
    private AuditoriaService auditoriaService;

    public boolean verificaVendasAtivasNaMatriz(Long matrizId) {
        List<Venda> vendasAtivas = caixaRepository.findVendasAtivasByMatrizId(matrizId);
        return !vendasAtivas.isEmpty();
    }

    public CaixaDTO findCaixaById(Long id) {
        Caixa caixa = caixaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caixa não encontrado!"));
        return entityToDTO.caixaToDTO(caixa);
    }

    public Optional<CaixaDTO> buscarCaixaAtivaPorFuncionario(Long funcionarioId) {
        return caixaRepository.findCaixaAtivaByFuncionarioId(funcionarioId)
                .map(entityToDTO::caixaToDTO);
    }

    public List<CaixaDTO> buscarCaixasPorFuncionarioNomeAtivoEPorMatrizId(String nome, Long matrizId, String tipo) {
        PermissaoUtil.validarOuLancar("historicoCaixa");
        List<Caixa> caixas = caixaRepository.findCaixasByNomeAndMatrizId(nome, matrizId, tipo);
        return caixas.stream().map(entityToDTO::caixaToDTO).collect(Collectors.toList());
    }

    public MensagemDTO fecharCaixa(Long id, CaixaDTO caixaDTO) {
        PermissaoUtil.validarOuLancar("editarCaixa");
        Caixa caixa = caixaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caixa não encontrado"));

        caixa.setAtivo(false);
        caixa.setDataFechamento(new Timestamp(System.currentTimeMillis()));
        caixa.setSaldoDinheiro(caixaDTO.getSaldoDinheiro());
        caixa.setSaldoCredito(caixaDTO.getSaldoCredito());
        caixa.setSaldoDebito(caixaDTO.getSaldoDebito());
        caixa.setSaldoPix(caixaDTO.getSaldoPix());

        Double totalVendas = caixaRepository.findTotalVendasByCaixaId(caixa.getId());
        Double valorAbertura = caixa.getValorAbertura();
        caixa.setSaldo((totalVendas != null ? totalVendas : 0.0) + (valorAbertura != null ? valorAbertura : 0.0));

        caixaRepository.save(caixa);
        if (caixa.getNomeImpressora() != null) {
            processarImpressaoService.processarConteudoCaixaConferencia(caixa);
        }
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CAIXA",
                "Fechou o caixa com valor de abertura R$ " + String.format("%.2f", caixa.getValorAbertura()),
                PermissaoUtil.getUsuarioLogado().getNome(),
                caixa.getMatriz().getId()
        );
        return new MensagemDTO("Caixa fechada com sucesso!", HttpStatus.CREATED);
    }

    public CaixaDTO abrirCaixa(CaixaDTO caixaDTO) {
        PermissaoUtil.validarOuLancar("editarCaixa");
        Caixa caixa = dtoToEntity.DTOToCaixa(caixaDTO);

        caixa.setDataAbertura(new Timestamp(System.currentTimeMillis()));

        caixa = caixaRepository.save(caixa);

        if (caixa.getNomeImpressora() != null) {
            processarImpressaoService.processarConteudoCaixaAbertura(caixa);
        }
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CAIXA",
                "Abriu um novo caixa com valor de abertura R$ " + String.format("%.2f", caixa.getValorAbertura()),
                PermissaoUtil.getUsuarioLogado().getNome(),
                caixa.getMatriz().getId()
        );
        return entityToDTO.caixaToDTO(caixa);
    }

    public MensagemDTO editarCaixa(Long id, CaixaDTO caixaDTO) {
        PermissaoUtil.validarOuLancar("editarCaixa");
        Caixa caixa = caixaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caixa não encontrado"));

        caixa.setValorAbertura(caixaDTO.getValorAbertura());

        caixaRepository.save(caixa);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CAIXA",
                "Editou o valor de abertura do caixa para R$ " + String.format("%.2f", caixa.getValorAbertura()),
                PermissaoUtil.getUsuarioLogado().getNome(),
                caixa.getMatriz().getId()
        );
        return new MensagemDTO("Caixa atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCaixa(Long id) {
        PermissaoUtil.validarOuLancar("deletarCaixa");
        Caixa caixaBanco = caixaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caixa com ID " + id + " não existe!"));
        desativarCaixa(caixaBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CAIXA",
                "Deletou um caixa com valor de abertura R$ " + String.format("%.2f", caixaBanco.getValorAbertura()),
                PermissaoUtil.getUsuarioLogado().getNome(),
                caixaBanco.getMatriz().getId()
        );
        return new MensagemDTO("Caixa deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarCaixa(Caixa caixa) {
        caixa.setDeletado(true);
        caixa.setAtivo(false);
        caixaRepository.save(caixa);
    }

    public Double getTotalPixByCaixaId(Long caixaId) {
        return caixaRepository.findTotalPixByCaixaId(caixaId);
    }

    public Double getTotalDinheiroByCaixaId(Long caixaId) {
        return caixaRepository.findTotalDinheiroByCaixaId(caixaId);
    }

    public Double getTotalDebitoByCaixaId(Long caixaId) {
        return caixaRepository.findTotalDebitoByCaixaId(caixaId);
    }

    public Double getTotalCreditoByCaixaId(Long caixaId) {
        return caixaRepository.findTotalCreditoByCaixaId(caixaId);
    }

    public Double getTotalSangriasByCaixaId(Long caixaId) {
        return caixaRepository.findTotalSangriasByCaixaId(caixaId);
    }

    public Double getTotalSuprimentosByCaixaId(Long caixaId) {
        return caixaRepository.findTotalSuprimentosByCaixaId(caixaId);
    }
}