package Ecomerce.assmar.Config;

import Ecomerce.assmar.Repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {
    @Autowired
    private LoginRepository loginRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> loginRepository.findByUsername(username)  // Carrega o usuário do banco de dados
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));  // Lança exceção se não encontrar o usuário
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());  // Define o serviço de detalhes do usuário
        authProvider.setPasswordEncoder(passwordEncoder());  // Define o encoder de senha
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();  // Cria o AuthenticationManager
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Cria o encoder de senha usando bcrypt
    }
}