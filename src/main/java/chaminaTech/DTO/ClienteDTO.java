package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClienteDTO {
    private Long id;

    private Boolean ativo = true;

    private String nome;

    private String cpf;

    private String celular;

    @JsonIgnoreProperties(value = {"cliente"}, allowSetters = true)
    private List<EnderecoDTO> enderecos;

    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private MatrizDTO matriz;
}