package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;

@Entity
public class Like {
    @EmbeddedId
    private LikeId id;

    @ManyToOne
    private Photo photo;

    @ManyToOne
    private Photographer photographer;

    public LikeId getId() {
        return id;
    }

    public void setId(LikeId id) {
        this.id = id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }
}