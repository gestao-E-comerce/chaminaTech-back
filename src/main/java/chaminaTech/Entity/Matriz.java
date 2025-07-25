package chaminaTech.Entity;

import chaminaTech.Entity.Configuracao.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name = "id_matriz")
@Getter @Setter
public class Matriz extends Usuario{
    @ManyToOne
    @JoinColumn(name = "matriz_filho")
    @JsonIgnoreProperties("filhos")
    private Matriz matriz;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String rua;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    @Column(nullable = false)
    private int limiteFuncionarios;

    @Column(nullable = false)
    private String fantasia;

    @Column(nullable = false)
    private String inscricaoEstadual;

    @Column(nullable = false)
    private Integer regimeTributario;

    @Column(nullable = false)
    private String codMunicipio;

    @OneToOne(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"matriz"})
    private ConfiguracaoEntrega configuracaoEntrega;

    @OneToOne(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"matriz"})
    private ConfiguracaoRetirada configuracaoRetirada;

    @OneToOne(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"matriz"})
    private ConfiguracaoImpressao configuracaoImpressao;

    @OneToOne(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"matriz"})
    private ConfiguracaoTaxaServico configuracaoTaxaServico;
}