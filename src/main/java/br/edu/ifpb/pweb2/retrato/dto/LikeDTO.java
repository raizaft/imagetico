package br.edu.ifpb.pweb2.retrato.dto;

import br.edu.ifpb.pweb2.retrato.model.Comment;

public record LikeDTO(
        Integer photographerId,
        Integer photoId
) {

    public LikeDTO(Comment comment) {
        this(
                comment.getPhotographer().getId(),
                comment.getPhoto().getId()
        );
    }
}