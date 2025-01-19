package br.edu.ifpb.pweb2.retrato.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tb_photographer")
@SuperBuilder
@ToString(callSuper = true)
public class Photographer extends User {

    private boolean suspended = false;

    @ManyToMany
    @JoinTable(
            name = "seguidores",
            joinColumns = @JoinColumn(name = "fotografo_id"),
            inverseJoinColumns = @JoinColumn(name = "seguidor_id")
    )

    @ToString.Exclude
    @JsonIgnore
    private List<Photographer> seguidores = new ArrayList<>();

    @ManyToMany(mappedBy = "seguidores")
    @ToString.Exclude
    @JsonIgnore
    private List<Photographer> seguindo = new ArrayList<>();

    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    public Photographer() {
        super();
    }
}
