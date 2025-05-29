package chaminaTech.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferenciaDTO {
    private VendaDTO vendaOriginal;
    private VendaDTO vendaDestino;
}
