package hbg.rrssbackend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class WishListItemID implements Serializable {
    private Long productId;
    private Long wishListId;

    public WishListItemID() {}

    public WishListItemID(Long productId, Long wishListId) {
        this.productId = productId;
        this.wishListId = wishListId;
    }

}

