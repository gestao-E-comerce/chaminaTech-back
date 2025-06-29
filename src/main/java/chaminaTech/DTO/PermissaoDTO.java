package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissaoDTO {
    private Long id;
    private String nome;

    private Boolean venda = false;
    private Boolean transferirVenda = false;
    private Boolean liberarVenda = false;
    private Boolean cadastrarVenda = false;
    private Boolean deletarVenda = false;
    private Boolean historicoVenda = false;
    private Boolean imprimir = false;

    private Boolean vendaBalcao = false;
    private Boolean vendaMesa = false;
    private Boolean vendaEntrega = false;
    private Boolean vendaRetirada = false;

    private Boolean editarProdutoVenda = false;
    private Boolean deletarProdutoVenda = false;

    private Boolean caixa = false;
    private Boolean editarCaixa = false;
    private Boolean deletarCaixa = false;
    private Boolean historicoCaixa = false;

    private Boolean cadastrarSangria = false;
    private Boolean editarSangria = false;
    private Boolean deletarSangria = false;

    private Boolean cadastrarSuprimento = false;
    private Boolean editarSuprimento = false;
    private Boolean deletarSuprimento = false;

    private Boolean cadastrarGorjeta = false;
    private Boolean editarGorjeta = false;
    private Boolean deletarGorjeta = false;

    private Boolean categoria = false;
    private Boolean cadastrarCategoria = false;
    private Boolean editarCategoria = false;
    private Boolean deletarCategoria = false;

    private Boolean cliente = false;
    private Boolean cadastrarCliente = false;
    private Boolean editarCliente = false;
    private Boolean deletarCliente = false;

    private Boolean estoque = false;
    private Boolean cadastrarEstoque = false;
    private Boolean editarEstoque = false;

    private Boolean deposito = false;
    private Boolean cadastrarDeposito = false;
    private Boolean editarDeposito = false;

    private Boolean funcionario = false;
    private Boolean cadastrarFuncionario = false;
    private Boolean editarFuncionario = false;
    private Boolean deletarFuncionario = false;

    private Boolean permissao = false;
    private Boolean cadastrarPermissao = false;
    private Boolean editarPermissao = false;
    private Boolean deletarPermissao = false;

    private Boolean materia = false;
    private Boolean cadastrarMateria = false;
    private Boolean editarMateria = false;
    private Boolean deletarMateria = false;

    private Boolean filho = false;
    private Boolean cadastrarFilho = false;
    private Boolean editarFilho = false;
    private Boolean deletarFilho = false;

    private Boolean matrizPermissao = false;
    private Boolean cadastrarMatriz = false;
    private Boolean editarMatriz = false;

    private Boolean produto = false;
    private Boolean cadastrarProduto = false;
    private Boolean editarProduto = false;
    private Boolean deletarProduto = false;

    private Boolean editarConfiguracoes = false;
    private Boolean auditoria = false;

    private Boolean relatorio = false;
    private Boolean cadastrarRelatorio = false;
    private Boolean editarRelatorio = false;
    private Boolean deletarRelatorio = false;

    @JsonIgnoreProperties(value = {"permissao"})
    private UsuarioDTO usuario;
}