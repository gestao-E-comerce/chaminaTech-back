package Ecomerce.assmar.Service;

import Ecomerce.assmar.DTO.UsuarioDTO;
import Ecomerce.assmar.DTOService.EntityToDTO;
import Ecomerce.assmar.Entity.Usuario;
import Ecomerce.assmar.Repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioDTO findUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado!"));
        return entityToDTO.usuarioToDTO(usuario);
    }
}
