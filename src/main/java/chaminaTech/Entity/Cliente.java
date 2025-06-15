package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;

    @Column(nullable = false)
    private String nome;

    private String cpf;

    @Column(nullable = false)
    private String celular;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "cliente" }, allowSetters = true)
    private List<Endereco> enderecos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_matriz", nullable = false)
    @JsonIgnoreProperties(value = { "configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao",
            "configuracaoTaxaServico", }, allowSetters = true)
    private Matriz matriz;
}