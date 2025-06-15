package chaminaTech.Controller;

import chaminaTech.Entity.Auditoria;
import chaminaTech.Service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    public Page<Auditoria> listarAuditorias(
            @RequestParam Long matrizId,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String operacao,
            @RequestParam(required = false) String tipo,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataHora").descending());
        return auditoriaService.listarAuditorias(
                matrizId, usuario, operacao, tipo,
                dataInicio, dataFim,
                pageable
        );
    }
}