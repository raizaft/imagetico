package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.dto.PhotoDTO;
import br.edu.ifpb.pweb2.retrato.dto.PhotographerDTO;
import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.repository.PhotoRepository;
import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotographerRepository photographerRepository;

    public PhotoDTO publish(PhotoDTO photoDTO) {
        Photographer photographer = photographerRepository.findById(photoDTO.photographerId())
                .orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado."));

        Photo photo = new Photo();
        photo.setImageData(photoDTO.imageData());
        photo.setPhotographer(photographer);

        Photo savedPhoto = photoRepository.save(photo);
        return new PhotoDTO(savedPhoto);
    }
}
