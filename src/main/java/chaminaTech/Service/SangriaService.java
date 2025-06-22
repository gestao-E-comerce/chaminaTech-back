package chaminaTech.Service;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.SangriaDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Caixa;
import chaminaTech.Entity.Sangria;
import chaminaTech.Repository.CaixaRepository;
import chaminaTech.Repository.GorjetaRepository;
import chaminaTech.Repository.SangriaRepository;
import chaminaTech.Repository.SuprimentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Optional;

@Service
public class SangriaService {
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private CaixaRepository caixaRepository;
    @Autowired
    private SangriaRepository sangriaRepository;
    @Autowired
    private SuprimentoRepository suprimentoRepository;
    @Autowired
    private ProcessarImpressaoService processarImpressaoService;
    @Autowired
    private AuditoriaService auditoriaService;
    @Autowired
    private GorjetaRepository gorjetaRepository;

    public MensagemDTO cadastrarSangria(SangriaDTO sangriaDTO) {
        PermissaoUtil.validarOuLancar("cadastrarSangria");
        Sangria sangria = dtoToEntity.DTOToSangria(sangriaDTO);

        Optional<Caixa> caixaAtivoOptional = caixaRepository.findCaixaAtivaByFuncionarioId(sangria.getFuncionario().getId());
        if (caixaAtivoOptional.isEmpty()) {
            return new MensagemDTO("Não há caixa ativo para o funcionário!", HttpStatus.BAD_REQUEST);
        }
        Caixa caixa = caixaAtivoOptional.get();
        Long caixaId = caixa.getId();

        sangria.setCaixa(caixa);
        sangria.setDataSangria(new Timestamp(System.currentTimeMillis()));

        BigDecimal totalDinheiro = Optional.ofNullable(caixaRepository.findTotalDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal valorAbertura = Optional.ofNullable(caixa.getValorAbertura()).orElse(BigDecimal.ZERO);
        BigDecimal totalSangrias = Optional.ofNullable(sangriaRepository.findTotalSangriasByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalSuprimentos = Optional.ofNullable(suprimentoRepository.findTotalSuprimentosByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalGorjetasDinheiro = Optional.ofNullable(gorjetaRepository.findTotalGorjetasDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalServicoDinheiro = Optional.ofNullable(caixaRepository.findTotalServicoDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalDescontosDinheiro = Optional.ofNullable(caixaRepository.findTotalDescontoDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);


        // Saldo disponível considerando apenas DINHEIRO
        BigDecimal saldoDisponivel = totalDinheiro.add(valorAbertura).add(totalSuprimentos).add(totalGorjetasDinheiro).add(totalServicoDinheiro).subtract(totalDescontosDinheiro).subtract(totalSangrias);

        if (sangria.getValor().compareTo(saldoDisponivel) > 0) {
            throw new IllegalStateException("Saldo insuficiente no dinheiro para realizar a sangria! Disponível: R$ " + saldoDisponivel.setScale(2, RoundingMode.HALF_UP));
        }
        sangriaRepository.save(sangria);

        if (sangria.getNomeImpressora() != null) {
            processarImpressaoService.processarConteudoSangria(sangria);
        }

        auditoriaService.salvarAuditoria("CADASTRAR", "SANGRIA", "Cadastrou sangria de R$ " + sangria.getValor() + " motivo: " + sangria.getMotivo(), PermissaoUtil.getUsuarioLogado().getNome(), sangria.getCaixa().getMatriz().getId());

        return new MensagemDTO("Sangria realizada com sucesso!", HttpStatus.OK);
    }

    public MensagemDTO editarSangria(Long id, SangriaDTO sangriaDTO) {
        PermissaoUtil.validarOuLancar("editarSangria");

        Sangria sangriaExistente = sangriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sangria com ID " + id + " não encontrada!"));

        Sangria sangriaAtualizada = dtoToEntity.DTOToSangria(sangriaDTO);
        sangriaAtualizada.setId(id); // Garante que o ID seja mantido

        Optional<Caixa> caixaAtivoOptional = caixaRepository.findCaixaAtivaByFuncionarioId(sangriaAtualizada.getFuncionario().getId());
        if (caixaAtivoOptional.isEmpty()) {
            return new MensagemDTO("Não há caixa ativo para o funcionário!", HttpStatus.BAD_REQUEST);
        }
        Caixa caixa = caixaAtivoOptional.get();
        Long caixaId = caixa.getId();

        BigDecimal totalDinheiro = Optional.ofNullable(caixaRepository.findTotalDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal valorAbertura = Optional.ofNullable(caixa.getValorAbertura()).orElse(BigDecimal.ZERO);
        BigDecimal totalSuprimentos = Optional.ofNullable(suprimentoRepository.findTotalSuprimentosByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalSangrias = Optional.ofNullable(sangriaRepository.findTotalSangriasByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalGorjetasDinheiro = Optional.ofNullable(gorjetaRepository.findTotalGorjetasDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalServicoDinheiro = Optional.ofNullable(caixaRepository.findTotalServicoDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);
        BigDecimal totalDescontosDinheiro = Optional.ofNullable(caixaRepository.findTotalDescontoDinheiroByCaixaId(caixaId)).orElse(BigDecimal.ZERO);

        BigDecimal saldoDisponivel = totalDinheiro
                .add(valorAbertura)
                .add(totalSuprimentos)
                .add(totalGorjetasDinheiro)
                .add(totalServicoDinheiro)
                .subtract(totalDescontosDinheiro)
                .subtract(totalSangrias);

        BigDecimal valorOriginal = sangriaExistente.getValor() != null ? sangriaExistente.getValor() : BigDecimal.ZERO;
        BigDecimal valorNovo = sangriaAtualizada.getValor() != null ? sangriaAtualizada.getValor() : BigDecimal.ZERO;

        if (valorNovo.compareTo(valorOriginal) > 0) {
            BigDecimal diferenca = valorNovo.subtract(valorOriginal);
            if (diferenca.compareTo(saldoDisponivel) > 0) {
                throw new IllegalStateException("Saldo insuficiente para aumentar o valor da sangria!");
            }
        }

        sangriaAtualizada.setCaixa(sangriaExistente.getCaixa());
        sangriaRepository.save(sangriaAtualizada);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "SANGRIA",
                "Editou sangria de R$ " + valorOriginal + " para R$ " + valorNovo + " motivo: " + sangriaAtualizada.getMotivo(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                sangriaAtualizada.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Sangria atualizada com sucesso!", HttpStatus.OK);
    }

    public MensagemDTO deletarSangria(Long id) {
        PermissaoUtil.validarOuLancar("deletarSangria");
        Sangria sangriaBanco = sangriaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Sangria com ID " + id + " não existe!"));
        desativarSangria(sangriaBanco);
        auditoriaService.salvarAuditoria("DELETAR", "SANGRIA", "Deletou sangria de R$ " + sangriaBanco.getValor() + " motivo: " + sangriaBanco.getMotivo(), PermissaoUtil.getUsuarioLogado().getNome(), sangriaBanco.getCaixa().getMatriz().getId());
        return new MensagemDTO("Sangria deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarSangria(Sangria sangria) {
        sangria.setAtivo(false);
        sangriaRepository.save(sangria);
    }
}
