package chaminaTech.Controller;

import chaminaTech.DTO.ClienteDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "*")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

//    @GetMapping("/{id}")
//    public ResponseEntity<ClienteDTO> findClienteById(@PathVariable Long id) {
//        ClienteDTO clienteDTO = clienteService.findClienteById(id);
//        if (clienteDTO != null) {
//            return ResponseEntity.ok(clienteDTO);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/lista/{matrizId}")
    public List<ClienteDTO> listarClientesPorMatrizId(@PathVariable Long matrizId) {
        return clienteService.listarClientesPorMatrizId(matrizId);
    }

    @GetMapping("/porNome")
    public List<ClienteDTO> listarClientesPorNome(
            @RequestParam("matrizId") Long matrizId,
            @RequestParam("termoPesquisa") String termoPesquisa) {
        return clienteService.listarClientesPorNome(matrizId, termoPesquisa);
    }

    @GetMapping("/porCpf")
    public List<ClienteDTO> listarClientesPorCpf(
            @RequestParam("matrizId") Long matrizId,
            @RequestParam("termoPesquisa") String termoPesquisa) {
        return clienteService.listarClientesPorCpf(matrizId, termoPesquisa);
    }

    @GetMapping("/porCelular")
    public List<ClienteDTO> listarClientesPorCelular(
            @RequestParam("matrizId") Long matrizId,
            @RequestParam("termoPesquisa") String termoPesquisa) {
        return clienteService.listarClientesPorCelular(matrizId, termoPesquisa);
    }

    @GetMapping("/porCep")
    public List<ClienteDTO> listarClientesPorCep(
            @RequestParam("matrizId") Long matrizId,
            @RequestParam("termoPesquisa") String termoPesquisa) {
        return clienteService.listarClientesPorCep(matrizId, termoPesquisa);
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> cadastrarCliente(@RequestBody ClienteDTO clienteDTO) {
        try{
            return ResponseEntity.ok(clienteService.cadastrarCliente(clienteDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensagemDTO> editarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        try {
            return ResponseEntity.ok(clienteService.editarCliente(id, clienteDTO));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemDTO> deletarCliente(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clienteService.deletarCliente(id));
        }catch(Exception e){
            MensagemDTO mensagem = new MensagemDTO(e.getMessage(),HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(mensagem);
        }
    }
}