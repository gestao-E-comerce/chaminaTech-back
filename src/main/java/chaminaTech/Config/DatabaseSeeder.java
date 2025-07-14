package chaminaTech.Config;

import chaminaTech.Entity.Admin;
import chaminaTech.Entity.Permissao;
import chaminaTech.Repository.AdminRepository;
import chaminaTech.Repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PermissaoRepository permissaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setNome("Administrador Padrão");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole("ADMIN");
            admin.setAtivo(true);

            admin = adminRepository.save(admin);

            Permissao permissao = new Permissao();
            permissao.setNome("Permissão total do admin padrão");
            permissao.setUsuario(admin);

            permissao.setVenda(true);
            permissao.setTransferirVenda(true);
            permissao.setLiberarVenda(true);
            permissao.setCadastrarVenda(true);
            permissao.setDeletarVenda(true);
            permissao.setHistoricoVenda(true);
            permissao.setImprimir(true);
            permissao.setVendaBalcao(true);
            permissao.setVendaMesa(true);
            permissao.setVendaEntrega(true);
            permissao.setVendaRetirada(true);
            permissao.setEditarProdutoVenda(true);
            permissao.setDeletarProdutoVenda(true);
            permissao.setCaixa(true);
            permissao.setDeletarCaixa(true);
            permissao.setEditarCaixa(true);
            permissao.setHistoricoCaixa(true);
            permissao.setCadastrarSangria(true);
            permissao.setEditarSangria(true);
            permissao.setDeletarSangria(true);
            permissao.setCadastrarSuprimento(true);
            permissao.setEditarSuprimento(true);
            permissao.setDeletarSuprimento(true);
            permissao.setCadastrarGorjeta(true);
            permissao.setEditarGorjeta(true);
            permissao.setDeletarGorjeta(true);
            permissao.setCategoria(true);
            permissao.setCadastrarCategoria(true);
            permissao.setEditarCategoria(true);
            permissao.setDeletarCategoria(true);
            permissao.setCliente(true);
            permissao.setCadastrarCliente(true);
            permissao.setEditarCliente(true);
            permissao.setDeletarCliente(true);
            permissao.setEstoque(true);
            permissao.setEditarEstoque(true);
            permissao.setCadastrarEstoque(true);
            permissao.setDeposito(true);
            permissao.setCadastrarDeposito(true);
            permissao.setEditarDeposito(true);
            permissao.setFuncionario(true);
            permissao.setCadastrarFuncionario(true);
            permissao.setEditarFuncionario(true);
            permissao.setDeletarFuncionario(true);
            permissao.setPermissao(true);
            permissao.setCadastrarPermissao(true);
            permissao.setEditarPermissao(true);
            permissao.setDeletarPermissao(true);
            permissao.setMateria(true);
            permissao.setCadastrarMateria(true);
            permissao.setEditarMateria(true);
            permissao.setDeletarMateria(true);
            permissao.setFilho(true);
            permissao.setCadastrarFilho(true);
            permissao.setEditarFilho(true);
            permissao.setDeletarFilho(true);
            permissao.setMatrizPermissao(true);
            permissao.setEditarMatriz(true);
            permissao.setCadastrarMatriz(true);
            permissao.setProduto(true);
            permissao.setCadastrarProduto(true);
            permissao.setEditarProduto(true);
            permissao.setDeletarProduto(true);
            permissao.setEditarConfiguracoes(true);
            permissao.setAuditoria(true);
            permissao.setRelatorio(true);
            permissao.setCadastrarRelatorio(true);
            permissao.setEditarRelatorio(true);
            permissao.setDeletarRelatorio(true);

            permissaoRepository.save(permissao);

            admin.setPermissao(permissao);
            adminRepository.save(admin);
        }
    }
}
