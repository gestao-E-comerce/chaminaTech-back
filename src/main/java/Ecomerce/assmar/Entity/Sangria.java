package Ecomerce.assmar.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Sangria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private Timestamp dataSangria;

    private String nomeImpressora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sangria_caixa", nullable = true)
    @JsonIgnoreProperties(value = {"vendas","funcionario","sangrias","suprimentos"})
    private Caixa caixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "sangria_funcionario", nullable = false)
    @JsonIgnoreProperties(value = {"matriz","caixas"})
    private Funcionario funcionario;
}
