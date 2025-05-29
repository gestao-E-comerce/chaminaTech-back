package chaminaTech.Config;

import chaminaTech.Entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;


@Service
public class JwtServiceGenerator {

    public String generateToken(Usuario userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("nome", userDetails.getNome());
        extraClaims.put("id", userDetails.getId().toString());
        extraClaims.put("role", userDetails.getRole());
        extraClaims.put("ativo", userDetails.getAtivo());

        long expirationTime = new Date().getTime() + (long)(3600000 * JwtParameters.HORAS_EXPIRACAO_TOKEN);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationTime))  // Define a expiração do token
                .signWith(getSigningKey(), JwtParameters.ALGORITMO_ASSINATURA)  // Assina o token com a chave secreta
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // Extrai o nome de usuário do token
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);  // Verifica se o token é válido
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // Verifica se o token expirou
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // Extrai a data de expiração do token
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JwtParameters.SECRET_KEY);  // Decodifica a chave secreta
        return Keys.hmacShaKeyFor(keyBytes);  // Cria a chave secreta para assinar o token
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);  // Extrai o nome de usuário do token
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Extrai todos os claims do token
        return claimsResolver.apply(claims);  // Aplica a função para extrair o valor desejado
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("Token expirado. Faça login novamente.");
        } catch (io.jsonwebtoken.JwtException e) {
            throw new RuntimeException("Token inválido.");
        }
    }
}