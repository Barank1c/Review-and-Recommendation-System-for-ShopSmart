package hbg.rrssbackend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class RRUpvoteID implements Serializable {
    private Long rrId;
    private Long userId;

    public RRUpvoteID() {}

    public RRUpvoteID(Long rrId, Long userId) {
        this.rrId = rrId;
        this.userId = userId;
    }
}
