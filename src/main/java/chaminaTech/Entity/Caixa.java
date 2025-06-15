package chaminaTech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
public class Caixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    @Column(nullable = false)
    private Double valorAbertura;

    private Double saldoDinheiro;

    private Double saldoCredito;

    private Double saldoDebito;

    private Double saldoPix;

    @Column(nullable = false)
    private Timestamp dataAbertura;

    private Timestamp dataFechamento;

    private Double saldo;

    private String nomeImpressora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_funcionario", nullable = false)
    @JsonIgnoreProperties(value = {"matriz", "caixas"})
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_matriz", nullable = false)
    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico",}, allowSetters = true)
    private Matriz matriz;

    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"caixa"}, allowSetters = true)
    private List<Venda> vendas;

    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"caixa"}, allowSetters = true)
    @OrderBy("id")
    private List<Sangria> sangrias;

    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"caixa"}, allowSetters = true)
    @OrderBy("id")
    private List<Suprimento> suprimentos;
}
