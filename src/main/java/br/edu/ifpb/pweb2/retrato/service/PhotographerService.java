package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
}
