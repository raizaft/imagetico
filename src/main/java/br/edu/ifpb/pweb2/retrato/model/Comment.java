package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "tb_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String commentText;

    @ManyToOne
    private Photographer photographer;

    @ManyToOne
    private Photo photo;

    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(String commentText, Photographer photographer, Photo photo) {
        this.commentText = commentText;
        this.photographer = photographer;
        this.photo = photo;
        this.createdAt = LocalDateTime.now();
    }
}
