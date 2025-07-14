package chaminaTech.Config;

import chaminaTech.Entity.Usuario;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;


@Service
public class JwtServiceGenerator {

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(JwtParameters.SECRET_KEY);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String generateToken(Usuario userDetails) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .claim("nome", userDetails.getNome())
                    .claim("id", userDetails.getId().toString())
                    .claim("role", userDetails.getRole())
                    .claim("ativo", userDetails.getAtivo())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + (long) (3600000 * JwtParameters.HORAS_EXPIRACAO_TOKEN)))
                    .build();

            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A192CBC_HS384);
            EncryptedJWT jwt = new EncryptedJWT(header, claims);
            jwt.encrypt(new DirectEncrypter(secretKey));

            return jwt.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWE", e);
        }
    }

    public String generateImpressaoToken(Long id) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .claim("id", id.toString())
                    .issueTime(new Date()) // opcional
                    .build(); // sem expirationTime

            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A192CBC_HS384);
            EncryptedJWT jwt = new EncryptedJWT(header, claims);
            jwt.encrypt(new DirectEncrypter(secretKey));

            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWE para impressão", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            JWTClaimsSet claims = decryptToken(token);
            String username = claims.getSubject();
            Date expiration = claims.getExpirationTime();
            return username.equals(userDetails.getUsername()) && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Date extractExpiration(String token) {
        try {
            return decryptToken(token).getExpirationTime();
        } catch (Exception e) {
            return new Date(0);
        }
    }

    public String extractUsername(String token) {
        try {
            return decryptToken(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> resolver) {
        try {
            return resolver.apply(decryptToken(token));
        } catch (Exception e) {
            return null;
        }
    }

    public JWTClaimsSet decryptToken(String token) {
        try {
            EncryptedJWT jwt = EncryptedJWT.parse(token);
            jwt.decrypt(new DirectDecrypter(secretKey));
            return jwt.getJWTClaimsSet();
        } catch (Exception e) {
            throw new TokenExpiradoException("Token inválido ou corrompido.");
        }
    }
}