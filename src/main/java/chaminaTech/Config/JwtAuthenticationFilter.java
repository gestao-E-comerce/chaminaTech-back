package chaminaTech.Config;

import chaminaTech.Entity.Usuario;
import chaminaTech.Repository.AppImpressaoTokenRepository;
import chaminaTech.Repository.ImpressaoRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtServiceGenerator jwtService; // Serviço para gerar e validar o JWT

    @Autowired
    private UserDetailsService userDetailsService; // Serviço para buscar o usuário no banco
    @Autowired
    private AppImpressaoTokenRepository appImpressaoTokenRepository;
    @Autowired
    private ImpressaoRepository impressaoRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String requestURI = request.getRequestURI();

        if (requestURI.contains("/api/login") || requestURI.contains("/api/ws")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"mensagem\":\"Token ausente ou inválido.\",\"status\":401}");
            return;
        }
        jwt = authHeader.substring(7);

        // 🔐 Lógica para impressão (app.jar)
        if (requestURI.contains("/api/app/impressao")) {
            Long matrizId;
            try {
                JWTClaimsSet claims = jwtService.decryptToken(jwt);
                matrizId = Long.parseLong(claims.getStringClaim("id")); // ID salvo como string no token

                // Inicializa variável para extrair o ID da matriz (pode vir da URL ou do banco)
                String matrizIdStr = request.getParameter("matrizId");

                // 🧠 Se a rota for deletar, o número final da URL é o ID da impressão, e não da matriz!
                if (requestURI.contains("/deletar/")) {
                    String[] partes = request.getRequestURI().split("/");
                    String idStr = partes[partes.length - 1];
                    if (idStr.matches("\\d+")) {
                        Long impressaoId = Long.parseLong(idStr);
                        var impressaoOpt = impressaoRepository.findById(impressaoId);
                        if (impressaoOpt.isPresent()) {
                            matrizIdStr = String.valueOf(impressaoOpt.get().getMatrizId());
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().write("{\"mensagem\":\"Impressão não encontrada.\",\"status\":404}");
                            return;
                        }
                    }
                }

                // 🧠 Caso contrário (ex: pendentes/{matrizId}), extrai do final da URL
                if (matrizIdStr == null || !matrizIdStr.matches("\\d+")) {
                    String[] partes = request.getRequestURI().split("/");
                    for (int i = partes.length - 1; i >= 0; i--) {
                        if (partes[i].matches("\\d+")) {
                            matrizIdStr = partes[i];
                            break;
                        }
                    }
                }

                if (matrizIdStr == null || !matrizIdStr.matches("\\d+")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"mensagem\":\"Parâmetro matrizId ausente ou inválido.\",\"status\":400}");
                    return;
                }

                Long matrizIdFromUrl = Long.parseLong(matrizIdStr);

                boolean valido = appImpressaoTokenRepository.existsByTokenAndMatrizIdAndAtivoTrue(jwt, matrizIdFromUrl);

                if (!valido || !matrizId.equals(matrizIdFromUrl)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"mensagem\":\"Token inválido para a matriz informada.\",\"status\":401}");
                    return;
                }

                // ✅ Marcar autenticação (impede erro 401 em métodos com @PreAuthorize, etc)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken("impressora", null, null);
                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request, response);
                return;

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"mensagem\":\"Token inválido ou corrompido.\",\"status\":401}");
                return;
            }
        }
        try {
            String userName = jwtService.extractUsername(jwt);

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

                if (userDetails instanceof Usuario usuario) {
                    // Verifica se o usuário está ativo
                    if (!usuario.getAtivo()) {
                        // Caso o usuário não esteja ativo, retorna 412 ou 422
                        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                        return;
                    }
                }

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    Date expirationDate = jwtService.extractExpiration(jwt);
                    long expirationThreshold = 10 * 60 * 1000; // 10 minutos

                    if (expirationDate != null && expirationDate.getTime() - System.currentTimeMillis() < expirationThreshold) {
                        String newJwtToken = jwtService.generateToken((Usuario) userDetails);
                        response.setHeader("Authorization", "Bearer " + newJwtToken);
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (TokenExpiradoException e) {
            // 🔇 NÃO deixa o erro subir nem printa no console
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"mensagem\":\"" + e.getMessage() + "\",\"status\":401}");
        }
    }
}