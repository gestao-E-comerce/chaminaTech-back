package Ecomerce.assmar.Config;

import io.jsonwebtoken.SignatureAlgorithm;

public class JwtParameters {
    //Parâmetros para geração do token
    public static final String SECRET_KEY = "6A576D5A7134743777217A25432A462D4A614E645267556B5870327235753878";
    public static final SignatureAlgorithm ALGORITMO_ASSINATURA = SignatureAlgorithm.HS256;
    // Definindo a expiração do token para 2 minutos (2 / 60 = 0.03333 horas)
    public static final double HORAS_EXPIRACAO_TOKEN = 30.0 / 60.0;
}