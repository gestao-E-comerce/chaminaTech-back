package Ecomerce.assmar.Entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "id_subadmin")
@Getter
@Setter
public class AdminFuncionario extends Usuario{
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonIgnoreProperties("subAdmins")
    private Admin admin;
}
