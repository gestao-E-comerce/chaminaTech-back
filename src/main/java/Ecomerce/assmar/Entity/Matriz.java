package Ecomerce.assmar.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id_matriz")
@Getter @Setter
@JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas"}, allowGetters = true)
public class Matriz extends Usuario{

    @OneToMany(mappedBy = "matriz",cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    @OrderBy("id")
    private List<Funcionario> funcionarios;

    @OneToMany(mappedBy = "matriz",cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Matriz> filhos;

    @ManyToOne
    @JoinColumn(name = "matriz_filho")
    @JsonIgnoreProperties("filhos")
    private Matriz matriz;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Deposito> depositos;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Estoque> estoques;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Materia> materias;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Produto> produtos;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Venda> vendas;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Categoria> categorias;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Cliente> clientes;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<GestaoCaixa> gestaoCaixas;

    //Configuracoes

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnoreProperties(value = "matriz" ,allowSetters = true)
    private List<Impressora> impressoras;

    private Boolean forcarRemocaoImpressora = false;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "matriz" ,allowSetters = true)
    private List<Identificador> identificador;

    private Boolean usarImpressora = true;

    private Boolean imprimirComprovanteRecebementoBalcao = true;

    private Boolean imprimirComprovanteRecebementoEntrega = true;

    private Boolean imprimirComprovanteRecebementoMesa = true;

    private Boolean imprimirComprovanteRecebementoRetirada = true;

    private Integer imprimirNotaFiscal = 0;

    private Integer imprimirCadastrar = 0;

    private Integer imprimirDeletar = 0;

    private Boolean imprimirComprovanteDeletarVenda = true;

    private Boolean imprimirComprovanteDeletarProduto = true;

    private Boolean imprimirConferenciaEntrega = true;

    private Boolean imprimirConferenciaRetirada = true;

    private Boolean mostarMotivoDeletarVenda = true;

    private Boolean mostarMotivoDeletarProduto = true;

    private Integer calcular = 0;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "matriz" ,allowSetters = true)
    private List<TaxaEntregaKm> taxasEntregaKm;

    private Integer tempoEstimadoRetidara;

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

    private Double latitude;

    private Double longitude;
}