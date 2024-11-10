package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class LikeId implements Serializable {

    @Column(name = "photo_id")
    private Integer photoId;
    @Column(name = "photographer_id")
    private Integer photographerId;
}