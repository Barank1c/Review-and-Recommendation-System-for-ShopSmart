package hbg.rrssbackend.dto;


import hbg.rrssbackend.model.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private long productId;
    private long user_id;
    private String productName;
    private String description;
    private int price;
    private Double rating;
    private Boolean isProductPurchasedByUser;
    private Boolean isProductBookmarkedByUser;
    private Long isProductHasPromotion;
    private Integer review_count;
    private Integer ratingOfUser;
    private Timestamp uploadTimestamp;
    private List<CategoryDto> categories;
    @Lob
    private byte[] productImage;
}

