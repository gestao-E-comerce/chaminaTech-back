package Ecomerce.assmar.Config;

import Ecomerce.assmar.Entity.Usuario;
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

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;

        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/login") || requestURI.contains("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        jwt = authHeader.substring(7);
        userName = jwtService.extractUsername(jwt);

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
//                Date expirationDate = jwtService.extractExpiration(jwt);
//                long expirationThreshold = 5 * 60 * 1000; // 5 minutos

//                if (expirationDate != null && expirationDate.getTime() - System.currentTimeMillis() < expirationThreshold) {
                String newJwtToken = jwtService.generateToken((Usuario) userDetails);
                response.setHeader("Authorization", "Bearer " + newJwtToken);
//                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}