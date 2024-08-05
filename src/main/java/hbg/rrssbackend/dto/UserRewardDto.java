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

public class UserRewardDto {
    private Long userRewardId;
    private Long userId;
    private String username;
    private String rewardName;
    private String description;
    private Timestamp givenTime;

}
