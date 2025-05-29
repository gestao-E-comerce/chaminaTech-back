package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class DepositoDescartarDTO {
    private Long id;
    @JsonIgnoreProperties(value = {"matriz"})
    private MateriaDTO materia;

    @Column(nullable = false)
    private BigDecimal quantidade = BigDecimal.ZERO;

    private Timestamp dataDescartar;

    private String motivo;

    @JoinColumn(nullable = false)
    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private MatrizDTO matriz;
}