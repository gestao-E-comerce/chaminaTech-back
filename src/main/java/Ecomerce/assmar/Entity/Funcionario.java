package Ecomerce.assmar.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id_funcionario")
@Getter
@Setter
public class Funcionario extends Usuario {
    private Double salario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_matriz", nullable = false)
    @JsonIgnoreProperties(value = {"funcionarios", "filhos", "matriz", "depositos", "estoques", "materias", "produtos", "vendas", "categorias", "clientes", "gestaoCaixas", "impressoras", "identificador"})
    private Matriz matriz;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"matriz", "vendas", "funcionario", "sangrias"})
    private List<Caixa> caixas;

    private String preferenciaImpressaoProdutoNovo;

    private String preferenciaImpressaoProdutoDeletado;
}