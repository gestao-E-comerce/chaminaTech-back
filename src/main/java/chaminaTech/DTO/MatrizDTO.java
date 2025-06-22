package chaminaTech.DTO;

import chaminaTech.DTO.ConfiguracaoDTO.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MatrizDTO extends UsuarioDTO {
    private MatrizDTO matriz;

    private String estado;

    private String cidade;

    private String bairro;

    private String cep;

    private String rua;

    private Integer numero;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private int limiteFuncionarios;

    private ConfiguracaoEntregaDTO configuracaoEntrega;

    private ConfiguracaoRetiradaDTO configuracaoRetirada;

    private ConfiguracaoImpressaoDTO configuracaoImpressao;

    private ConfiguracaoTaxaServicoDTO configuracaoTaxaServicio;
}