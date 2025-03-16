package br.edu.ifpb.pweb2.retrato.repository;

import br.edu.ifpb.pweb2.retrato.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

    Hashtag findByText(String text);

    List<Hashtag> findByTextStartingWith(String query);
}
