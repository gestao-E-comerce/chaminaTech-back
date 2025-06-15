package chaminaTech.Service;

import chaminaTech.DTO.MatrizDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.*;
import chaminaTech.Entity.Configuracao.ConfiguracaoEntrega;
import chaminaTech.Entity.Configuracao.ConfiguracaoImpressao;
import chaminaTech.Entity.Configuracao.ConfiguracaoRetirada;
import chaminaTech.Entity.Configuracao.ConfiguracaoTaxaServico;
import chaminaTech.Repository.FuncionarioRepository;
import chaminaTech.Repository.LoginRepository;
import chaminaTech.Repository.MatrizRepository;
import chaminaTech.Repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        PermissaoUtil.validarOuLancar("cadastrarMatriz");
        Matriz matriz = dtoToEntity.DTOToMatriz(matrizDTO);
        if (matriz.getPassword() == null) {
            throw new IllegalStateException("Password obrigatório!");
        }
        if (loginRepository.existsByUsername(matriz.getUsername())) {
            throw new IllegalStateException("Username já está em uso.");
        }
        matriz.setPassword(passwordEncoder.encode(matriz.getPassword()));
        if (matrizRepository.existsByNome(matriz.getNome())) {
            throw new IllegalStateException("Já existe uma matriz com esse nome!");
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
        criarConfiguracoesPadrao(matriz);

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

    private void criarConfiguracoesPadrao(Matriz matriz) {
        // Configuração de Impressão
        ConfiguracaoImpressao confImpressao = new ConfiguracaoImpressao();
        confImpressao.setMatriz(matriz);
        confImpressao.setUsarImpressora(true);
        confImpressao.setImprimirComprovanteRecebementoBalcao(true);
        confImpressao.setImprimirComprovanteRecebementoEntrega(true);
        confImpressao.setImprimirComprovanteRecebementoMesa(true);
        confImpressao.setImprimirComprovanteRecebementoRetirada(true);
        confImpressao.setImprimirNotaFiscal(0);
        confImpressao.setImprimirCadastrar(0);
        confImpressao.setImprimirDeletar(0);
        confImpressao.setImprimirComprovanteDeletarVenda(true);
        confImpressao.setImprimirComprovanteDeletarProduto(true);
        confImpressao.setImprimirConferenciaEntrega(true);
        confImpressao.setImprimirConferenciaRetirada(true);
        confImpressao.setImprimirConferenciaCaixa(true);
        confImpressao.setImprimirAberturaCaixa(true);
        confImpressao.setImprimirSangria(true);
        confImpressao.setImprimirSuprimento(true);
        confImpressao.setMostarMotivoDeletarVenda(true);
        confImpressao.setMostarMotivoDeletarProduto(true);
        matriz.setConfiguracaoImpressao(confImpressao);

        // Configuração de Entrega
        ConfiguracaoEntrega confEntrega = new ConfiguracaoEntrega();
        confEntrega.setMatriz(matriz);
        confEntrega.setCalcular(0);
        matriz.setConfiguracaoEntrega(confEntrega);

        // Configuração de Retirada
        ConfiguracaoRetirada confRetirada = new ConfiguracaoRetirada();
        confRetirada.setMatriz(matriz);
        confRetirada.setTempoEstimadoRetidara(30);  // Exemplo: 30 min padrão
        matriz.setConfiguracaoRetirada(confRetirada);

        // Configuração de Taxa Serviço
        ConfiguracaoTaxaServico confTaxaServico = new ConfiguracaoTaxaServico();
        confTaxaServico.setMatriz(matriz);
        confTaxaServico.setAplicar(false);
        confTaxaServico.setPercentual(BigDecimal.valueOf(0.0));
        confTaxaServico.setValorFixo(BigDecimal.valueOf(0.0));
        matriz.setConfiguracaoTaxaServico(confTaxaServico);
    }


    public MensagemDTO editarMatriz(Long id, MatrizDTO matrizDTO) {
        PermissaoUtil.validarAlgumaOuLancar("editarMatriz", "editarConfiguracoes");

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

        if (matriz.getPermissao() != null) {
            Permissao permissaoMatriz = matriz.getPermissao();
            List<Funcionario> funcionarios = funcionarioRepository.buscarFuncionarios(matriz.getId(), null, null);

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
                        if (!Boolean.TRUE.equals(permissaoMatriz.getMatrizPermissao())) p.setMatrizPermissao(false);
                    }
                }

                // 🔐 persistir alterações
                funcionarioRepository.saveAll(funcionarios);
            }
        }

        if (matriz.getConfiguracaoEntrega() != null) {
            matriz.getConfiguracaoEntrega().setMatriz(matriz);
        }
        if (matriz.getConfiguracaoRetirada() != null) {
            matriz.getConfiguracaoRetirada().setMatriz(matriz);
        }
        if (matriz.getConfiguracaoTaxaServico() != null) {
            matriz.getConfiguracaoTaxaServico().setMatriz(matriz);
        }
        if (matriz.getConfiguracaoImpressao() != null) {
            matriz.getConfiguracaoImpressao().setMatriz(matriz);
        }
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
        PermissaoUtil.validarOuLancar("editarMatriz");
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