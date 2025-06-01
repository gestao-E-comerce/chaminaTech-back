// src/main/java/…/config/WebSocketConfig.java
package chaminaTech.Config;

import chaminaTech.Entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtServiceGenerator jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSocketConfig(JwtServiceGenerator jwtService,
                           UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/api/ws")                // endpoint SockJS / STOMP
                .setAllowedOriginPatterns("*")     // ajuste seu CORS aqui
                .withSockJS();                     // fallback XHR-streaming, polling…
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor
                        .getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String auth = accessor.getFirstNativeHeader("Authorization");
                    if (auth == null || !auth.startsWith("Bearer "))
                        throw new IllegalArgumentException("Sem token JWT");

                    String token = auth.substring(7);
                    String username = jwtService.extractUsername(token);
                    UserDetails user = userDetailsService.loadUserByUsername(username);

                    // mesma validação de "ativo" do JwtAuthenticationFilter
                    if (!(user instanceof Usuario u) || !u.getAtivo() || !jwtService.isTokenValid(token, user)) {
                        throw new IllegalArgumentException("Token inválido ou usuário inativo");
                    }

                    var authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    accessor.setUser(authToken);
                }
                return message;
            }
        });
    }
}
