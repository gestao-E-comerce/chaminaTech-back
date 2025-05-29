package chaminaTech.Service;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.ProdutoDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Estoque;
import chaminaTech.Entity.Produto;
import chaminaTech.Repository.EstoqueRepository;
import chaminaTech.Repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private AuditoriaService auditoriaService;

    public ProdutoDTO obterProdutoPorCodigoEMatriz(Integer codigo, Long matrizId) {
        Optional<Produto> produtoOptional = produtoRepository.findByCodigoAndMatrizIdAndAtivo(codigo, matrizId);
        return produtoOptional.map(entityToDTO::produtoToDTO).orElse(null);
    }

    public List<ProdutoDTO> listarProdutosComFiltro(Long matrizId, Boolean deletado, Boolean ativo, Boolean cardapio, Boolean estocavel, Boolean validarExestencia, String categoriaNome, String nome) {
        List<Produto> produtos = produtoRepository.listarProdutos(matrizId, deletado, ativo, cardapio, estocavel, validarExestencia, categoriaNome, nome);

        return produtos.stream()
                .map(entityToDTO::produtoToDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoDTO> listarProdutosEstoques(Long matrizId, String termoPesquisa) {
        List<Object[]> resultados = produtoRepository.listarProdutosEstoques(matrizId, termoPesquisa);
        return resultados.stream().map(obj -> {
            Produto produto = (Produto) obj[0];
            Double quantidade = obj[1] != null ? ((BigDecimal) obj[1]).doubleValue() : 0.0;

            ProdutoDTO dto = entityToDTO.produtoToDTO(produto);
            dto.setQuantidadeDisponivel(quantidade);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ProdutoDTO> listarProdutosEstoquesDescartados(Long matrizId, String termoPesquisa) {
        List<Object[]> resultados = produtoRepository.listarProdutosEstoquesDescartados(matrizId, termoPesquisa);
        return resultados.stream().map(obj -> {
            Produto produto = (Produto) obj[0];
            Double quantidade = obj[1] != null ? ((BigDecimal) obj[1]).doubleValue() : 0.0;

            ProdutoDTO dto = entityToDTO.produtoToDTO(produto);
            dto.setQuantidadeDescartada(quantidade);
            return dto;
        }).collect(Collectors.toList());
    }

    public MensagemDTO cadastrarProduto(ProdutoDTO produtoDTO) {
        PermissaoUtil.validarOuLancar("cadastrarProduto");
        Produto produto = dtoToEntity.DTOToProduto(produtoDTO);

        if (produtoRepository.existsByNomeAndMatrizIdAndDeletado(produtoDTO.getMatriz().getId(), produto.getNome(), false)) {
            throw new IllegalStateException("Já existe um produto com esse nome!");
        }

        if (produtoRepository.existsByCodigoAndMatrizIdAndDeletado(produtoDTO.getMatriz().getId(), produto.getCodigo(), false)) {
            throw new IllegalStateException("Já existe um produto com esse codigo!");
        }

        if (produto.getProdutoMaterias() != null) {
            for (int i = 0; i < produto.getProdutoMaterias().size(); i++) {
                produto.getProdutoMaterias().get(i).setProduto(produto);
            }
        }

        if (produto.getProdutoCompostos() != null) {
            for (int i = 0; i < produto.getProdutoCompostos().size(); i++) {
                produto.getProdutoCompostos().get(i).setProduto(produto);
            }
        }

        produtoRepository.save(produto);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "PRODUTO",
                "Cadastrou o produto: " + produto.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                produto.getMatriz().getId()
        );
        return new MensagemDTO("Produto cadastrado com sucesso!", HttpStatus.CREATED);
    }

    @Transactional
    public MensagemDTO editarProduto(Long id, ProdutoDTO produtoDTO) throws Exception {
        PermissaoUtil.validarOuLancar("editarProduto");
        produtoDTO.setId(id);
        Produto produto = dtoToEntity.DTOToProduto(produtoDTO);
        if (produtoRepository.existsByNomeAndMatrizIdAndDeletadoAndNotId(produtoDTO.getMatriz().getId(), produto.getNome(), false, produto.getId())) {
            throw new IllegalStateException("Já existe um produto com esse nome!");
        }
        if (produtoRepository.existsByCodigoAndMatrizIdAndDeletadoAndNotId(produtoDTO.getMatriz().getId(), produto.getCodigo(), false, produto.getId())) {
            throw new IllegalStateException("Já existe um produto com esse codigo!");
        }

        if (!produto.getValidarExestencia()) {
            // Consulta para verificar se há estoques ativos associados a esse produto na matriz
            List<Estoque> estoquesAtivos = estoqueRepository.findByProdutoAndMatrizIdAndAtivoTrue(produto, produto.getMatriz().getId());

            if (!estoquesAtivos.isEmpty()) {
                // Se houver estoques ativos e validarExestencia for false, lança uma exceção
                throw new Exception("Não é possível desativar a validação de existência enquanto houver estoque ativo na matriz.");
            }
        }

        if (produto.getProdutoMaterias() != null)
            for (int i = 0; i < produto.getProdutoMaterias().size(); i++) {
                produto.getProdutoMaterias().get(i).setProduto(produto);
            }

        if (produto.getProdutoCompostos() != null)
            for (int i = 0; i < produto.getProdutoCompostos().size(); i++) {
                produto.getProdutoCompostos().get(i).setProduto(produto);
            }

        produtoRepository.save(produto);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "PRODUTO",
                "Editou o produto: " + produto.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                produto.getMatriz().getId()
        );
        return new MensagemDTO("Produto atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO ativarOuDesativarProduto(Long id, ProdutoDTO produtoDTO) {
        PermissaoUtil.validarOuLancar("editarProduto");

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        // Inverte o status atual
        boolean novoStatus = !Boolean.TRUE.equals(produto.getAtivo());
        produto.setAtivo(novoStatus);

        // Se estiver tentando ativar, verifica se a categoria está ativa
        if (novoStatus && !produto.getCategoria().getAtivo()) {
            throw new IllegalStateException("Não é possível ativar o produto. A categoria está desativada.");
        }
        if (produtoRepository.existsEmObservacao(id)) {
            throw new IllegalStateException("Produto não pode ser desativado pois pois está em uso em uma observação.");
        }

        if (produtoRepository.existsEmProdutoComposto(id)) {
            throw new IllegalStateException("Produto não pode ser desativado pois está em um produto composto.");
        }

        produtoRepository.save(produto);
        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                "PRODUTO",
                (novoStatus ? "Ativou" : "Desativou") + " o produto: " + produto.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                produto.getMatriz().getId()
        );

        String mensagem = novoStatus
                ? "Produto ativado com sucesso!"
                : "Produto desativado com sucesso!";

        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO deletarProduto(Long id) {
        PermissaoUtil.validarOuLancar("deletarProduto");
        Produto produtoBanco = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não existe!"));

        if (produtoRepository.existsEmVendaAtiva(id)) {
            throw new IllegalStateException("Produto não pode ser deletado pois está vinculado a uma venda.");
        }

        if (produtoRepository.existsEmEstoqueAtivo(id)) {
            throw new IllegalStateException("Produto não pode ser deletado pois está em estoque ativo.");
        }

        if (produtoRepository.existsEmObservacao(id)) {
            throw new IllegalStateException("Produto não pode ser deletado pois está em uso em uma observação.");
        }

        if (produtoRepository.existsEmProdutoMateria(id)) {
            throw new IllegalStateException("Produto não pode ser deletado pois está vinculado como matéria-prima.");
        }

        if (produtoRepository.existsEmProdutoComposto(id)) {
            throw new IllegalStateException("Produto não pode ser deletado pois está em um produto composto.");
        }

        desativarProduto(produtoBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "PRODUTO",
                "Deletou o produto: " + produtoBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                produtoBanco.getMatriz().getId()
        );
        return new MensagemDTO("Produto deledato com sucesso!", HttpStatus.CREATED);
    }

    private void desativarProduto(Produto produto) {
        produto.setDeletado(true);
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}