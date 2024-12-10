package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
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

    @ElementCollection
    private List<String> hashtags = new ArrayList<>();

    private LocalDateTime createdAt;


    public Comment(String commentText, Photographer photographer, Photo photo) {
        this.commentText = commentText;
        this.photographer = photographer;
        this.photo = photo;
        this.createdAt = LocalDateTime.now();
    }
}
