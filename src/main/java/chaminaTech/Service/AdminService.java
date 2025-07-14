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

import java.util.ArrayList;
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
            throw new IllegalStateException("Username inválido! Tente outro!");
        }
        if (admin.getPassword() == null) {
            String senha = loginRepository.findSenhaById(admin.getId());
            admin.setPassword(senha);
        } else {
            validarSenhaOuLancar(admin.getPassword());
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }

        adminRepository.save(admin);
        return new MensagemDTO("Admin atualizado com sucesso!", HttpStatus.CREATED);
    }

    public List<MatrizDTO> listarMatrizes(String termoPesquisa, Boolean ativo) {
        return adminRepository.findAllMatrizes(termoPesquisa, ativo).stream().map(entityToDTO::matrizToDTO).toList();
    }

    public List<MatrizDTO> listarFilhos(String termoPesquisa, Boolean ativo) {
        return adminRepository.findAllFilhos(termoPesquisa, ativo).stream().map(entityToDTO::matrizToDTO).toList();
    }

    public String buscarChaveApiCoordenadas() {
        return adminRepository.findByRole("ADMIN")
                .map(Admin::getChaveApiCoordenades)
                .orElseThrow(() -> new RuntimeException("Administrador com chave não encontrado."));
    }

    private void validarSenhaOuLancar(String senha) {
        List<String> erros = new ArrayList<>();

        if (senha.length() < 8) erros.add("mínimo de 8 caracteres");
        if (!senha.matches(".*[A-Z].*")) erros.add("1 letra maiúscula");
        if (!senha.matches(".*[a-z].*")) erros.add("1 letra minúscula");
        if (!senha.matches(".*\\d.*")) erros.add("1 número");
        if (!senha.matches(".*[\\W_].*")) erros.add("1 caractere especial");
        if (senha.matches(".*\\s.*")) erros.add("sem espaços");

        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Senha inválida: deve conter " + String.join(", ", erros) + ".");
        }
    }
}