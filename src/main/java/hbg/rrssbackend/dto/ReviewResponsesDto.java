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
public class ReviewResponsesDto {
    private long rrId;
    private String text;
    private long userId;
    private Integer rrUpvotes;
    private Boolean isUpvotedByUser;
    private String username;
    private Timestamp RRTime;
    private long reviewId;

}
