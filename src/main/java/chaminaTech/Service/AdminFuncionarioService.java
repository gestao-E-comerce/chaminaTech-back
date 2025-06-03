package chaminaTech.Service;

import chaminaTech.DTO.AdminFuncionarioDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Entity.AdminFuncionario;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.AdminFuncionarioRepository;
import chaminaTech.Repository.LoginRepository;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminFuncionarioService {
    @Autowired
    private AdminFuncionarioRepository adminFuncionarioRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private AuditoriaService auditoriaService;

    public AdminFuncionarioDTO findAdminFuncionarioById(Long id) {
        AdminFuncionario adminFuncionario = adminFuncionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado!"));
        return entityToDTO.adminFuncionarioToDTO(adminFuncionario);
    }

    public List<AdminFuncionarioDTO> listarAdminFuncionarios(Long adminId, String termoPesquisa, Boolean ativo) {
        PermissaoUtil.validarOuLancar("funcionario");
        try {
            Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
            return adminFuncionarioRepository.buscarFuncionarios(adminId, termoPesquisa, ativo).stream()
                    .filter(funcionario -> {
                        // só aplica o filtro se o logado for FUNCIONARIO
                        if ("ADMINFUNCIONARIO".equalsIgnoreCase(usuarioLogado.getRole())) {
                            return !funcionario.getId().equals(usuarioLogado.getId());
                        }
                        return true; // outros tipos de usuário não filtram nada
                    })
                    .map(entityToDTO::adminFuncionarioToDTO)
                    .collect(Collectors.toList());

        }  finally {
            PermissaoUtil.limparUsuarioLogado();
        }
    }

    public MensagemDTO cadastrarAdminFuncionario(AdminFuncionarioDTO adminFuncionarioDTO) {
        PermissaoUtil.validarOuLancar("cadastrarFuncionario");
        AdminFuncionario adminFuncionario = dtoToEntity.DTOToAdminFuncionario(adminFuncionarioDTO);
        if (adminFuncionario.getPassword() == null) {
            throw new IllegalStateException("Password obrigatório!");
        }
        if (loginRepository.existsByUsername(adminFuncionario.getUsername())) {
            throw new IllegalStateException("Username já está em uso.");
        }

        adminFuncionario.setRole("ADMINFUNCIONARIO");
        adminFuncionario.setPassword(passwordEncoder.encode(adminFuncionario.getPassword()));
        if (adminFuncionarioRepository.existsByNomeAndAdminIdAndDeletado(adminFuncionarioDTO.getAdmin().getId(), adminFuncionario.getNome())) {
            throw new IllegalStateException("UserName indispensável!");
        }

        adminFuncionarioRepository.save(adminFuncionario);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "SUBFUNCIONARIO",
                "Cadastrou funcionário: " + adminFuncionario.getNome(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Funcionário cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarAdminFuncionario(Long id, AdminFuncionarioDTO adminFuncionarioDTO) {
        PermissaoUtil.validarOuLancar("editarFuncionario");
        adminFuncionarioDTO.setId(id);
        AdminFuncionario adminFuncionario = dtoToEntity.DTOToAdminFuncionario(adminFuncionarioDTO);
        if (loginRepository.existsByUsernameExcludingId(adminFuncionario.getUsername(), adminFuncionario.getId())) {
            throw new IllegalStateException("UserName indispensável!.");
        }
        if (adminFuncionario.getPassword() == null) {
            String senha = loginRepository.findSenhaById(adminFuncionario.getId());
            adminFuncionario.setPassword(senha);
        } else {
            adminFuncionario.setPassword(passwordEncoder.encode(adminFuncionario.getPassword()));
        }

        if (adminFuncionarioRepository.existsByNomeAndAdminIdAndDeletadoAndNotId(adminFuncionarioDTO.getAdmin().getId(), adminFuncionario.getNome(), adminFuncionario.getId())) {
            throw new IllegalStateException("Já existe um adminFuncionario com esse nome!");
        }

        adminFuncionarioRepository.save(adminFuncionario);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "EDITAR",
                "SUBFUNCIONARIO",
                "Editou funcionário: " + adminFuncionario.getNome(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Funcionário atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO ativarOuDesativarAdminFuncionario(Long id, AdminFuncionarioDTO adminFuncionarioDTO) {
        PermissaoUtil.validarOuLancar("editarFuncionario");
        adminFuncionarioDTO.setId(id);
        AdminFuncionario adminFuncionario = dtoToEntity.DTOToAdminFuncionario(adminFuncionarioDTO);

        // Recupera a senha atual do banco, já que você não edita ela aqui
        String senha = loginRepository.findSenhaById(adminFuncionario.getId());
        adminFuncionario.setPassword(senha);

        // Inverte o status atual
        boolean novoStatus = !Boolean.TRUE.equals(adminFuncionarioDTO.getAtivo());
        adminFuncionario.setAtivo(novoStatus);

        adminFuncionarioRepository.save(adminFuncionario);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                "SUBFUNCIONARIO",
                (novoStatus ? "Ativou" : "Desativou") + " funcionário: " + adminFuncionario.getNome(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        String mensagem = novoStatus ? "Funcionário ativado com sucesso!" : "Funcionário desativado com sucesso!";

        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO deletarAdminFuncionario(Long id) {
        PermissaoUtil.validarOuLancar("deletarFuncionario");
        AdminFuncionario funcionarioBanco = adminFuncionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário com ID " + id + " não existe!"));

        desativarFuncionario(funcionarioBanco);
        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria = usuarioLogado.getRole().equals("ADMIN")
                ? usuarioLogado.getId()
                : ((AdminFuncionario) usuarioLogado).getAdmin().getId();

        auditoriaService.salvarAuditoria(
                "DELETAR",
                "SUBFUNCIONARIO",
                "Deletou funcionário: " + funcionarioBanco.getNome(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Funcionário deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarFuncionario(AdminFuncionario adminFuncionario) {
        adminFuncionario.setDeletado(true);
        adminFuncionario.setAtivo(false);
        adminFuncionarioRepository.save(adminFuncionario);
    }
}
