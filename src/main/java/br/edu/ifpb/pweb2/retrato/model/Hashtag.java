package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "hashtag")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tag_name", nullable = false)
    @NotEmpty(message = "O nome da hashtag não pode ser vazio")
    private String text;

    @ManyToMany(mappedBy = "hashtags")
    private Set<Photo> photos = new HashSet<>();

    public Hashtag() {
    }

    public Hashtag(String tagName) {
        this.text = tagName;
    }

}
