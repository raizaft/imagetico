package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PhotographerService {
    @Autowired
    private PhotographerRepository repository;


    public Page<Photographer> listPaginatedExcludingAdmin(Integer adminId, Pageable pageable) {
        return repository.findByIdNot(adminId, pageable);
    }
    public Photographer register(Photographer photographer) {
        if (repository.existsByEmail(photographer.getEmail())) {
            throw new IllegalArgumentException("Já existe uma conta com esse e-mail.");
        }
       return repository.save(photographer);
    }

    @Transactional
    public Photographer save(Photographer photographer) {
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


    public Photographer findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Photographer getPhotographerByEmail(String email) {
        return repository.findByEmail(email);
    }

    public void suspendComments(Integer photographerId) {
        Photographer photographer = repository.findById(photographerId).orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado"));
        photographer.setCanComment(false);
        repository.save(photographer);
    }

    public void allowComments(Integer photographerId) {
        Photographer photographer = repository.findById(photographerId).orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado"));
        photographer.setCanComment(true);
        repository.save(photographer);
    }

    public void followPhotographer(Integer followerId, Integer followedId) {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("Um fotógrafo não pode seguir a si mesmo.");
        }

        Photographer follower = findById(followerId);
        Photographer followed = findById(followedId);
        if(follower.getFollowing().contains(followed)) {
            throw new IllegalArgumentException("Fotografo já é seguido");
        }
        follower.getFollowing().add(followed);

        repository.save(followed);
    }

    public void followAllowedAction(Photographer photographer){
        if(photographer.isFollowAllowed()) {
            photographer.setFollowAllowed(false);
        } else{
            photographer.setFollowAllowed(true);
        }
    }


    public List<Photographer> getFollowing(Integer photographerId) {
        Photographer photographer = repository.findById(photographerId)
                .orElseThrow(() -> new IllegalArgumentException("Photographer not found with ID: " + photographerId));
        return photographer.getFollowing();
    }
}
