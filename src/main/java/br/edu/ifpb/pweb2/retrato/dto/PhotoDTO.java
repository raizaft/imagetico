package br.edu.ifpb.pweb2.retrato.dto;

import br.edu.ifpb.pweb2.retrato.model.Photo;
import org.springframework.web.multipart.MultipartFile;

public record PhotoDTO(
        Integer id,
        MultipartFile photoFile,
        String photoPath,
        Integer photographerId,
        String photoDescription
) {

    public PhotoDTO(Photo photo) {
        this(
            photo.getId(),
            photo.getPhotoFile(),
            photo.getPhotoPath(),
            photo.getPhotographer().getId(),
            photo.getDescription()
        );
    }
}