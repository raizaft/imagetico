package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_like")
public class Like {
    @EmbeddedId
    private LikeId id;

    public LikeId getId() {
        return id;
    }

    public void setId(LikeId id) {
        this.id = id;
    }

}