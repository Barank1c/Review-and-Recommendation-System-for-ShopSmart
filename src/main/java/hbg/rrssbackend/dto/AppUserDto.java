package hbg.rrssbackend.dto;



import hbg.rrssbackend.model.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto {
    private long userId;
    private String name;
    private String username;
    private String hashedPassword;
    private String email;
    private String phoneNumber;
    private String gender;
    private Date dateOfBirth;
    private Timestamp joinTime;
    private String theme;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean notificationEnabled;
    private boolean isBannedFromForum;
}
