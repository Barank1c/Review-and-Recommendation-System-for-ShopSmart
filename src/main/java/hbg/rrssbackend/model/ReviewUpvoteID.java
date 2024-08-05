package hbg.rrssbackend.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class ReviewUpvoteID implements Serializable {
    private Long reviewId;
    private Long userId;

    public ReviewUpvoteID() {}

    public ReviewUpvoteID(Long reviewId, Long userId) {
        this.reviewId = reviewId;
        this.userId = userId;
    }


}
