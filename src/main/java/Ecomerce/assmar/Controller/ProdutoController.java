package Ecomerce.assmar.Controller;

import Ecomerce.assmar.DTO.ProdutoDTO;
import Ecomerce.assmar.DTO.MensagemDTO;
import Ecomerce.assmar.Service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/produto")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdutoController {
    @Autowired
    ProdutoService produtoService;

    @GetMapping("/codigo/{codigo}/matriz/{matrizId}")
    public ResponseEntity<ProdutoDTO> obterProdutoPorCodigoEMatriz(@PathVariable Integer codigo, @PathVariable Long matrizId) {
        ProdutoDTO produtoDTO = produtoService.obterProdutoPorCodigoEMatriz(codigo, matrizId);
        if (produtoDTO != null) {
            return ResponseEntity.ok(produtoDTO);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ProdutoDTO> findProdutoById(@PathVariable Long id) {
//        ProdutoDTO produtoDTO = produtoService.findProdutoById(id);
//        if (produtoDTO != null) {
//            return ResponseEntity.ok(produtoDTO);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/lista")
    public ResponseEntity<List<ProdutoDTO>> listarComFiltro(
            @RequestParam Long matrizId,
            @RequestParam Boolean deletado,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Boolean cardapio,
            @RequestParam(required = false) Boolean estocavel,
            @RequestParam(required = false) Boolean validarExestencia,
            @RequestParam(required = false) String categoriaNome,
            @RequestParam(required = false) String nome
    ) {
        List<ProdutoDTO> resultado = produtoService.listarProdutosComFiltro(matrizId, deletado, ativo, cardapio, estocavel, validarExestencia, categoriaNome, nome);
        return ResponseEntity.ok(resultado);
    }
    @GetMapping("/listarProdutosEstoque")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosEstoque(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String termoPesquisa) {
        return ResponseEntity.ok(produtoService.listarProdutosEstoques(matrizId, termoPesquisa));
    }

    @GetMapping("/listarProdutosEstoqueDescartados")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosEstoqueDescartados(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String termoPesquisa) {
        return ResponseEntity.ok(produtoService.listarProdutosEstoquesDescartados(matrizId, termoPesquisa));
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarProduto(@RequestBody ProdutoDTO produtoDTO) {
        try{
            return ResponseEntity.ok(produtoService.cadastrarProduto(produtoDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarProduto(@PathVariable Long id, @RequestBody ProdutoDTO produtoDTO) {
        try {
            return ResponseEntity.ok(produtoService.editarProduto(id, produtoDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<MensagemDTO> desativarProduto(@PathVariable Long id, @RequestBody ProdutoDTO produtoDTO) {
        try {
            return ResponseEntity.ok(produtoService.ativarOuDesativarProduto(id, produtoDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarProduto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(produtoService.deletarProduto(id));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}