package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_phototag")
public class PhotoTag {
    @EmbeddedId
    private PhotoTagId id;

    public PhotoTagId getId() {
        return id;
    }

    public void setId(PhotoTagId id) {
        this.id = id;
    }

}
