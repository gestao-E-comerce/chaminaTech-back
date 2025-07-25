package chaminaTech.Controller;

import chaminaTech.DTO.LoginDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.UsuarioDTO;
import chaminaTech.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<Object> logar(@RequestBody LoginDTO loginDTO) {
        try {
            // Tenta realizar o login
            Object response = loginService.logar(loginDTO);

            if (response instanceof UsuarioDTO) {
                return ResponseEntity.ok(response); // Sucesso: retorna o UsuarioDTO
            } else {
                // Caso o login falhe por causa de dados inválidos, retorna um erro com 401
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MensagemDTO("Usuário ou senha inválidos", HttpStatus.UNAUTHORIZED));
            }

        } catch (IllegalStateException ex) {
            // Verifica se a falha foi devido ao usuário desativado
            if (ex.getMessage().equals("Usuário desativado")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MensagemDTO("Usuário desativado", HttpStatus.FORBIDDEN));
            }
            // Se for outro erro, retorna um erro genérico
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensagemDTO("Usuário ou senha inválidos", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            // Caso ocorra outro erro inesperado, retorna o status 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemDTO("Erro ao realizar login", HttpStatus.BAD_REQUEST));
        }
    }
    @PostMapping("/buscarUsuarioPermissao")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPermissao(@RequestBody LoginDTO loginDTO) {
        try {
            return ResponseEntity.ok(loginService.buscarUsuarioPermissao(loginDTO));
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/authenticate/{matrizId}")
    public ResponseEntity<UsuarioDTO> authenticate(@PathVariable Long matrizId, @RequestBody LoginDTO loginDTO) {
        try {
            UsuarioDTO usuarioDTO = loginService.buscarPorUsernameESenha(matrizId, loginDTO);
            return ResponseEntity.ok(usuarioDTO);
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("deslogar")
    public ResponseEntity<HttpStatus> logout() {

        SecurityContextHolder.clearContext();
        return new ResponseEntity<>(null, HttpStatus.OK);

    }
}