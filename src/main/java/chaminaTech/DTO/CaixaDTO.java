package chaminaTech.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class CaixaDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    private Double valorAbertura;

    private Double saldoDinheiro;

    private Double saldoCredito;

    private Double saldoDebito;

    private Double saldoPix;

    private Double saldo;

    private Timestamp dataAbertura;

    private Timestamp dataFechamento;

    private String nomeImpressora;

    @JsonIgnoreProperties(value = {"matriz","caixas"})
    private FuncionarioDTO funcionario;

    @JsonIgnoreProperties(value = {"configuracaoEntrega", "configuracaoRetirada", "configuracaoImpressao", "configuracaoTaxaServico", }, allowSetters = true)
    private MatrizDTO matriz;

    @JsonIgnoreProperties(value = {"caixa"}, allowSetters = true)
    private List<VendaDTO> vendas;

    @JsonIgnoreProperties(value = {"caixa"}, allowSetters = true)
    private List<SangriaDTO> sangrias;

    @JsonIgnoreProperties(value = {"caixa"}, allowSetters = true)
    private List<SuprimentoDTO> suprimentos;
}
