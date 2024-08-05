package hbg.rrssbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String username;
    private String email;
    private String hashedPassword;
    private String phoneNumber;
    private String gender;
    private Date dateOfBirth;

}
