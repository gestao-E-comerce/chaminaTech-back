package chaminaTech.DTO;

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

//    @JsonIgnoreProperties(value = {"cliente"}, allowSetters = true)
    private List<EnderecoDTO> enderecos;

//    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;
}