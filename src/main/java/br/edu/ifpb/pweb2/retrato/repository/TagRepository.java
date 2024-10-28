package br.edu.ifpb.pweb2.retrato.repository;

import br.edu.ifpb.pweb2.retrato.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Photo, Integer> {
}
