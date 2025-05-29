package Ecomerce.assmar.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id_admin")
@Getter
@Setter
public class Admin extends Usuario{

    private String chaveApiCoordenades;

    @OneToMany(mappedBy = "matriz",cascade = CascadeType.ALL)
    @JsonIgnoreProperties("matriz")
    private List<Matriz> matrizs;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("admin")
    private List<AdminFuncionario> adminFuncionarios;
}