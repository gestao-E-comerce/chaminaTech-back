package chaminaTech.Service;

import chaminaTech.DTO.MensagemDTO;
import chaminaTech.DTO.TaxaEntregaKmDTO;
import chaminaTech.DTOService.DTOToEntity;
import chaminaTech.DTOService.EntityToDTO;
import chaminaTech.DTOService.PermissaoUtil;
import chaminaTech.Entity.Matriz;
import chaminaTech.Entity.TaxaEntregaKm;
import chaminaTech.Repository.MatrizRepository;
import chaminaTech.Repository.TaxaEntregaKmRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaxaEntregaKmService {
    @Autowired
    private EntityToDTO entityToDTO;
    @Autowired
    private DTOToEntity dtoToEntity;
    @Autowired
    private TaxaEntregaKmRepository taxaEntregaKmRepository;
    @Autowired
    private MatrizRepository matrizRepository;
    @Autowired
    private AuditoriaService auditoriaService;


    public List<TaxaEntregaKmDTO> buscarTaxasKmPorMatriz(Long matrizId) {
        return taxaEntregaKmRepository.findByMatrizIdOrderByKmAsc(matrizId).stream()
                .map(entityToDTO::taxaEntregaKmToDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public MensagemDTO salvarlista(List<TaxaEntregaKmDTO> listaDTO, Long matrizId) {
        PermissaoUtil.validarOuLancar("editarConfiguracoes");
        Matriz matriz = matrizRepository.findById(matrizId)
                .orElseThrow(() -> new RuntimeException("Matriz não encontrada"));

        Set<Integer> kmsVistos = new HashSet<>();
        List<TaxaEntregaKm> listaFiltrada = new ArrayList<>();

        for (TaxaEntregaKmDTO dto : listaDTO) {
            Integer km = dto.getKm();
            if (km == null || kmsVistos.contains(km)) continue;
            kmsVistos.add(km);

            TaxaEntregaKm nova = dtoToEntity.DTOToTaxaEntregaKm(matriz, dto);
            nova.setMatriz(matriz);
            listaFiltrada.add(nova);
        }

        matriz.getTaxasEntregaKm().clear();

        matriz.getTaxasEntregaKm().addAll(listaFiltrada);

        matrizRepository.save(matriz);
        auditoriaService.salvarAuditoria(
                "EDITAR",
                "CONFIGURACAO",
                "Editou as taxas de entrega da matriz: " + matriz.getNome(),
                PermissaoUtil.getUsuarioLogado().getNome(),
                matriz.getId()
        );
        return new MensagemDTO("Taxas atualizadas com sucesso", HttpStatus.OK);
    }

}
