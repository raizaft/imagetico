package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PhotographerService {
    @Autowired
    private PhotographerRepository repository;

    public Photographer register(Photographer photographer) {
        if (repository.existsByEmail(photographer.getEmail())) {
            throw new IllegalArgumentException("Já existe uma conta com esse e-mail.");
        }
       return repository.save(photographer);
    }

    public Photographer login(String name, String email) {
        if (repository.existsByEmail(email)) {
            Photographer photographer = repository.findByEmail(email);
            if (Objects.equals(photographer.getName(),name)){
                return photographer;
            }
        }
        return null;
    }

    public List<Photographer> list() {
        return repository.findAll();
    }

    public void suspendPhotographer(Integer photographerId) {
        Photographer photographer = repository.findById(photographerId)
                .orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado"));
        photographer.setSuspended(true);
        repository.save(photographer);
    }

    public void activatePhotographer(Integer photographerId) {
        Photographer photographer = repository.findById(photographerId)
                .orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado"));
        photographer.setSuspended(false);
        repository.save(photographer);
    }

    public void follow(Photographer photographerFollowed, Photographer photographerFollower) {
        photographerFollowed.getSeguidores().add(photographerFollower);
        photographerFollower.getSeguindo().add(photographerFollowed);
        repository.save(photographerFollowed);
        repository.save(photographerFollower);
    }

    public List<Photographer> getAllFollowers(Integer id) {
        Photographer photographer = repository.findById(id).orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado"));
        return photographer.getSeguidores();
    }
}
