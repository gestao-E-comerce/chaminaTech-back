package chaminaTech.DTOService;

import chaminaTech.Entity.Permissao;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.LoginRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PermissaoUtil {
    private static LoginRepository loginRepository;

    private static final ThreadLocal<Usuario> usuarioLogadoThread = new ThreadLocal<>();

    public PermissaoUtil(LoginRepository repo) {
        PermissaoUtil.loginRepository = repo;
    }

    public static boolean validar(String permissaoChave) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = loginRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuarioLogadoThread.set(usuario);

        Permissao permissao = usuario.getPermissao();
        if (permissao == null) return false;

        return switch (permissaoChave) {
            case "transferirVenda" -> permissao.getTransferirVenda();
            case "liberarVenda" -> permissao.getLiberarVenda();
            case "cadastrarVenda" -> permissao.getCadastrarVenda();
            case "deletarVenda" -> permissao.getDeletarVenda();
            case "historicoVenda" -> permissao.getHistoricoVenda();
            case "imprimir" -> permissao.getImprimir();

            case "editarCaixa" -> permissao.getEditarCaixa();
            case "deletarCaixa" -> permissao.getDeletarCaixa();
            case "historicoCaixa" -> permissao.getHistoricoCaixa();

            case "cadastrarSangria" -> permissao.getCadastrarSangria();
            case "editarSangria" -> permissao.getEditarSangria();
            case "deletarSangria" -> permissao.getDeletarSangria();

            case "cadastrarSuprimento" -> permissao.getCadastrarSuprimento();
            case "editarSuprimento" -> permissao.getEditarSuprimento();
            case "deletarSuprimento" -> permissao.getDeletarSuprimento();

            case "categoria" -> permissao.getCategoria();
            case "cadastrarCategoria" -> permissao.getCadastrarCategoria();
            case "editarCategoria" -> permissao.getEditarCategoria();
            case "deletarCategoria" -> permissao.getDeletarCategoria();

            case "cliente" -> permissao.getCliente();
            case "cadastrarCliente" -> permissao.getCadastrarCliente();
            case "editarCliente" -> permissao.getEditarCliente();
            case "deletarCliente" -> permissao.getDeletarCliente();

            case "estoque" -> permissao.getEstoque();
            case "cadastrarEstoque" -> permissao.getCadastrarEstoque();
            case "editarEstoque" -> permissao.getEditarEstoque();

            case "deposito" -> permissao.getDeposito();
            case "cadastrarDeposito" -> permissao.getCadastrarDeposito();
            case "editarDeposito" -> permissao.getEditarDeposito();

            case "funcionario" -> permissao.getFuncionario();
            case "cadastrarFuncionario" -> permissao.getCadastrarFuncionario();
            case "editarFuncionario" -> permissao.getEditarFuncionario();
            case "deletarFuncionario" -> permissao.getDeletarFuncionario();

            case "permissao" -> permissao.getPermissao();
            case "cadastrarPermissao" -> permissao.getCadastrarPermissao();
            case "editarPermissao" -> permissao.getEditarPermissao();
            case "deletarPermissao" -> permissao.getDeletarPermissao();

            case "materia" -> permissao.getMateria();
            case "cadastrarMateria" -> permissao.getCadastrarMateria();
            case "editarMateria" -> permissao.getEditarMateria();
            case "deletarMateria" -> permissao.getDeletarMateria();

            case "filho" -> permissao.getFilho();
            case "cadastrarFilho" -> permissao.getCadastrarFilho();
            case "editarFilho" -> permissao.getEditarFilho();
            case "deletarFilho" -> permissao.getDeletarFilho();

            case "matrizPermissao" -> permissao.getMatrizPermissao();
            case "cadastrarMatriz" -> permissao.getCadastrarMatriz();
            case "editarMatriz" -> permissao.getEditarMatriz();

            case "produto" -> permissao.getProduto();
            case "cadastrarProduto" -> permissao.getCadastrarProduto();
            case "editarProduto" -> permissao.getEditarProduto();
            case "deletarProduto" -> permissao.getDeletarProduto();

            case "editarConfiguracoes" -> permissao.getEditarConfiguracoes();
            case "auditoria" -> permissao.getAuditoria();

            default -> false;
        };
    }

    public static void validarOuLancar(String permissaoChave) {
        if (!validar(permissaoChave)) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "Você não tem permissão para: " + permissaoChave);
        }
    }

    public static void validarAlgumaOuLancar(String... permissoes) {
        for (String permissao : permissoes) {
            if (validar(permissao)) {
                return; // Se tiver ao menos uma, libera
            }
        }
        throw new ResponseStatusException(HttpStatus.LOCKED, "Você não tem permissão");
    }


    public static Usuario getUsuarioLogado() {
        Usuario usuario = usuarioLogadoThread.get();
        if (usuario == null) {
            throw new IllegalStateException("Usuário logado ainda não foi carregado");
        }
        return usuario;
    }

    public static void limparUsuarioLogado() {
        usuarioLogadoThread.remove();
    }
}
