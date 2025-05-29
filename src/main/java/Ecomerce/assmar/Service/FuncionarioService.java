package Ecomerce.assmar.Service;

import Ecomerce.assmar.DTO.*;
import Ecomerce.assmar.DTOService.DTOToEntity;
import Ecomerce.assmar.DTOService.EntityToDTO;
import Ecomerce.assmar.DTOService.PermissaoUtil;
import Ecomerce.assmar.Entity.*;
import Ecomerce.assmar.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {
    @Autowired
    private FuncionarioRepository funcionarioRepository;
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

    public FuncionarioDTO findFuncionarioById(Long id) {
        Funcionario funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado!"));
        return entityToDTO.funcionarioToDTO(funcionario);
    }

    public List<FuncionarioDTO> listarFuncionarios(Long matrizId, Boolean deletado, String termoPesquisa, Boolean ativo) {
        PermissaoUtil.validarOuLancar("funcionario");
        try {
            Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();

            return funcionarioRepository.buscarFuncionarios(matrizId, deletado, termoPesquisa, ativo).stream()
                    .filter(funcionario -> {
                        // só aplica o filtro se o logado for FUNCIONARIO
                        if ("FUNCIONARIO".equalsIgnoreCase(usuarioLogado.getRole())) {
                            return !funcionario.getId().equals(usuarioLogado.getId());
                        }
                        return true; // outros tipos de usuário não filtram nada
                    })
                    .map(entityToDTO::funcionarioToDTO)
                    .collect(Collectors.toList());

        }  finally {
            PermissaoUtil.limparUsuarioLogado();
        }
    }

    public MensagemDTO salvarPreferenciasImpressao(List<FuncionarioDTO> funcionariosDTO) {
        List<Funcionario> funcionarios = funcionariosDTO.stream()
                .map(dtoToEntity::DTOToFuncionario)
                .collect(Collectors.toList());

        for (Funcionario f : funcionarios) {
            String senha = loginRepository.findSenhaById(f.getId());
            f.setPassword(senha); // mantém a senha existente
        }

        funcionarioRepository.saveAll(funcionarios);
        return new MensagemDTO("Preferências de impressão atualizadas com sucesso!", HttpStatus.OK);
    }

    public MensagemDTO cadastrarFuncionario(FuncionarioDTO funcionarioDTO) {
        PermissaoUtil.validarOuLancar("cadastrarFuncionario");
        Funcionario funcionario = dtoToEntity.DTOToFuncionario(funcionarioDTO);
        if (funcionario.getPassword() == null) {
            throw new IllegalStateException("Password obrigatório!");
        }
        if (loginRepository.existsByUsername(funcionario.getUsername())) {
            throw new IllegalStateException("Username já está em uso.");
        }
        funcionario.setRole("FUNCIONARIO");
        funcionario.setPassword(passwordEncoder.encode(funcionario.getPassword()));
        if (funcionarioRepository.existsByNomeAndMatrizIdAndDeletado(funcionario.getMatriz().getId(), funcionario.getNome(), false)) {
            throw new IllegalStateException("Já existe um funcionario com esse nome!");
        }

        funcionarioRepository.save(funcionario);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "FUNCIONARIO",
                "Cadastrou o funcionário: " + funcionario.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                funcionario.getMatriz().getId()
        );
        return new MensagemDTO("Funcionário cadastrada com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarFuncionario(Long id, FuncionarioDTO funcionarioDTO) {
        PermissaoUtil.validarOuLancar("editarFuncionario");
        funcionarioDTO.setId(id);
        Funcionario funcionario = dtoToEntity.DTOToFuncionario(funcionarioDTO);
        if (loginRepository.existsByUsernameExcludingId(funcionario.getUsername(), funcionario.getId())) {
            throw new IllegalStateException("UserName indispensável!.");
        }
        if (funcionario.getPassword() == null) {
            String senha = loginRepository.findSenhaById(funcionario.getId());
            funcionario.setPassword(senha);
        } else {
            funcionario.setPassword(passwordEncoder.encode(funcionario.getPassword()));
        }

        if (funcionarioRepository.existsByNomeAndMatrizIdAndDeletadoAndNotId(funcionarioDTO.getMatriz().getId(), funcionario.getNome(), false, funcionario.getId())) {
            throw new IllegalStateException("Já existe um funcionario com esse nome!");
        }

        funcionarioRepository.save(funcionario);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "FUNCIONARIO",
                "Editou o funcionário: " + funcionario.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                funcionario.getMatriz().getId()
        );
        return new MensagemDTO("Funcionário atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO ativarOuDesativarFuncionario(Long id, FuncionarioDTO funcionarioDTO) {
        PermissaoUtil.validarOuLancar("editarFuncionario");
        funcionarioDTO.setId(id);
        Funcionario funcionario = dtoToEntity.DTOToFuncionario(funcionarioDTO);

        // Recupera a senha atual do banco, já que você não edita ela aqui
        String senha = loginRepository.findSenhaById(funcionario.getId());
        funcionario.setPassword(senha);

        // Inverte o status atual
        boolean novoStatus = !Boolean.TRUE.equals(funcionarioDTO.getAtivo());
        funcionario.setAtivo(novoStatus);

        funcionarioRepository.save(funcionario);
        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                "FUNCIONARIO",
                (novoStatus ? "Ativou" : "Desativou") + " o funcionário: " + funcionario.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                funcionario.getMatriz().getId()
        );
        String mensagem = novoStatus ? "Funcionário ativado com sucesso!" : "Funcionário desativado com sucesso!";

        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO deletarFuncionario(Long id) {
        PermissaoUtil.validarOuLancar("deletarFuncionario");
        Funcionario funcionarioBanco = funcionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário com ID " + id + " não existe!"));

        boolean temVenda = funcionarioRepository.existsByFuncionarioEmVendaAtiva(funcionarioBanco.getId());
        boolean temCaixa = funcionarioRepository.existsCaixaAtivoPorFuncionario(funcionarioBanco.getId());

        if (temVenda) {
            throw new IllegalStateException("Funcionário não pode ser deletado pois está vinculado a uma venda ativa.");
        }

        if (temCaixa) {
            throw new IllegalStateException("Funcionário não pode ser deletado pois está vinculado a um caixa ativo.");
        }

        desativarFuncionario(funcionarioBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "FUNCIONARIO",
                "Deletou o funcionário: " + funcionarioBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                funcionarioBanco.getMatriz().getId()
        );
        return new MensagemDTO("Funcionário deletado com sucesso!", HttpStatus.CREATED);
    }

    private void desativarFuncionario(Funcionario funcionario) {
        funcionario.setDeletado(true);
        funcionario.setAtivo(false);
        funcionarioRepository.save(funcionario);
    }
}