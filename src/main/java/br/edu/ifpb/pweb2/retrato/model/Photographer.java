package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tb_photographer")
public class Photographer extends User {

    private boolean suspended = false;

    @ManyToMany
    @JoinTable(
            name = "seguidores",
            joinColumns = @JoinColumn(name = "fotografo_id"),
            inverseJoinColumns = @JoinColumn(name = "seguidor_id")
    )
    private List<Photographer> seguidores = new ArrayList<>();

    @ManyToMany(mappedBy = "seguidores")
    private List<Photographer> seguindo = new ArrayList<>();

    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();


}
