package chaminaTech.Service;

import chaminaTech.Config.JwtServiceGenerator;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Funcionario;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.FuncionarioRepository;
import chaminaTech.Repository.LoginRepository;
import chaminaTech.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private JwtServiceGenerator jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private EntityToDTO entityToDTO;

    public Object logar(LoginDTO loginDTO) {
        try {
            // Tenta autenticar o usuário
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getSenha()
                    )
            );

            // Verifica se o usuário existe e se está ativo
            Usuario usuario = loginRepository.findByUsername(loginDTO.getUsername()).orElse(null);

            if (usuario != null) {
                // Verifica se o usuário está ativo
                if (!usuario.getAtivo()) {
                    // Se o usuário não estiver ativo, lança uma exceção informando o motivo
                    throw new IllegalStateException("Usuário desativado");
                }

                // Se o usuário for encontrado e estiver ativo, gera um token
                var jwtToken = jwtService.generateToken(usuario);
                return toUsuarioDTO(usuario, jwtToken); // Retorna o DTO com o token
            } else {
                // Se o usuário não for encontrado, retorna uma mensagem de erro
                throw new IllegalStateException("Usuário ou senha inválidos");
            }
        } catch (AuthenticationException e) {
            throw new IllegalStateException("Usuário ou senha inválidos", e);
        }
    }

    public UsuarioDTO buscarUsuarioPermissao(LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getSenha()
                )
        );

        Usuario usuario = loginRepository.findByUsername(loginDTO.getUsername()).orElse(null);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        return toUsuarioDTOUsuario(usuario);

    }

    public UsuarioDTO buscarPorUsernameESenha(Long matrizId, LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getSenha()
                )
        );

        Funcionario funcionario = funcionarioRepository.buscarFuncionarios(matrizId, null,null).stream()
                .filter(f -> f.getUsername().equals(loginDTO.getUsername()))
                .findFirst()
                .orElse(null);

        if (funcionario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return toUsuarioDTOBuscarPorFuncionario(funcionario);

    }

    private UsuarioDTO toUsuarioDTO(Usuario usuario, String token) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();

        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setRole(usuario.getRole());
        usuarioDTO.setPassword(null);
        usuarioDTO.setToken(token);
        usuarioDTO.setUsername(usuario.getUsername());
        return usuarioDTO;
    }

    private UsuarioDTO toUsuarioDTOUsuario(Usuario usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();

        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setRole(usuario.getRole());
        usuarioDTO.setUsername(usuario.getUsername());

        if (usuario.getPermissao() != null) {
            PermissaoDTO permissaoDTO = entityToDTO.permissaoToDTO(usuario.getPermissao());

            usuarioDTO.setPermissao(permissaoDTO);
        }
        return usuarioDTO;
    }

    private FuncionarioDTO toUsuarioDTOBuscarPorFuncionario(Funcionario funcionario) {
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();

        funcionarioDTO.setId(funcionario.getId());
        funcionarioDTO.setAtivo(funcionario.getAtivo());
        funcionarioDTO.setDeletado(funcionario.getDeletado());
        funcionarioDTO.setNome(funcionario.getNome());
        funcionarioDTO.setUsername(funcionario.getUsername());
        funcionarioDTO.setCelular(funcionario.getCelular());
        funcionarioDTO.setEmail(funcionario.getEmail());
        funcionarioDTO.setRole(funcionario.getRole());

        MatrizDTO matrizDTO = new MatrizDTO();
        if (funcionario.getMatriz() != null) {
            matrizDTO.setId(funcionario.getMatriz().getId());
            funcionarioDTO.setMatriz(matrizDTO);
        }

        return funcionarioDTO;
    }
}