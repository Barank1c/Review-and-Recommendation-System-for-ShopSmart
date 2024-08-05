package hbg.rrssbackend.dto;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CommunityEventMembers;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityEventDto {
    private Long ceId;
    private Integer maxUser;
    private String title;
    private String description;
    private Long userId;
    private String username;
    private Timestamp ceTime;

}
