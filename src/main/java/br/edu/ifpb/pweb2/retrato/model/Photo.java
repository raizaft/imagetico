package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String description;
    private byte[] imageData;
    private String imageUrl;
    private int curtidas;

    @ElementCollection
    private List<String> hashtags = new ArrayList<>();

    private LocalDate dataPublicacao = LocalDate.now();

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comentarios = new ArrayList<>();

    @ManyToOne
    private Photographer photographer;


}
