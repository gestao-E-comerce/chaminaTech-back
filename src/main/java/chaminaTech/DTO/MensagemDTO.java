package chaminaTech.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
@AllArgsConstructor
public class MensagemDTO {
    private String mensagem;
    private HttpStatus status;
}