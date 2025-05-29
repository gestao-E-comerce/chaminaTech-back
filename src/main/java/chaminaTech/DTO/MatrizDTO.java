package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas"}, allowGetters = true)
public class MatrizDTO extends UsuarioDTO{
    @JsonIgnoreProperties("matriz")
    private List<FuncionarioDTO> funcionarios;

    @JsonIgnoreProperties("matriz")
    private List<MatrizDTO> filhos;

    @JsonIgnoreProperties("filhos")
    private MatrizDTO matriz;

    @JsonIgnoreProperties("matriz")
    private List<DepositoDTO> depositos;

    @JsonIgnoreProperties("matriz")
    private List<EstoqueDTO> estoques;

    @JsonIgnoreProperties("matriz")
    private List<MateriaDTO> materias;

    @JsonIgnoreProperties("matriz")
    private List<ProdutoDTO> produtos;

    @JsonIgnoreProperties("matriz")
    private List<VendaDTO> vendas;

    @JsonIgnoreProperties("matriz")
    private List<CategoriaDTO> categorias;

    @JsonIgnoreProperties("matriz")
    private List<ClienteDTO> clientes;

    @JsonIgnoreProperties("matriz")
    private List<GestaoCaixaDTO> gestaoCaixas;


    //Configuracoes

    @JsonIgnoreProperties(value = "matriz" ,allowSetters = true)
    private List<ImpressoraDTO> impressoras;

    private Boolean forcarRemocaoImpressora = false;

    @JsonIgnoreProperties(value = "matriz" ,allowSetters = true)
    private List<IdentificadorDTO> identificador;

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

    @JsonIgnoreProperties(value = "matriz" ,allowSetters = true)
    private List<TaxaEntregaKmDTO> taxasEntregaKm;

    private Integer tempoEstimadoRetidara;

    private String estado;

    private String cidade;

    private String bairro;

    private String cep;

    private String rua;

    private Integer numero;

    private Double latitude;

    private Double longitude;
}