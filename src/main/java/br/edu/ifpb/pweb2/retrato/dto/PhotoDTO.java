package br.edu.ifpb.pweb2.retrato.dto;

import br.edu.ifpb.pweb2.retrato.model.Photo;

public record PhotoDTO(
        Integer id,
        byte[] imageData,
        String imageUrl,
        Integer photographerId
) {

    public PhotoDTO(Photo photo) {
        this(
            photo.getId(),
            photo.getImageData(),
            photo.getImageUrl(),
            photo.getPhotographer().getId()
        );
    }
}