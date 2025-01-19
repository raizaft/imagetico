package br.edu.ifpb.pweb2.retrato.repository;

import br.edu.ifpb.pweb2.retrato.model.Like;
import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByPhotographerAndPhoto(Photographer photographer, Photo photo);
}
