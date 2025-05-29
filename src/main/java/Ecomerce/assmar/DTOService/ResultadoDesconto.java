package Ecomerce.assmar.DTOService;

import Ecomerce.assmar.Entity.Deposito;
import Ecomerce.assmar.Entity.Estoque;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ResultadoDesconto {
    private List<Estoque> estoques;
    private List<Deposito> depositos;

    public ResultadoDesconto() {
        this.estoques = new ArrayList<>();
        this.depositos = new ArrayList<>();
    }

    public List<Estoque> getEstoques() { return estoques; }
    public List<Deposito> getDepositos() { return depositos; }

    public void addEstoque(Estoque e) { estoques.add(e); }
    public void addDeposito(Deposito d) { depositos.add(d); }

    public void addAllEstoque(List<Estoque> e) { estoques.addAll(e); }
    public void addAllDeposito(List<Deposito> d) { depositos.addAll(d); }
}