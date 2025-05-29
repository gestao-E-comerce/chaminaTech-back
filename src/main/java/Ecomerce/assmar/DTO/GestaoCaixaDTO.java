package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class GestaoCaixaDTO {
    private Long id;

    private Boolean ativo = true;

    private Integer cupom;

    private VendaDTO venda;

    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private MatrizDTO matriz;
}
