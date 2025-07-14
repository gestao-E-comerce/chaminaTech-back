package chaminaTech.Service;

import chaminaTech.DTO.EstoqueDTO;
import chaminaTech.DTO.EstoqueDescartarDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.*;
import chaminaTech.Entity.*;
import chaminaTech.Repository.DepositoRepository;
import chaminaTech.Repository.EstoqueDescartarRepository;
import chaminaTech.Repository.EstoqueRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstoqueService {
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private DepositoRepository depositoRepository;
    @Autowired
    private TratarEstoqueDeposito tratarEstoqueDeposito;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private EstoqueDescartarRepository estoqueDescartarRepository;
    @Autowired
    private AuditoriaService auditoriaService;


    public List<EstoqueDTO> listarEstoques(Long matrizId, Boolean ativo, String produtoNome) {
        List<Estoque> estoques = estoqueRepository.listarEstoques(matrizId, ativo, produtoNome);

        return estoques.stream().map(entityToDTO::estoqueToDTO).collect(Collectors.toList());
    }

    public List<EstoqueDescartarDTO> listarEstoquesDescartados(Long matrizId, String produtoNome) {
        List<EstoqueDescartar> estoquesDescartados = estoqueDescartarRepository.listarEstoquesDescartados(matrizId, produtoNome);

        return estoquesDescartados.stream().map(entityToDTO::estoqueDescartarToDTO).collect(Collectors.toList());
    }

    @Transactional
    public MensagemDTO cadastrarEstoque(EstoqueDTO estoqueDTO) {
        PermissaoUtil.validarOuLancar("cadastrarEstoque");
        Estoque estoque = dtoToEntity.DTOToEstoque(estoqueDTO);
        Produto produto = estoque.getProduto();
        Matriz matriz = estoque.getMatriz();
        ResultadoDesconto resultado = new ResultadoDesconto();

        if (produto.getProdutoMaterias() != null && !produto.getProdutoMaterias().isEmpty()) {
            List<Deposito> depositos = tratarEstoqueDeposito.descontarDepositoEstoque(produto, matriz);
            resultado.addAllDeposito(depositos);
        }

        if (produto.getProdutoCompostos() != null && !produto.getProdutoCompostos().isEmpty()) {
            ResultadoDesconto sub = tratarEstoqueDeposito.processarProdutoCompostoEstoque(
                    produto.getProdutoCompostos(), estoque.getQuantidade(), matriz
            );
            resultado.addAllDeposito(sub.getDepositos());
            resultado.addAllEstoque(sub.getEstoques());
        }

        if (!resultado.getEstoques().isEmpty()) {
            estoqueRepository.saveAll(resultado.getEstoques());
        }

        if (!resultado.getDepositos().isEmpty()) {
            depositoRepository.saveAll(resultado.getDepositos());
        }

        estoque.setQuantidadeVendido(BigDecimal.ZERO);
        estoque.setDataCadastrar(new Timestamp(System.currentTimeMillis()));
        estoqueRepository.save(estoque);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "ESTOQUE",
                "Cadastrou estoque de " + estoque.getQuantidade() + " para " + estoque.getProduto().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                estoque.getMatriz().getId()
        );
        return new MensagemDTO("Produto adicionado no estoque com sucesso!", HttpStatus.CREATED);
    }


    public MensagemDTO editarEstoque(Long id, EstoqueDTO estoqueDTO) {
        PermissaoUtil.validarOuLancar("editarEstoque");
        estoqueDTO.setId(id);
        Estoque estoque = dtoToEntity.DTOToEstoque(estoqueDTO);
        estoqueRepository.save(estoque);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "ESTOQUE",
                "Editou estoque de " + estoque.getQuantidade() + " para " + estoque.getProduto().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                estoque.getMatriz().getId()
        );
        return new MensagemDTO("Estoque atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO ativarOuDesativarEstoque(Long id, EstoqueDTO estoqueDTO) {
        PermissaoUtil.validarOuLancar("editarEstoque");
        estoqueDTO.setId(id);
        Estoque estoque = dtoToEntity.DTOToEstoque(estoqueDTO);

        // Inverte o status atual
        boolean novoStatus = !Boolean.TRUE.equals(estoqueDTO.getAtivo());
        estoque.setAtivo(novoStatus);

        estoqueRepository.save(estoque);
        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                "ESTOQUE",
                (novoStatus ? "Ativou" : "Desativou") + " estoque de " + estoque.getQuantidade() + " para " + estoque.getProduto().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                estoque.getMatriz().getId()
        );
        String mensagem = novoStatus ? "Estoque ativado com sucesso!" : "Estoque desativado com sucesso!";

        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO descartarEstoque(EstoqueDescartarDTO estoqueDescartarDTO) {
        PermissaoUtil.validarOuLancar("editarEstoque");
        EstoqueDescartar estoqueDescartar = dtoToEntity.DTOToEstoqueDescartar(estoqueDescartarDTO);

        Produto produto = estoqueDescartar.getProduto();
        Matriz matriz = estoqueDescartar.getMatriz();
        BigDecimal quantidadeDescartar = estoqueDescartar.getQuantidade();
        BigDecimal estoqueTotalDisponivel = BigDecimal.ZERO;

        // Busca os depósitos ativos, ordenados pela data (FIFO)
        List<Estoque> estoques = estoqueRepository.findByProdutoAndMatrizIdAndAtivoTrueOrderByDataAsc(produto, matriz.getId());

        for (Estoque estoque : estoques) {
            BigDecimal quantidadeDisponivel = estoque.getQuantidade().subtract(estoque.getQuantidadeVendido());
            estoqueTotalDisponivel = estoqueTotalDisponivel.add(quantidadeDisponivel);
        }

        if (estoqueTotalDisponivel.compareTo(quantidadeDescartar) < 0) {
            throw new IllegalStateException("Estoque insuficiente para a matéria: " + produto.getNome());
        }

        List<Estoque> estoquesParaSalvar = new ArrayList<>();

        for (Estoque estoque : estoques) {
            BigDecimal quantidadeDisponivel = estoque.getQuantidade().subtract(estoque.getQuantidadeVendido());

            if (quantidadeDisponivel.compareTo(quantidadeDescartar) >= 0) {
                estoque.setQuantidadeVendido(estoque.getQuantidadeVendido().add(quantidadeDescartar));
                quantidadeDescartar = BigDecimal.ZERO;
            } else {
                estoque.setQuantidadeVendido(estoque.getQuantidadeVendido().add(quantidadeDisponivel));
                quantidadeDescartar = quantidadeDescartar.subtract(quantidadeDisponivel);
            }

            if (estoque.getQuantidadeVendido().compareTo(estoque.getQuantidade()) >= 0) {
                estoque.setAtivo(false);
                estoque.setDataDesativar(new Timestamp(System.currentTimeMillis()));
            }

            estoquesParaSalvar.add(estoque);

            if (quantidadeDescartar.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        estoqueRepository.saveAll(estoquesParaSalvar);
        estoqueDescartar.setDataDescartar(new Timestamp(System.currentTimeMillis()));
        estoqueDescartarRepository.save(estoqueDescartar);
        auditoriaService.salvarAuditoria(
                "DESCARTAR",
                "ESTOQUE",
                "Descartou " + estoqueDescartar.getQuantidade() + " do produto " + estoqueDescartar.getProduto().getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                estoqueDescartar.getMatriz().getId()
        );

        return new MensagemDTO("Produto descartado com sucesso!", HttpStatus.OK);
    }
}