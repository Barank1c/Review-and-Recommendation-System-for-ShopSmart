package hbg.rrssbackend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class PQUpvoteID implements Serializable {
    private Long pqId;
    private Long userId;

    public PQUpvoteID() {}

    public PQUpvoteID(Long pqId, Long userId) {
        this.pqId = pqId;
        this.userId = userId;
    }
}
