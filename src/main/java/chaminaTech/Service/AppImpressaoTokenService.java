package chaminaTech.Service;

import chaminaTech.Config.JwtServiceGenerator;
import chaminaTech.Entity.AppImpressaoToken;
import chaminaTech.Repository.AppImpressaoTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppImpressaoTokenService {

    @Autowired
    private AppImpressaoTokenRepository appImpressaoTokenRepository;
    @Autowired
    private JwtServiceGenerator jwtService;
    public String gerarTokenMemoria(Long matrizId) {
        return jwtService.generateImpressaoToken(matrizId);
    }

    public void criarRegistroToken(String token, Long matrizId) {
        AppImpressaoToken novoToken = new AppImpressaoToken();
        novoToken.setMatrizId(matrizId);
        novoToken.setToken(token);
        novoToken.setDataCriacao(LocalDateTime.now());
        novoToken.setAtivo(true);
        appImpressaoTokenRepository.save(novoToken);
    }
}
