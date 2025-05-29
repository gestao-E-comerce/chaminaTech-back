package Ecomerce.assmar.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter @Setter

public class DepositoDTO {
    private Long id;

    private Boolean ativo = true;

    private Boolean deletado = false;

    @JsonIgnoreProperties(value = {"matriz"})
    private MateriaDTO materia;

    private BigDecimal quantidade;

    private BigDecimal quantidadeVendido;

    private Double valorTotal = 0.0;

    private Timestamp dataCadastrar;

    private Timestamp dataDesativar;

    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas"})
    private MatrizDTO matriz;
}