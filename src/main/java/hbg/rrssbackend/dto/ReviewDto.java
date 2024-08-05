package hbg.rrssbackend.dto;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private long reviewId;
    private String text;
    private long userId;
    private Integer reviewUpvotes;
    private Boolean isUpvotedByUser;
    private String username;
    private Timestamp reviewTime;
    //private int upvotes;
    private long productId;
}
