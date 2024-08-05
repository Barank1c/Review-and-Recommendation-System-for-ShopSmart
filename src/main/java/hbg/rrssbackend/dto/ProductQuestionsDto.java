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
public class ProductQuestionsDto {
    private long pqId;
    private String text;
    private String answer;
    private Integer pqUpvote;
    private Boolean isUpvotedByUser;
    private long userId;
    private String username;
    private Timestamp qTime;
    private long productId;
}
