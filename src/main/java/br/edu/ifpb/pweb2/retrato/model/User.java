package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;

@Data
@MappedSuperclass
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @NotEmpty(message = "O nome é obrigatório.")
    private String name;
    @NotEmpty(message = "O email é obrigatório.")
    @Email(message = "Por favor, forneça um email válido.")
    private String email;

    private LocalDate dataCriacao = LocalDate.now();

    private String city;
    private String country;
    private String profilePhoto;
}
