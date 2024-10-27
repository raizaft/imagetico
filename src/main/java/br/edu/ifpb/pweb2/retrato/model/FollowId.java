package br.edu.ifpb.pweb2.retrato.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class FollowId implements Serializable {

    private Integer followeeId;
    private Integer followerId;

    public Integer getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(Integer followeeId) {
        this.followeeId = followeeId;
    }

    public Integer getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Integer followerId) {
        this.followerId = followerId;
    }
}
