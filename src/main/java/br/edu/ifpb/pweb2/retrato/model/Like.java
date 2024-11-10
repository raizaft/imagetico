package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_like")
public class Like {
    @EmbeddedId
    private LikeId id;

}