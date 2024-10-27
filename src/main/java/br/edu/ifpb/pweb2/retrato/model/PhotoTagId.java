package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class PhotoTagId implements Serializable {
    private Integer photoId;
    private Integer tagId;

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}