package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;

@Entity
public class Follow {
    @EmbeddedId
    private FollowId id;

    @ManyToOne
    private Photographer followee;

    @ManyToOne
    private Photographer follower;
}
