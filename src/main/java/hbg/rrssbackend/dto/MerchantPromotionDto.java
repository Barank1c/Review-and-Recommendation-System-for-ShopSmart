package hbg.rrssbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantPromotionDto {
    private Long mpId;
    private Long userId;
    private String username;
    private Long productId;
    private Integer discount;
    private Integer originalPrice;
    private Timestamp mpCreateTime;

}
