package br.edu.ifpb.pweb2.retrato.dto;

import br.edu.ifpb.pweb2.retrato.model.Photographer;

public record PhotographerDTO(
        Integer id,
        String name,
        String email,
        String city,  //opcional
        String country,  //opcional
        byte[] profilePhoto  //opcional
) {

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
}
