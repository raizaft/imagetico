package br.edu.ifpb.pweb2.retrato.repository;

import br.edu.ifpb.pweb2.retrato.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPhotoId(int photoId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :id")
    void deleteCommentById(@Param("id") Integer id);
}