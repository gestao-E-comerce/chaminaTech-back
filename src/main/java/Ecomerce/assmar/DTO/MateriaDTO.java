package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MateriaDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private String nome;

    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private MatrizDTO matriz;

    private Double quantidadeDisponivel;

    private Double quantidadeDescartada;
}