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

import java.math.BigDecimal;
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
        Caixa caixa = caixaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Caixa não encontrado!"));
        return entityToDTO.caixaToDTO(caixa);
    }

    public Optional<CaixaDTO> buscarCaixaAtivaPorFuncionario(Long funcionarioId) {
        return caixaRepository.findCaixaAtivaByFuncionarioId(funcionarioId).map(entityToDTO::caixaToDTO);
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

        BigDecimal totalBruto = caixaRepository.findTotalVendasBrutoByCaixaId(caixa.getId());
        BigDecimal valorAbertura = caixa.getValorAbertura();
        BigDecimal totalSuprimentos = caixaRepository.findTotalSuprimentosByCaixaId(caixa.getId());
        BigDecimal totalSangrias = caixaRepository.findTotalSangriasByCaixaId(caixa.getId());
        BigDecimal totalDescontos = caixaRepository.findTotalDescontosByCaixaId(caixa.getId());
        BigDecimal totalServico = caixaRepository.findTotalServiciosByCaixaId(caixa.getId());
        BigDecimal totalGorjetas = caixaRepository.findTotalGorjetasByCaixaId(caixa.getId());

        // Garantir que os valores não sejam null
        totalBruto = totalBruto != null ? totalBruto : BigDecimal.ZERO;
        valorAbertura = valorAbertura != null ? valorAbertura : BigDecimal.ZERO;
        totalSuprimentos = totalSuprimentos != null ? totalSuprimentos : BigDecimal.ZERO;
        totalSangrias = totalSangrias != null ? totalSangrias : BigDecimal.ZERO;
        totalDescontos = totalDescontos != null ? totalDescontos : BigDecimal.ZERO;
        totalServico = totalServico != null ? totalServico : BigDecimal.ZERO;
        totalGorjetas = totalGorjetas != null ? totalGorjetas : BigDecimal.ZERO;

        BigDecimal saldoFinal = valorAbertura
                .add(totalSuprimentos)
                .add(totalBruto)
                .add(totalServico)
                .add(totalGorjetas)
                .subtract(totalSangrias)
                .subtract(totalDescontos)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        caixa.setSaldo(saldoFinal);

        caixaRepository.save(caixa);

        if (caixa.getNomeImpressora() != null) {
            processarImpressaoService.processarConteudoCaixaConferencia(caixa);
        }

        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CAIXA",
                "Fechou o caixa - Abertura: R$ " + valorAbertura.setScale(2, java.math.RoundingMode.HALF_UP) +
                        ", Suprimentos: R$ " + totalSuprimentos.setScale(2, java.math.RoundingMode.HALF_UP) +
                        ", Bruto: R$ " + totalBruto.setScale(2, java.math.RoundingMode.HALF_UP) +
                        ", Serviço: R$ " + totalServico.setScale(2, java.math.RoundingMode.HALF_UP) +
                        ", Gorjetas: R$ " + totalGorjetas.setScale(2, java.math.RoundingMode.HALF_UP) +
                        ", Sangrias: R$ " + totalSangrias.setScale(2, java.math.RoundingMode.HALF_UP) +
                        ", Descontos: R$ " + totalDescontos.setScale(2, java.math.RoundingMode.HALF_UP) +
                        ", Saldo Final: R$ " + saldoFinal,
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
        auditoriaService.salvarAuditoria("CADASTRAR", "CAIXA", "Abriu um novo caixa com valor de abertura R$ " + String.format("%.2f", caixa.getValorAbertura()), PermissaoUtil.getUsuarioLogado().getNome(), caixa.getMatriz().getId());
        return entityToDTO.caixaToDTO(caixa);
    }

    public MensagemDTO editarCaixa(Long id, CaixaDTO caixaDTO) {
        PermissaoUtil.validarOuLancar("editarCaixa");
        Caixa caixa = caixaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Caixa não encontrado"));

        caixa.setValorAbertura(caixaDTO.getValorAbertura());

        caixaRepository.save(caixa);
        auditoriaService.salvarAuditoria("EDITAR", "CAIXA", "Editou o valor de abertura do caixa para R$ " + String.format("%.2f", caixa.getValorAbertura()), PermissaoUtil.getUsuarioLogado().getNome(), caixa.getMatriz().getId());
        return new MensagemDTO("Caixa atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCaixa(Long id) {
        PermissaoUtil.validarOuLancar("deletarCaixa");
        Caixa caixaBanco = caixaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Caixa com ID " + id + " não existe!"));
        desativarCaixa(caixaBanco);
        auditoriaService.salvarAuditoria("DELETAR", "CAIXA", "Deletou um caixa com valor de abertura R$ " + String.format("%.2f", caixaBanco.getValorAbertura()), PermissaoUtil.getUsuarioLogado().getNome(), caixaBanco.getMatriz().getId());
        return new MensagemDTO("Caixa deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarCaixa(Caixa caixa) {
        caixa.setDeletado(true);
        caixa.setAtivo(false);
        caixaRepository.save(caixa);
    }

    public BigDecimal getTotalPixByCaixaId(Long caixaId) {
        return caixaRepository.findTotalPixByCaixaId(caixaId);
    }

    public BigDecimal getTotalDinheiroByCaixaId(Long caixaId) {
        return caixaRepository.findTotalDinheiroByCaixaId(caixaId);
    }

    public BigDecimal getTotalDebitoByCaixaId(Long caixaId) {
        return caixaRepository.findTotalDebitoByCaixaId(caixaId);
    }

    public BigDecimal getTotalCreditoByCaixaId(Long caixaId) {
        return caixaRepository.findTotalCreditoByCaixaId(caixaId);
    }

    public BigDecimal getTotalDescontosByCaixaId(Long caixaId) {
        return caixaRepository.findTotalDescontosByCaixaId(caixaId);
    }

    public BigDecimal getTotalSangriasByCaixaId(Long caixaId) {
        return caixaRepository.findTotalSangriasByCaixaId(caixaId);
    }

    public BigDecimal getTotalSuprimentosByCaixaId(Long caixaId) {
        return caixaRepository.findTotalSuprimentosByCaixaId(caixaId);
    }

    public BigDecimal getTotalGorjetasByCaixaId(Long caixaId) {
        return caixaRepository.findTotalGorjetasByCaixaId(caixaId);
    }
    public BigDecimal getTotalServiciosByCaixaId(Long caixaId) {
        return caixaRepository.findTotalServiciosByCaixaId(caixaId);
    }
}