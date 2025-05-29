package chaminaTech.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Impressao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long matrizId;

    @Column(nullable = false)
    private String nomeImpressora;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] conteudoImpressao;

    @Column(nullable = false)
    private Boolean status = true;
}
