package chaminaTech.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter; // Filtro que verifica o token JWT

    @Autowired
    private AuthenticationProvider authenticationProvider; // Fornece o provedor de autenticação

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/login", "/api/app/impressao/**", "/api/ws/**", "/sockjs/**").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Configuração específica para o /api/impressao/**
        CorsConfiguration impressaoConfig = new CorsConfiguration();
        impressaoConfig.setAllowCredentials(true);
        impressaoConfig.addAllowedOriginPattern("*");  // Permite qualquer origem (IP/porta) para impressão
        impressaoConfig.setAllowedMethods(Arrays.asList("GET", "DELETE"));
        impressaoConfig.setAllowedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        source.registerCorsConfiguration("/api/app/impressao/**", impressaoConfig);

        // Configuração padrão para os outros endpoints
        CorsConfiguration defaultConfig = new CorsConfiguration();
        defaultConfig.setAllowCredentials(true);
//        defaultConfig.setAllowedOrigins(List.of("http://localhost:4200", "https://chaminatech.com",
//                "https://www.chaminatech.com", "http://192.168.0.104:4200"));
        defaultConfig.addAllowedOriginPattern("*");
        defaultConfig.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(),
                HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.PATCH.name(), HttpMethod.OPTIONS.name()));
        defaultConfig.setAllowedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT, "chaveUnico", "Authorization"));
        defaultConfig.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
        defaultConfig.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", defaultConfig);
        return source;
    }
}