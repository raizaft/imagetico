package br.edu.ifpb.pweb2.retrato.repository;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotographerRepository extends JpaRepository<Photographer, Integer> {

    boolean existsByEmail(String email);

    Optional<Photographer> findById(Integer id);

    Photographer findByEmail(String email);

    Page<Photographer> findByIdNot(Integer id, Pageable pageable);
}
