package chaminaTech.DTO;

import lombok.Getter;

@Getter
public class ParcialDTO {
    private VendaDTO vendaOriginal;
    private VendaDTO vendaParcial;
    private String chaveUnico;
}
