package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(value = {"caixas"}, allowGetters = true)
public class FuncionarioDTO extends UsuarioDTO{

    private Double salario;

    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private MatrizDTO matriz;

    @JsonIgnoreProperties(value = {"matriz","vendas","funcionario","sangrias"})
    private List<CaixaDTO> caixas;

    private String preferenciaImpressaoProdutoNovo;

    private String preferenciaImpressaoProdutoDeletado;
}