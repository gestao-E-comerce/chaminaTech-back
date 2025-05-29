package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentificadorDTO {

    private Long id;

    @Column(nullable = false)
    private String impressoraNome;

    @Column(nullable = false)
    private String identificadorNome;

    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private MatrizDTO matriz;
}