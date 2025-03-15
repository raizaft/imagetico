package br.edu.ifpb.pweb2.retrato.repository;

import br.edu.ifpb.pweb2.retrato.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByAuthority(String authority);
}
