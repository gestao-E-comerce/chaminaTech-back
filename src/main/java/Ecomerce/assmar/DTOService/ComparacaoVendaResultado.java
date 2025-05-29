package Ecomerce.assmar.DTOService;

import Ecomerce.assmar.Entity.ProdutoVenda;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ComparacaoVendaResultado {
    private List<ProdutoVenda> removidos = new ArrayList<>();
    private List<ProdutoVenda> adicionados = new ArrayList<>();

    public void addRemovido(ProdutoVenda pv) {
        removidos.add(pv);
    }

    public void addAdicionado(ProdutoVenda pv) {
        adicionados.add(pv);
    }

    public List<ProdutoVenda> getRemovidos() {
        return removidos;
    }

    public List<ProdutoVenda> getAdicionados() {
        return adicionados;
    }
}