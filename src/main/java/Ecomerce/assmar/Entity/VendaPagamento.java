package Ecomerce.assmar.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class VendaPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double dinheiro;

    private Double debito;

    private Double credito;

    private Double pix;

    @OneToOne
    @JoinColumn(name = "venda_id", nullable = false, unique = true)
    @JsonIgnoreProperties("vendaPagamento")
    private Venda venda;
}