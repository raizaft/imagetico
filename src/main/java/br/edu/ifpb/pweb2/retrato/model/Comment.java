package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_comment")
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotEmpty(message = "Escreva algo.")
    private String commentText;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Photographer photographer;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Photo photo;

    private LocalDateTime createdAt;

    public Comment(String commentText, Photographer photographer, Photo photo) {
        this.commentText = commentText;
        this.photographer = photographer;
        this.photo = photo;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
