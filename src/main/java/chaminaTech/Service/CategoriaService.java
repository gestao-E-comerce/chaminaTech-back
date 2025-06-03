package chaminaTech.Service;

import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.*;
import chaminaTech.Repository.CategoriaRepository;
import chaminaTech.Repository.ProdutoRepository;
import chaminaTech.DTO.CategoriaDTO;
import chaminaTech.DTO.MensagemDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private AuditoriaService auditoriaService;

    public CategoriaDTO findCategoriaById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrado!"));
        return entityToDTO.categoriaToDTO(categoria);
    }

    public List<CategoriaDTO> listarCategorias(Long matrizId, Boolean ativo, String nome) {
        List<Categoria> categorias = categoriaRepository.listarCategorias(matrizId, ativo, nome);

        return categorias.stream()
                .map(entityToDTO::categoriaToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarCategoria(CategoriaDTO categoriaDTO) {
        PermissaoUtil.validarOuLancar("cadastrarCategoria");
        Categoria categoria = dtoToEntity.DTOToCategoria(categoriaDTO);

        if (categoriaRepository.existsByNomeAndMatrizIdAndDeletado(categoriaDTO.getMatriz().getId(), categoria.getNome(), false)) {
            throw new IllegalStateException("Já existe um categoria com esse nome!");
        }

        if (categoria.getObservacoesCategoria() != null) {
            List<String> nomes = new ArrayList<>();
            for (Observacoes observacoes : categoria.getObservacoesCategoria()) {
                String nome = observacoes.getObservacao().trim().toLowerCase();

                if (nomes.contains(nome)) {
                    throw new IllegalStateException("Já existe uma observação com o nome '" + observacoes.getObservacao() + "' nessa categoria.");
                }

                nomes.add(nome);
                observacoes.setCategoria(categoria);

                if (observacoes.getObservacaoProdutos() != null) {
                    for (ObservacaoProduto observacaoProduto : observacoes.getObservacaoProdutos()) {
                        observacaoProduto.setObservacoes(observacoes);
                    }
                }

                if (observacoes.getObservacaoMaterias() != null) {
                    for (ObservacaoMateria observacaoMateria : observacoes.getObservacaoMaterias()) {
                        observacaoMateria.setObservacoes(observacoes);
                    }
                }
            }
        }

        categoriaRepository.save(categoria);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CATEGORIA",
                "Cadastrou a categoria: " + categoria.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                categoria.getMatriz().getId()
        );
        return new MensagemDTO("Categoria cadastrado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarCategoria(Long id, CategoriaDTO categoriaDTO) {
        PermissaoUtil.validarOuLancar("editarCategoria");
        categoriaDTO.setId(id);
        Categoria categoria = dtoToEntity.DTOToCategoria(categoriaDTO);

        if (categoriaRepository.existsByNomeAndMatrizIdAndDeletadoAndNotId(categoriaDTO.getMatriz().getId(), categoria.getNome(), false, categoria.getId())) {
            throw new IllegalStateException("Já existe um categoria com esse nome!");
        }

        if (categoria.getObservacoesCategoria() != null) {
            List<String> nomes = new ArrayList<>();
            for (Observacoes observacoes : categoria.getObservacoesCategoria()) {
                String nome = observacoes.getObservacao().trim().toLowerCase();

                if (nomes.contains(nome)) {
                    throw new IllegalStateException("Já existe uma observação com o nome '" + observacoes.getObservacao() + "' nessa categoria.");
                }

                nomes.add(nome);
                observacoes.setCategoria(categoria);

                if (observacoes.getObservacaoProdutos() != null) {
                    for (ObservacaoProduto observacaoProduto : observacoes.getObservacaoProdutos()) {
                        observacaoProduto.setObservacoes(observacoes);
                    }
                }

                if (observacoes.getObservacaoMaterias() != null) {
                    for (ObservacaoMateria observacaoMateria : observacoes.getObservacaoMaterias()) {
                        observacaoMateria.setObservacoes(observacoes);
                    }
                }
            }
        }

        categoriaRepository.save(categoria);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CATEGORIA",
                "Editou a categoria: " + categoria.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                categoria.getMatriz().getId()
        );
        return new MensagemDTO("Categoria atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO ativarOuDesativarCategoria(Long id, CategoriaDTO categoriaDTO) {
        PermissaoUtil.validarOuLancar("editarCategoria");

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        boolean novoStatus = !Boolean.TRUE.equals(categoria.getAtivo());
        categoria.setAtivo(novoStatus);

        List<Produto> produtos = produtoRepository.findByCategoriaId(id);

        if (!produtos.isEmpty()) {
            for (Produto produto : produtos) {
                produto.setAtivo(novoStatus);
            }
            produtoRepository.saveAll(produtos);
        }

        categoriaRepository.save(categoria);
        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                "CATEGORIA",
                (novoStatus ? "Ativou" : "Desativou") + " a categoria: " + categoria.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                categoria.getMatriz().getId()
        );

        String mensagem = novoStatus
                ? "Categoria e produtos ativados com sucesso!"
                : "Categoria e produtos desativados com sucesso!";

        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO deletarCategoria(Long id) {
        PermissaoUtil.validarOuLancar("deletarCategoria");
        Categoria categoriaBanco = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não existe!"));

        List<Produto> produtos = produtoRepository.findByCategoriaId(id);

        if (!produtos.isEmpty()) {
            throw new IllegalStateException("Não é possível deletar uma categoria que possui produtos vinculados.");
        }
        desativarCategoria(categoriaBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CATEGORIA",
                "Deletou a categoria: " + categoriaBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                categoriaBanco.getMatriz().getId()
        );
        return new MensagemDTO("Categoria deledato com sucesso!", HttpStatus.CREATED);
    }

    private void desativarCategoria(Categoria categoria) {
        categoria.setDeletado(true);
        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }
}