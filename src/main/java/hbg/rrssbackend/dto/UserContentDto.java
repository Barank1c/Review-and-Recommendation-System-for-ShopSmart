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
public class UserContentDto {
    private Long userContentId;
    private String title;
    private String content;
    private Long userId;
    private String username;
    private Timestamp cTime;

}
