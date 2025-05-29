package chaminaTech.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long matrizId;

    @Column(nullable = false)
    private String operacao;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime dataHora;
}
