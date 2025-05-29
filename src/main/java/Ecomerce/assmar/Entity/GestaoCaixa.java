package Ecomerce.assmar.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class GestaoCaixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;

    @Column(nullable = false)
    private Integer cupom;

    @OneToOne
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestao_caixa_matriz",nullable = false)
    @JsonIgnoreProperties(value = {"funcionarios","filhos","matriz","depositos","estoques","materias","produtos","vendas","categorias","clientes","gestaoCaixas","impressoras","identificador"})
    private Matriz matriz;
}
