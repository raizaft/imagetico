package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.dto.PhotographerDTO;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotographerService {
    @Autowired
    private PhotographerRepository repository;

    public PhotographerDTO register(PhotographerDTO photographerDTO) {
        if (repository.existsByEmail(photographerDTO.email())) {
            throw new IllegalArgumentException("Já existe uma conta com esse e-mail.");
        }

        Photographer photographer = new Photographer();
        photographer.setName(photographerDTO.name());
        photographer.setEmail(photographerDTO.email());
        photographer.setProfilePhoto(photographerDTO.profilePhoto());
        photographer.setCity(photographerDTO.city());
        photographer.setCountry(photographerDTO.country());

        // Retornar um DTO ao invés do objeto Photographer
        Photographer savedPhotographer = repository.save(photographer);
        return new PhotographerDTO(savedPhotographer);
    }

    public List<PhotographerDTO> list() {
        return repository.findAll().stream().map(PhotographerDTO::new).toList();
    }
}
