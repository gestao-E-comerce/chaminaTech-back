package chaminaTech.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AppImpressaoToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long matrizId;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;
}
