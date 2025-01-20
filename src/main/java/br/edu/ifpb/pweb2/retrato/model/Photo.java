package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_photo")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String description;
    @Transient
    private MultipartFile photoFile;
    private String photoPath;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @ManyToOne
    private Photographer photographer;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.likes = new ArrayList<>();
    }
}
