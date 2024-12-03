package br.edu.ifpb.pweb2.retrato.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tb_administrator")
public class Administrator extends User {

    public void suspenderUsuario(User usuario, String motivo) {

    }

    public void gerarRelatorioPDF(Photo foto) {

    }
}
