package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;

@Entity
public class PhotoTag {
    @EmbeddedId
    private PhotoTagId id;

    @ManyToOne
    private Photo photo;

    @ManyToOne
    private Tag tag;

    public PhotoTagId getId() {
        return id;
    }

    public void setId(PhotoTagId id) {
        this.id = id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
