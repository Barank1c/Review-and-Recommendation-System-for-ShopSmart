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
public class CPCommentsDto {
    private Long cpcId;
    private Long cpId;
    private String comment;
    private String username;
    private Long userId;
    private Timestamp cpcTime;
}
