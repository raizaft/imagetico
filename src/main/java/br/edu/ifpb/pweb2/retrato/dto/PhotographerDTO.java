package br.edu.ifpb.pweb2.retrato.dto;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;


public record PhotographerDTO(
        Integer id,

        @NotEmpty(message = "O nome é obrigatório.")
        String name,

        @NotEmpty(message = "O e-mail é obrigatório.")
        @Email(message = "Por favor, forneça um e-mail válido.")
        String email,

        String city,

        String country,

        byte[] profilePhoto
) {

    public PhotographerDTO() {
        this(null, null, null, null, null, null);
    }

    public PhotographerDTO(Photographer photographer) {
        this(
                photographer.getId(),
                photographer.getName(),
                photographer.getEmail(),
                photographer.getCity(),
                photographer.getCountry(),
                photographer.getProfilePhoto()
        );
    }


    public boolean isValid() {
        return name != null && !name.isBlank() &&
                email != null && !email.isBlank() && email.contains("@");
    }
}
