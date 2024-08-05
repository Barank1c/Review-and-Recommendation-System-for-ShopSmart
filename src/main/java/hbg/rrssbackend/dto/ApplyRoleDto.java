package hbg.rrssbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRoleDto {
    private Long arId;
    private Long userId;
    private String username;
    private String role;
}
