package chaminaTech.Service;

import chaminaTech.DTO.DepositoDTO;
import chaminaTech.DTO.DepositoDescartarDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Deposito;
import chaminaTech.Entity.DepositoDescartar;
import chaminaTech.Entity.Materia;
import chaminaTech.Entity.Matriz;
import chaminaTech.Repository.DepositoDescartarRepository;
import chaminaTech.Repository.DepositoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepositoService {
    @Autowired
    private DepositoRepository depositoRepository;
    @Autowired
    private DepositoDescartarRepository depositoDescartarRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<DepositoDTO> listarDepositos(Long matrizId, Boolean deletado, Boolean ativo, String materiaNome) {
        List<Deposito> depositos = depositoRepository.listarDepositos(matrizId, deletado, ativo, materiaNome);

        return depositos.stream()
                .map(entityToDTO::depositoToDTO)
                .collect(Collectors.toList());
    }

    public List<DepositoDescartarDTO> listarDepositosDescartados(Long matrizId, String materiaNome) {
        List<DepositoDescartar> depositosDescartados = depositoDescartarRepository.listarDepositosDescartados(matrizId, materiaNome);

        return depositosDescartados.stream()
                .map(entityToDTO::depositoDescartarToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarDeposito(DepositoDTO depositoDTO) {
        PermissaoUtil.validarOuLancar("cadastrarDeposito");
        Deposito deposito = dtoToEntity.DTOToDeposito(depositoDTO);

        deposito.setQuantidadeVendido(BigDecimal.ZERO);
        deposito.setDataCadastrar(new Timestamp(System.currentTimeMillis()));

        depositoRepository.save(deposito);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "DEPOSITO",
                "Cadastrou depósito de " + deposito.getQuantidade() + " para " + deposito.getMateria().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                deposito.getMatriz().getId()
        );
        return new MensagemDTO("Materia adicionada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarDeposito(Long id, DepositoDTO depositoDTO) {
        PermissaoUtil.validarOuLancar("editarDeposito");
        depositoDTO.setId(id);
        Deposito deposito = dtoToEntity.DTOToDeposito(depositoDTO);
        depositoRepository.save(deposito);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "DEPOSITO",
                "Editou depósito de " + deposito.getQuantidade() + " para " + deposito.getMateria().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                deposito.getMatriz().getId()
        );
        return new MensagemDTO("Deposito atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO ativarOuDesativarDeposito(Long id, DepositoDTO depositoDTO) {
        PermissaoUtil.validarOuLancar("editarDeposito");
        depositoDTO.setId(id);
        Deposito deposito = dtoToEntity.DTOToDeposito(depositoDTO);

        // Inverte o status atual
        boolean novoStatus = !Boolean.TRUE.equals(depositoDTO.getAtivo());
        deposito.setAtivo(novoStatus);

        depositoRepository.save(deposito);

        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                "DEPOSITO",
                (novoStatus ? "Ativou" : "Desativou") + " depósito de " + deposito.getQuantidade() + " para " + deposito.getMateria().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                deposito.getMatriz().getId()
        );
        String mensagem = novoStatus
                ? "Deposito ativado com sucesso!"
                : "Deposito desativado com sucesso!";

        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO descartarDeposito(DepositoDescartarDTO depositoDescartarDTO) {
        PermissaoUtil.validarOuLancar("editarDeposito");
        DepositoDescartar depositoDescartar = dtoToEntity.DTOToDepositoDescartar(depositoDescartarDTO);

        Materia materia = depositoDescartar.getMateria();
        Matriz matriz = depositoDescartar.getMatriz();
        BigDecimal quantidadeDescartar = depositoDescartar.getQuantidade();
        BigDecimal depositoTotalDisponivel = BigDecimal.ZERO;

        // Busca os depósitos ativos, ordenados pela data (FIFO)
        List<Deposito> depositos = depositoRepository.findByMateriaAndMatrizIdAndAtivoTrueOrderByDataAsc(materia, matriz.getId());

        for (Deposito deposito : depositos) {
            BigDecimal quantidadeDisponivel = deposito.getQuantidade().subtract(deposito.getQuantidadeVendido());
            depositoTotalDisponivel = depositoTotalDisponivel.add(quantidadeDisponivel);
        }

        if (depositoTotalDisponivel.compareTo(quantidadeDescartar) < 0) {
            throw new IllegalStateException("Depósito insuficiente para a matéria: " + materia.getNome());
        }

        List<Deposito> depositosParaSalvar = new ArrayList<>();

        for (Deposito deposito : depositos) {
            BigDecimal quantidadeDisponivel = deposito.getQuantidade().subtract(deposito.getQuantidadeVendido());

            if (quantidadeDisponivel.compareTo(quantidadeDescartar) >= 0) {
                deposito.setQuantidadeVendido(deposito.getQuantidadeVendido().add(quantidadeDescartar));
                quantidadeDescartar = BigDecimal.ZERO;
            } else {
                deposito.setQuantidadeVendido(deposito.getQuantidadeVendido().add(quantidadeDisponivel));
                quantidadeDescartar = quantidadeDescartar.subtract(quantidadeDisponivel);
            }

            if (deposito.getQuantidadeVendido().compareTo(deposito.getQuantidade()) >= 0) {
                deposito.setAtivo(false);
                deposito.setDataDesativar(new Timestamp(System.currentTimeMillis()));
            }

            depositosParaSalvar.add(deposito);

            if (quantidadeDescartar.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        depositoRepository.saveAll(depositosParaSalvar);
        depositoDescartar.setDataDescartar(new Timestamp(System.currentTimeMillis()));
        depositoDescartarRepository.save(depositoDescartar);
        auditoriaService.salvarAuditoria(
                "DESCARTAR",
                "DEPOSITO",
                "Descartou " + depositoDescartar.getQuantidade() + " da matéria " + depositoDescartar.getMateria().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                depositoDescartar.getMatriz().getId()
        );
        return new MensagemDTO("Matéria descartada com sucesso!", HttpStatus.OK);
    }
}