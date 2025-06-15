package chaminaTech.Service;

import chaminaTech.DTO.GestaoCaixaDTO;
import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.GestaoCaixa;
import chaminaTech.Entity.ProdutoVenda;
import chaminaTech.Repository.GestaoCaixaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GestaoCaixaService {
    @Autowired
    private GestaoCaixaRepository gestaoCaixaRepository;
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private AuditoriaService auditoriaService;

    public GestaoCaixaDTO findByCupomAndAtivoAndEntregaAndMatrizId(Integer numeroCupom, Long matrizId) {
        Optional<GestaoCaixa> gestaoCaixaOptional = gestaoCaixaRepository.findByCupomAndAtivoAndEntregaAndMatrizId(numeroCupom, matrizId);
        return gestaoCaixaOptional.map(gestaoCaixa -> {
            gestaoCaixa.getVenda().setProdutoVendas(
                    gestaoCaixa.getVenda().getProdutoVendas().stream()
                            .filter(ProdutoVenda::getAtivo)
                            .collect(Collectors.toList())
            );
            return entityToDTO.gestaoCaixaToDTO(gestaoCaixa);
        }).orElse(null);
    }

    public GestaoCaixaDTO findByCupomAndAtivoAndRetiradaAndMatrizId(Integer numeroCupom, Long matrizId) {
        Optional<GestaoCaixa> gestaoCaixaOptional = gestaoCaixaRepository.findByCupomAndAtivoAndRetiradaAndMatrizId(numeroCupom, matrizId);
        return gestaoCaixaOptional.map(gestaoCaixa -> {
            gestaoCaixa.getVenda().setProdutoVendas(
                    gestaoCaixa.getVenda().getProdutoVendas().stream()
                            .filter(ProdutoVenda::getAtivo)
                            .collect(Collectors.toList())
            );
            return entityToDTO.gestaoCaixaToDTO(gestaoCaixa);
        }).orElse(null);
    }

    public List<Map<String, Object>> buscarCuponsEntregaByMatrizId(Long matrizId) {
        List<Object[]> resultados = gestaoCaixaRepository.buscarCuponsEntregaByMatrizId(matrizId);

        List<Map<String, Object>> cuponsEntraga = new ArrayList<>();
        for (Object[] resultado : resultados) {
            Map<String, Object> cupomInfo = new HashMap<>();
            cupomInfo.put("numero", resultado[0]);
            cupomInfo.put("statusEmAberto", resultado[1]);
            cupomInfo.put("statusEmPagamento", resultado[2]);
            cupomInfo.put("cliente", resultado[3]);
            cupomInfo.put("id", resultado[4]);
            cupomInfo.put("dataVenda", resultado[5]);
            cupomInfo.put("tempoPrevisto", resultado[6]);
            cuponsEntraga.add(cupomInfo);
        }

        return cuponsEntraga;
    }

    public List<Map<String, Object>> buscarCuponsRetiradaByMatrizId(Long matrizId) {
        List<Object[]> resultados = gestaoCaixaRepository.buscarCuponsRetiradaByMatrizId(matrizId);

        List<Map<String, Object>> cuponsRetirada = new ArrayList<>();
        for (Object[] resultado : resultados) {
            Map<String, Object> cupomInfo = new HashMap<>();
            cupomInfo.put("numero", resultado[0]);
            cupomInfo.put("statusEmAberto", resultado[1]);
            cupomInfo.put("statusEmPagamento", resultado[2]);
            cupomInfo.put("cliente", resultado[3]);
            cupomInfo.put("id", resultado[4]);
            cupomInfo.put("dataVenda", resultado[5]);
            cupomInfo.put("tempoPrevisto", resultado[6]);
            cuponsRetirada.add(cupomInfo);
        }

        return cuponsRetirada;
    }

    public List<GestaoCaixaDTO> buscarHistoricoComFiltro(Long matrizId, String tipo, String cupom) {
        PermissaoUtil.validarOuLancar("historicoVenda");
        List<GestaoCaixa> lista = gestaoCaixaRepository.buscarHistoricoComFiltro(matrizId, tipo, cupom);
        return lista.stream().map(entityToDTO::gestaoCaixaToDTO).collect(Collectors.toList());
    }

    public MensagemDTO zerarCupom(Long matrizId) {
        PermissaoUtil.validarOuLancar("editarConfiguracoes");
        boolean temCuponsAtivos = gestaoCaixaRepository.existsByMatrizIdAndAtivoTrue(matrizId);

        if (temCuponsAtivos) {
            throw new RuntimeException("Existem Vendas ativas. Finalize todos antes de zerar.");
        }

        // Verifica se já existe um cupom com ativo = null (já zerado)
        boolean jaFoiZerado = gestaoCaixaRepository.existsByMatrizIdAndAtivoIsNull(matrizId);

        if (jaFoiZerado) {
            throw new RuntimeException("A numeração já foi zerada. Aguarde a próxima venda para reiniciar.");
        }

        Optional<GestaoCaixa> ultimoCupomOpt = gestaoCaixaRepository.findTopByMatrizIdOrderByIdDesc(matrizId);

        if (ultimoCupomOpt.isEmpty()) {
            return new MensagemDTO("Numeração de cupons zerada com sucesso!", HttpStatus.OK);
        }
        // Busca direto o último registro completo com base no maior cupom da matriz

        GestaoCaixa ultimo = ultimoCupomOpt.get();

        // Cria novo cupom com campos nulos
        GestaoCaixa novo = new GestaoCaixa();
        novo.setMatriz(ultimo.getMatriz());
        novo.setVenda(null);
        novo.setCupom(0);
        novo.setAtivo(null);

        gestaoCaixaRepository.save(novo);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CONFIGURACAO",
                "Zerou os cupons",
                PermissaoUtil.getUsuarioLogado().getNome(),
                matrizId
        );
        return new MensagemDTO("Numeração de cupons zerada com sucesso!", HttpStatus.OK);
    }

}