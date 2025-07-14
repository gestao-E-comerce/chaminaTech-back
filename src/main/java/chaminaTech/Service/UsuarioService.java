package chaminaTech.Service;

import chaminaTech.Config.JwtServiceGenerator;
import chaminaTech.DTO.UsuarioDTO;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtServiceGenerator jwtService;

    public UsuarioDTO findUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado!"));
        return entityToDTO.usuarioToDTO(usuario);
    }

    public UsuarioDTO findUsuarioByToken(String token) {
        try {
            var claims = jwtService.decryptToken(token); // Usa o JwtServiceGenerator
            Long id = Long.parseLong(claims.getStringClaim("id"));
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            return entityToDTO.usuarioToDTO(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair usuário do token.", e);
        }
    }
}
