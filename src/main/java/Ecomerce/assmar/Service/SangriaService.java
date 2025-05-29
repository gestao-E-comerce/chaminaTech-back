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
public class SangriaService {
    @Autowired
    private EntityToDTO entityToDTO;
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

        Double totalDinheiro = caixaRepository.findTotalDinheiroByCaixaId(caixaId);
        Double valorAbertura = caixa.getValorAbertura();
        Double totalSangrias = sangriaRepository.findTotalSangriasByCaixaId(caixaId);
        Double totalSuprimentos = suprimentoRepository.findTotalSuprimentosByCaixaId(caixaId);

        totalDinheiro = (totalDinheiro != null) ? totalDinheiro : 0.0;
        valorAbertura = (valorAbertura != null) ? valorAbertura : 0.0;
        totalSangrias = (totalSangrias != null) ? totalSangrias : 0.0;
        totalSuprimentos = (totalSuprimentos != null) ? totalSuprimentos : 0.0;

        // Calcula o saldo disponível considerando suprimentos e sangrias já feitas
        Double saldoDisponivel = (totalDinheiro + valorAbertura + totalSuprimentos) - totalSangrias;

        // Verifica se há saldo suficiente para a sangria
        if (sangria.getValor() > saldoDisponivel) {
            return new MensagemDTO("Erro: Saldo insuficiente para realizar a sangria.", HttpStatus.BAD_REQUEST);
        }
        // Salva a sangria no banco
        sangriaRepository.save(sangria);
        if (sangria.getNomeImpressora() != null) {
            processarImpressaoService.processarConteudoSangria(sangria);
        }
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "SANGRIA",
                "Cadastrou sangria de R$ " + sangria.getValor()
                        + " motivo: " + sangria.getMotivo(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                sangria.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Sangria realizada com sucesso!", HttpStatus.OK);
    }

    public MensagemDTO editarSangria(Long id, SangriaDTO sangriaDTO) {
        PermissaoUtil.validarOuLancar("editarSangria");
        Sangria sangriaExistente = sangriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sangria com ID " + sangriaDTO.getId() + " não encontrada!"));

        Sangria sangriaAtualizada = dtoToEntity.DTOToSangria(sangriaDTO);

        // Verifica se o funcionário tem um caixa ativo
        Optional<Caixa> caixaAtivoOptional = caixaRepository.findCaixaAtivaByFuncionarioId(sangriaAtualizada.getFuncionario().getId());
        if (caixaAtivoOptional.isEmpty()) {
            return new MensagemDTO("Não há caixa ativo para o funcionário!", HttpStatus.BAD_REQUEST);
        }
        Caixa caixa = caixaAtivoOptional.get();
        Long caixaId = caixa.getId();

        Double totalDinheiro = caixaRepository.findTotalDinheiroByCaixaId(caixaId);
        Double valorAbertura = caixa.getValorAbertura();
        Double totalSangrias = sangriaRepository.findTotalSangriasByCaixaId(caixaId);
        Double totalSuprimentos = suprimentoRepository.findTotalSuprimentosByCaixaId(caixaId);

        totalDinheiro = (totalDinheiro != null) ? totalDinheiro : 0.0;
        valorAbertura = (valorAbertura != null) ? valorAbertura : 0.0;
        totalSangrias = (totalSangrias != null) ? totalSangrias : 0.0;
        totalSuprimentos = (totalSuprimentos != null) ? totalSuprimentos : 0.0;

        Double saldoDisponivel = (totalDinheiro + valorAbertura + totalSuprimentos) - totalSangrias;

        Double valorOriginal = sangriaExistente.getValor();
        Double valorNovo = sangriaAtualizada.getValor();

        if (valorNovo > valorOriginal) {
            Double valorAtualizado = valorNovo - valorOriginal;

            if (valorAtualizado > saldoDisponivel) {
                return new MensagemDTO("Erro: Saldo insuficiente para realizar a sangria.", HttpStatus.BAD_REQUEST);
            }
        }

        sangriaAtualizada.setCaixa(sangriaExistente.getCaixa());
        sangriaRepository.save(sangriaAtualizada);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "SANGRIA",
                "Editou sangria para R$ " + sangriaAtualizada.getValor()
                        + " motivo: " + sangriaAtualizada.getMotivo(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                sangriaAtualizada.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Sangria atualizada com sucesso!", HttpStatus.OK);
    }

    public MensagemDTO deletarSangria(Long id) {
        PermissaoUtil.validarOuLancar("deletarSangria");
        Sangria sangriaBanco = sangriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sangria com ID " + id + " não existe!"));
        desativarSangria(sangriaBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "SANGRIA",
                "Deletou sangria de R$ " + sangriaBanco.getValor()
                        + " motivo: " + sangriaBanco.getMotivo(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                sangriaBanco.getCaixa().getMatriz().getId()
        );
        return new MensagemDTO("Sangria deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarSangria(Sangria sangria) {
        sangria.setAtivo(false);
        sangriaRepository.save(sangria);
    }
}
