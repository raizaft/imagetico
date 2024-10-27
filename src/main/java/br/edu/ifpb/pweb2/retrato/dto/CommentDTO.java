package br.edu.ifpb.pweb2.retrato.dto;

import br.edu.ifpb.pweb2.retrato.model.Comment;

public record CommentDTO(
        Integer id,
        String commentText,
        Integer photographerId,
        Integer photoId,
        String createdAt
) {

    public CommentDTO(Comment comment) {
        this(
            comment.getId(),
            comment.getCommentText(),
            comment.getPhotographer().getId(),
            comment.getPhoto().getId(),
            comment.getCreatedAt().toString()
        );
    }
}