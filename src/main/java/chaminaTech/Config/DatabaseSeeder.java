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
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            admin.setAtivo(true);

            admin = adminRepository.save(admin);

            Permissao permissao = new Permissao();
            permissao.setNome("Permissão total do admin padrão");
            permissao.setUsuario(admin);

            permissao.setFuncionario(true);
            permissao.setCadastrarFuncionario(true);
            permissao.setEditarFuncionario(true);
            permissao.setDeletarFuncionario(true);
            permissao.setEditarConfiguracoes(true);
            permissao.setAuditoria(true);
            permissao.setPermissao(true);
            permissao.setCadastrarPermissao(true);
            permissao.setEditarPermissao(true);
            permissao.setDeletarPermissao(true);

            permissaoRepository.save(permissao);

            admin.setPermissao(permissao);
            adminRepository.save(admin);
        }
    }
}
