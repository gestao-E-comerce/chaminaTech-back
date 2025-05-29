package Ecomerce.assmar.Service;

import Ecomerce.assmar.DTO.*;
import Ecomerce.assmar.DTOService.*;
import Ecomerce.assmar.Entity.*;
import Ecomerce.assmar.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatrizService {
    @Autowired
    private MatrizRepository matrizRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private AuditoriaService auditoriaService;

    public MatrizDTO findMatrizById(Long id) {
        Matriz matriz = matrizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Matriz não encontrado!"));
        return entityToDTO.matrizToDTO(matriz);
    }

    public List<MatrizDTO> listarFilhosPorMatrizId(Long matrizId) {
        return matrizRepository.findByMatrizIdAndAtivo(matrizId, true).stream()
                .map(entityToDTO::matrizToDTO)
                .collect(Collectors.toList());
    }

    public MensagemDTO cadastrarMatriz(MatrizDTO matrizDTO) {
        Matriz matriz = dtoToEntity.DTOToMatriz(matrizDTO);
        if (matriz.getPassword() == null) {
            throw new IllegalStateException("Password obrigatório!");
        }
        if (loginRepository.existsByUsername(matriz.getUsername())) {
            throw new IllegalStateException("Username já está em uso.");
        }
        matriz.setPassword(passwordEncoder.encode(matriz.getPassword()));
        if (matrizRepository.existsByNomeAndDeletado(matriz.getNome(), false)) {
            throw new IllegalStateException("Já existe uma matriz com esse nome!");
        }

        if (matriz.getImpressoras() != null) {
            for (int i = 0; i < matriz.getImpressoras().size(); i++) {
                matriz.getImpressoras().get(i).setMatriz(matriz);
            }
        }
        if (matriz.getIdentificador() != null) {
            for (int i = 0; i < matriz.getIdentificador().size(); i++) {
                matriz.getIdentificador().get(i).setMatriz(matriz);
            }
        }
        if (matriz.getTaxasEntregaKm() != null) {
            for (int i = 0; i < matriz.getTaxasEntregaKm().size(); i++) {
                matriz.getTaxasEntregaKm().get(i).setMatriz(matriz);
            }
        }
        String tipo;
        String descricao;

        if (matriz.getMatriz() != null) {
            matriz.setRole("FILHO");
            tipo = "FILHO";
            descricao = "Cadastrou um novo filho: " + matriz.getNome();
        } else {
            matriz.setRole("MATRIZ");
            tipo = "MATRIZ";
            descricao = "Cadastrou uma nova matriz: " + matriz.getNome();
        }
        matrizRepository.save(matriz);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria;
        switch (usuarioLogado.getRole()) {
            case "ADMIN":
                matrizIdAuditoria = usuarioLogado.getId();
                break;
            case "ADMINFUNCIONARIO":
                matrizIdAuditoria = ((AdminFuncionario) usuarioLogado).getAdmin().getId();
                break;
            default:
                throw new IllegalStateException("Tipo de usuário não suportado para auditoria.");
        }

        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                tipo,
                descricao,
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );

        String mensagem = matriz.getMatriz() != null
                ? "Filho cadastrado com sucesso!"
                : "Matriz cadastrada com sucesso!";
        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }

    public MensagemDTO editarMatriz(Long id, MatrizDTO matrizDTO) {
        PermissaoUtil.validarOuLancar("editarConfiguracoes");
        matrizDTO.setId(id);
        Matriz matriz = dtoToEntity.DTOToMatriz(matrizDTO);
        if (loginRepository.existsByUsernameExcludingId(matriz.getUsername(), matriz.getId())) {
            throw new IllegalStateException("UserName indispensável!.");
        }
        if (matrizRepository.existsByNomeAndDeletadoAndNotId(matrizDTO.getNome(), matrizDTO.getId())) {
            throw new IllegalStateException("Já existe uma matriz com esse nome!");
        }
        if (matriz.getPassword() == null) {
            String senha = loginRepository.findSenhaById(matriz.getId());
            matriz.setPassword(senha);
        } else {
            matriz.setPassword(passwordEncoder.encode(matriz.getPassword()));
        }

        if (matriz.getImpressoras() != null) {
            for (int i = 0; i < matriz.getImpressoras().size(); i++) {
                matriz.getImpressoras().get(i).setMatriz(matriz);
            }
        }
        if (matriz.getIdentificador() != null) {
            for (int i = 0; i < matriz.getIdentificador().size(); i++) {
                matriz.getIdentificador().get(i).setMatriz(matriz);
            }
        }

        if (matriz.getTaxasEntregaKm() != null) {
            for (int i = 0; i < matriz.getTaxasEntregaKm().size(); i++) {
                matriz.getTaxasEntregaKm().get(i).setMatriz(matriz);
            }
        }
        if (matriz.getPermissao() != null) {
            Permissao permissaoMatriz = matriz.getPermissao();
            List<Funcionario> funcionarios = funcionarioRepository.buscarFuncionarios(matriz.getId(), false, null, null);

            if (funcionarios != null && !funcionarios.isEmpty()) {
                for (Funcionario funcionario : funcionarios) {
                    Permissao p = funcionario.getPermissao();
                    if (p != null) {
                        if (!Boolean.TRUE.equals(permissaoMatriz.getVendaBalcao())) p.setVendaBalcao(false);
                        if (!Boolean.TRUE.equals(permissaoMatriz.getVendaEntrega())) p.setVendaEntrega(false);
                        if (!Boolean.TRUE.equals(permissaoMatriz.getVendaMesa())) p.setVendaMesa(false);
                        if (!Boolean.TRUE.equals(permissaoMatriz.getVendaRetirada())) p.setVendaRetirada(false);
                        if (!Boolean.TRUE.equals(permissaoMatriz.getEstoque())) p.setEstoque(false);
                        if (!Boolean.TRUE.equals(permissaoMatriz.getDeposito())) p.setDeposito(false);
                        if (!Boolean.TRUE.equals(permissaoMatriz.getMateria())) p.setMateria(false);
                        if (!Boolean.TRUE.equals(permissaoMatriz.getFilho())) p.setFilho(false);
                    }
                }

                // 🔐 persistir alterações
                funcionarioRepository.saveAll(funcionarios);
            }
        }


        // Verificar se alguma impressora foi removida
        List<Impressora> impressorasAtuais = matrizRepository.findById(id).get().getImpressoras();
        List<Impressora> impressorasNovas = matriz.getImpressoras();

        // Encontra impressoras que foram removidas
        List<Impressora> impressorasRemovidas = new ArrayList<>();
        for (Impressora impressoraAtual : impressorasAtuais) {
            boolean impressoraExiste = false;
            for (Impressora novaImpressora : impressorasNovas) {
                if (impressoraAtual.getId().equals(novaImpressora.getId())) {
                    impressoraExiste = true;
                    break;
                }
            }
            if (!impressoraExiste) {
                impressorasRemovidas.add(impressoraAtual);
            }
        }

        // Verifica se alguma das impressoras removidas está associada a produtos
        for (Impressora impressoraRemovida : impressorasRemovidas) {
            List<Produto> produtosRelacionados = produtoRepository.findProdutosByImpressoraAndMatriz(impressoraRemovida, matriz.getId());

            if (!produtosRelacionados.isEmpty()) {
                for (Produto produto : produtosRelacionados) {
                    produto.getImpressoras().remove(impressoraRemovida);
                    produtoRepository.save(produto);  // Salva o produto atualizado
                }
            }
        }

        if (matriz.getImpressoras() != null) {
            for (int i = 0; i < matriz.getImpressoras().size(); i++) {
                matriz.getImpressoras().get(i).setMatriz(matriz);
            }
        }
        matriz.setForcarRemocaoImpressora(false);
        matrizRepository.save(matriz);

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria;
        switch (usuarioLogado.getRole()) {
            case "MATRIZ", "ADMIN":
                matrizIdAuditoria = usuarioLogado.getId();
                break;
            case "FUNCIONARIO":
                matrizIdAuditoria = matriz.getId();
                break;
            case "ADMINFUNCIONARIO":
                matrizIdAuditoria = ((AdminFuncionario) usuarioLogado).getAdmin().getId();
                break;
            default:
                throw new IllegalStateException("Tipo de usuário não suportado para auditoria.");
        }

        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CONFIGURACAO",
                "Editou as configurações da matriz: " + matriz.getNome(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO("Matriz atualizado com sucesso!", HttpStatus.CREATED);
    }

    @Transactional
    public MensagemDTO ativarOuDesativarMatriz(Long id, MatrizDTO matrizDTO) {
        matrizDTO.setId(id);
        Matriz matriz = dtoToEntity.DTOToMatriz(matrizDTO);

        // Recupera a senha atual do banco, já que você não edita ela aqui
        String senha = loginRepository.findSenhaById(matriz.getId());
        matriz.setPassword(senha);

        // Inverte o status atual
        boolean novoStatus = !Boolean.TRUE.equals(matrizDTO.getAtivo());
        matriz.setAtivo(novoStatus);

        matrizRepository.save(matriz);

        String mensagem = novoStatus ? "Matriz ativado com sucesso!" : "Matriz desativado com sucesso!";

        if (!matriz.getAtivo()) {
            List<Funcionario> funcionarios = funcionarioRepository.findByMatrizId(id);
            funcionarios.forEach(f -> f.setAtivo(false));
            funcionarioRepository.saveAll(funcionarios);
        }

        String tipo;
        if (matriz.getMatriz() != null) {
            tipo = "FILHO";
        } else {
            tipo = "MATRIZ";
        }

        Usuario usuarioLogado = PermissaoUtil.getUsuarioLogado();
        Long matrizIdAuditoria;
        switch (usuarioLogado.getRole()) {
            case "ADMIN":
                matrizIdAuditoria = usuarioLogado.getId();
                break;
            case "ADMINFUNCIONARIO":
                matrizIdAuditoria = ((AdminFuncionario) usuarioLogado).getAdmin().getId();
                break;
            default:
                throw new IllegalStateException("Tipo de usuário não suportado para auditoria.");
        }

        auditoriaService.salvarAuditoria(
                novoStatus ? "ATIVAR" : "DESATIVAR",
                tipo,
                (novoStatus ? "Ativou" : "Desativou") + " a matriz: " + matriz.getNome(),
                usuarioLogado.getNome(),
                matrizIdAuditoria
        );
        return new MensagemDTO(mensagem, HttpStatus.CREATED);
    }
}