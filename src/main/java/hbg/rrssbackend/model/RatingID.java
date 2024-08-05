package hbg.rrssbackend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class RatingID  implements Serializable {
    private Long userId ;
    private Long productId ;

    public RatingID() {}

    public RatingID(Long userId, Long productId) {
        this.productId = productId;
        this.userId = userId;
    }
}
