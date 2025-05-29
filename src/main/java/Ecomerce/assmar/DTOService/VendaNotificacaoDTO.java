package Ecomerce.assmar.DTOService;

import Ecomerce.assmar.DTO.VendaDTO;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class VendaNotificacaoDTO {
    private String tipo; // "mesa", "entrega", "retirada", "balcao"
    private String acao;     // "cadastrar", "editar", "deletar", "liberar", etc
    private VendaDTO venda;
}
