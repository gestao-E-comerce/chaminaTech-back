package Ecomerce.assmar.DTOService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class VendaSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notificarAtualizacao(Long matrizId, VendaNotificacaoDTO notificacao) {
        // Envia a notificação para todos conectados no canal da matriz
        messagingTemplate.convertAndSend("/topic/venda/" + matrizId, notificacao);
    }
}
