package chaminaTech.Service;

import chaminaTech.DTO.AdminDTO;
import chaminaTech.DTO.MatrizDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.Entity.Admin;
import chaminaTech.Repository.AdminRepository;
import chaminaTech.Repository.LoginRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private LoginRepository loginRepository;

    public AdminDTO findAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado!"));
        return entityToDTO.adminToDTO(admin);
    }

    public MensagemDTO editarAdmin(Long id, AdminDTO adminDTO) {
        adminDTO.setId(id);
        Admin admin = dtoToEntity.DTOToAdmin(adminDTO);
        if (loginRepository.existsByUsernameExcludingId(admin.getUsername(), admin.getId())) {
            throw new IllegalStateException("UserName indispensável!.");
        }
        if (admin.getPassword() == null){
            String senha = loginRepository.findSenhaById(admin.getId());
            admin.setPassword(senha);
        } else {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }

        adminRepository.save(admin);
        return new MensagemDTO("Admin atualizado com sucesso!", HttpStatus.CREATED);
    }

    public List<MatrizDTO> listarMatrizes(Boolean deletado, String termoPesquisa, Boolean ativo) {
        return adminRepository.findAllMatrizes(deletado, termoPesquisa, ativo).stream().map(entityToDTO::matrizToDTO).toList();
    }
    public List<MatrizDTO> listarFilhos(Boolean deletado, String termoPesquisa, Boolean ativo) {
        return adminRepository.findAllFilhos(deletado, termoPesquisa, ativo).stream().map(entityToDTO::matrizToDTO).toList();
    }

    public String buscarChaveApiCoordenadas() {
        return adminRepository.findByRole("ADMIN")
                .map(Admin::getChaveApiCoordenades)
                .orElseThrow(() -> new RuntimeException("Administrador com chave não encontrado."));
    }
}