package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SuprimentoDTO {
    private Long id;

    private Boolean ativo = true;

    private Double valor;

    private String motivo;

    private Timestamp dataSuprimento;

    private String nomeImpressora;

    @JsonIgnoreProperties(value = {"vendas","funcionario","sangrias","suprimentos"})
    private CaixaDTO caixa;

    @JsonIgnoreProperties(value = {"matriz","caixas"})
    private FuncionarioDTO funcionario;
}
