package chaminaTech.DTO.ConfiguracaoDTO;

import chaminaTech.DTO.IdentificadorDTO;
import chaminaTech.DTO.ImpressoraDTO;
import chaminaTech.DTO.MatrizDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfiguracaoImpressaoDTO {
    private Long id;

    private MatrizDTO matriz;

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
    private Boolean imprimirConferenciaCaixa = true;
    private Boolean imprimirAberturaCaixa = true;
    private Boolean imprimirSangria = true;
    private Boolean imprimirSuprimento = true;
    private Boolean imprimirGorjeta = true;
    private Boolean mostarMotivoDeletarVenda = true;
    private Boolean mostarMotivoDeletarProduto = true;
    private Boolean imprimirComprovanteConsumo = true;

    private List<ImpressoraDTO> impressoras;

    private List<IdentificadorDTO> identificador;
}
