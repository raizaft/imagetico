package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@Table(name = "tb_photographer")
@SuperBuilder
@ToString(callSuper = true)
public class Photographer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @NotEmpty(message = "O nome é obrigatório.")
    private String name;
    @NotEmpty(message = "O email é obrigatório.")
    @Email(message = "Por favor, forneça um email válido.")
    private String email;

    private String password;

    private LocalDateTime createdAt;

    private String city;
    private String country;

    @Transient
    private MultipartFile profilePhotoFile;
    private String profilePhotoPath;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private boolean suspended = false;
    private boolean followAllowed = true;

    @Column(nullable = false)
    private boolean isAdmin = false;

    private boolean canComment = true;

    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Photo> photos = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "photographer_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private List<Photographer> following = new ArrayList<>();

    public Photographer() {
    }
}
