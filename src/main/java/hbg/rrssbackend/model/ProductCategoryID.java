package hbg.rrssbackend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class ProductCategoryID implements Serializable {
    private Long categoryId;
    private Long productId;

    public ProductCategoryID() {}

    public ProductCategoryID(Long categoryId, Long productId) {
        this.categoryId = categoryId;
        this.productId = productId;
    }
}
