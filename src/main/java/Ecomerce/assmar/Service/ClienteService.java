package Ecomerce.assmar.Service;

import Ecomerce.assmar.DTO.*;
import Ecomerce.assmar.DTOService.*;
import Ecomerce.assmar.Entity.Cliente;
import Ecomerce.assmar.Repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<ClienteDTO> listarClientesPorMatrizId(Long matrizId) {

        return clienteRepository.findByMatrizIdAndAtivoOrderByIdAsc(matrizId, true).stream().map(entityToDTO::clienteToDTO).collect(Collectors.toList());
    }

    public List<ClienteDTO> listarClientesPorNome(Long matrizId, String termoPesquisa) {

        return clienteRepository.buscarClientesPorNome(matrizId, true, termoPesquisa).stream().map(entityToDTO::clienteToDTO).collect(Collectors.toList());
    }

    public List<ClienteDTO> listarClientesPorCpf(Long matrizId, String termoPesquisa) {

        return clienteRepository.buscarClientesPorCpf(matrizId, true, termoPesquisa).stream().map(entityToDTO::clienteToDTO).collect(Collectors.toList());
    }

    public List<ClienteDTO> listarClientesPorCelular(Long matrizId, String termoPesquisa) {

        return clienteRepository.buscarClientesPorCelular(matrizId, true, termoPesquisa).stream().map(entityToDTO::clienteToDTO).collect(Collectors.toList());
    }

    public List<ClienteDTO> listarClientesPorCep(Long matrizId, String termoPesquisa) {

        return clienteRepository.buscarClientesPorCep(matrizId, true, termoPesquisa).stream().map(entityToDTO::clienteToDTO).collect(Collectors.toList());
    }

    public MensagemDTO cadastrarCliente(ClienteDTO clienteDTO) {
        PermissaoUtil.validarOuLancar("cadastrarCliente");

        Cliente cliente = dtoToEntity.DTOToCliente(clienteDTO);
        if (clienteRepository.existsByNomeAndMatrizIdAndAtivo(clienteDTO.getMatriz().getId(), cliente.getNome(), true)) {
            throw new IllegalStateException("Já existe um cliente com esse nome!");
        }

        if (cliente.getEnderecos() != null) for (int i = 0; i < cliente.getEnderecos().size(); i++) {
            cliente.getEnderecos().get(i).setCliente(cliente);
        }

        clienteRepository.save(cliente);
        auditoriaService.salvarAuditoria(
                "CADASTRAR",
                "CLIENTE",
                "Cadastrou o cliente: " + cliente.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                cliente.getMatriz().getId()
        );
        return new MensagemDTO("Cliente cadastrado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO editarCliente(Long id, ClienteDTO clienteDTO) {
        PermissaoUtil.validarOuLancar("editarCliente");

        clienteDTO.setId(id);
        Cliente clienteAtual = clienteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado!"));

        Cliente clienteEditado = dtoToEntity.DTOToCliente(clienteDTO);

        // 🔍 Verificar endereços removidos que estão em uso em venda ativa
        List<Long> idsEditados = clienteEditado.getEnderecos().stream().filter(e -> e.getId() != null).map(e -> e.getId()).toList();

        List<Long> idsRemovidos = clienteAtual.getEnderecos().stream().filter(e -> e.getId() != null && !idsEditados.contains(e.getId())).map(e -> e.getId()).toList();

        for (Long enderecoId : idsRemovidos) {
            boolean emUso = clienteRepository.existsByEnderecoEmVendaAtiva(enderecoId, clienteDTO.getMatriz().getId());
            if (emUso) {
                throw new IllegalStateException("Endereço em uso por uma venda ativa. Remoção não permitida.");
            }
        }

        if (clienteRepository.existsByNomeAndMatrizIdAndAtivoAndNotId(clienteDTO.getMatriz().getId(), clienteEditado.getNome(), true, clienteEditado.getId())) {
            throw new IllegalStateException("Já existe um cliente com esse nome!");
        }

        if (clienteEditado.getEnderecos() != null) {
            for (int i = 0; i < clienteEditado.getEnderecos().size(); i++) {
                clienteEditado.getEnderecos().get(i).setCliente(clienteEditado);
            }
        }
        clienteRepository.save(clienteEditado);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CLIENTE",
                "Editou o cliente: " + clienteEditado.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                clienteEditado.getMatriz().getId()
        );
        return new MensagemDTO("Cliente atualizado com sucesso!", HttpStatus.CREATED);
    }

    public MensagemDTO deletarCliente(Long id) {
        PermissaoUtil.validarOuLancar("deletarCliente");
        Cliente clienteBanco = clienteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não existe!"));

        boolean possuiVendaAtiva = clienteRepository.existsByClienteEmVendaAtiva(clienteBanco.getId(), clienteBanco.getMatriz().getId());

        if (possuiVendaAtiva) {
            throw new IllegalStateException("Não é possível deletar este cliente pois ele está vinculado a uma venda ativa.");
        }

        desativarCliente(clienteBanco);
        auditoriaService.salvarAuditoria(
                "DELETAR",
                "CLIENTE",
                "Deletou o cliente: " + clienteBanco.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                clienteBanco.getMatriz().getId()
        );
        return new MensagemDTO("Cliente deletada com sucesso!", HttpStatus.CREATED);
    }

    private void desativarCliente(Cliente cliente) {
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }
}