package chaminaTech.Entity.Configuracao;

import chaminaTech.Entity.Identificador;
import chaminaTech.Entity.Impressora;
import chaminaTech.Entity.Matriz;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ConfiguracaoImpressao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matriz_id", nullable = false)
    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico"})
    private Matriz matriz;

    @Column(nullable = false)
    private Boolean usarImpressora = true;

    @Column(nullable = false)
    private Boolean imprimirComprovanteRecebementoBalcao = true;
    @Column(nullable = false)
    private Boolean imprimirComprovanteRecebementoEntrega = true;
    @Column(nullable = false)
    private Boolean imprimirComprovanteRecebementoMesa = true;
    @Column(nullable = false)
    private Boolean imprimirComprovanteRecebementoRetirada = true;
    @Column(nullable = false)
    private Integer imprimirNotaFiscal = 0;
    @Column(nullable = false)
    private Integer imprimirCadastrar = 0;
    @Column(nullable = false)
    private Integer imprimirDeletar = 0;
    @Column(nullable = false)
    private Boolean imprimirComprovanteDeletarVenda = true;
    @Column(nullable = false)
    private Boolean imprimirComprovanteDeletarProduto = true;
    @Column(nullable = false)
    private Boolean imprimirConferenciaEntrega = true;
    @Column(nullable = false)
    private Boolean imprimirConferenciaRetirada = true;
    @Column(nullable = false)
    private Boolean imprimirConferenciaCaixa = true;
    @Column(nullable = false)
    private Boolean imprimirAberturaCaixa = true;
    @Column(nullable = false)
    private Boolean imprimirSangria = true;
    @Column(nullable = false)
    private Boolean imprimirSuprimento = true;
    @Column(nullable = false)
    private Boolean imprimirGorjeta = true;
    @Column(nullable = false)
    private Boolean mostarMotivoDeletarVenda = true;
    @Column(nullable = false)
    private Boolean mostarMotivoDeletarProduto = true;
    @Column(nullable = false)
    private Boolean imprimirComprovanteConsumo = true;

    @OneToMany(mappedBy = "configuracaoImpressao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("configuracaoImpressao")
    private List<Impressora> impressoras;

    @OneToMany(mappedBy = "configuracaoImpressao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("configuracaoImpressao")
    private List<Identificador> identificador;
}
