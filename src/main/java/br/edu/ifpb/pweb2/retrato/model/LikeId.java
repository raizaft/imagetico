package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class LikeId implements Serializable {

    @Column(name = "photo_id")
    private Integer photoId;
    @Column(name = "photographer_id")
    private Integer photographerId;

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getPhotographerId() {
        return photographerId;
    }

    public void setPhotographerId(Integer photographerId) {
        this.photographerId = photographerId;
    }
}