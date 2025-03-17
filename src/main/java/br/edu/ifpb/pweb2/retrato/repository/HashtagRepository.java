package br.edu.ifpb.pweb2.retrato.repository;

import br.edu.ifpb.pweb2.retrato.model.Hashtag;
import br.edu.ifpb.pweb2.retrato.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

    Hashtag findByText(String text);

    List<Hashtag> findByTextStartingWith(String query);

    @Query("SELECT h FROM Hashtag h JOIN h.photos p WHERE p.id = :photoId")
    List<Hashtag> findHashtagsByPhotoId(@Param("photoId") Integer photoId);
}
