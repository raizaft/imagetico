package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @ElementCollection
    private List<String> hashtags = new ArrayList<>();

    private LocalDateTime createdAt;

//    public void editarComentario(String novoTexto) {
//        this.commentText = novoTexto;
//    }
//
//    public void excluirComentario() {
//        // Lógica para exclusão (removido pelo Foto)
//    }
//
//    public void adicionarHashtag(String hashtag) {
//        hashtags.add(hashtag);
//    }
//
//    public void removerHashtag(String hashtag) {
//        hashtags.remove(hashtag);
//    }


    public Comment() {}

    public Comment(String commentText, Photographer photographer, Photo photo) {
        this.commentText = commentText;
        this.photographer = photographer;
        this.photo = photo;
        this.createdAt = LocalDateTime.now();
    }
}
