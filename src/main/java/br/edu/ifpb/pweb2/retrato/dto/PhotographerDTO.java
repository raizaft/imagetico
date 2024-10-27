package br.edu.ifpb.pweb2.retrato.dto;

import br.edu.ifpb.pweb2.retrato.model.Photographer;

public record PhotographerDTO(
        Integer id,
        String name,
        String email
) {

    public PhotographerDTO(Photographer photographer) {
        this(
            photographer.getId(),
            photographer.getName(),
            photographer.getEmail()
        );
    }
}
