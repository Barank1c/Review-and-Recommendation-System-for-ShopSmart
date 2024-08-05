package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.ApplyRoleDto;
import hbg.rrssbackend.dto.ReviewDto;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor

public class ApplyRoleMapper {
    AppUserService appUserService;

    public ApplyRoleDto toDto(ApplyRole applyRole) {
        if (applyRole == null) {
            return null;
        }
        return ApplyRoleDto.builder()
                .arId(applyRole.getArId())
                .userId(applyRole.getAppUser() != null ? applyRole.getAppUser().getUserId() : null)
                .username(applyRole.getAppUser() != null ? applyRole.getAppUser().getUsername() : null)
                .role(applyRole.getRole().toString())
                .build();
    }

    public  ApplyRole fromDto(ApplyRoleDto dto) {
        if (dto == null) {
            return null;
        }
        ApplyRole applyRole = new ApplyRole();
        applyRole.setRole(Role.valueOf(dto.getRole()));
        applyRole.setArId(dto.getArId());
        Optional<AppUser> appUser = appUserService.getUserById(dto.getUserId());
        if (appUser.isPresent()) {
            applyRole.setAppUser(appUser.get());
        }
        else{
            applyRole.setAppUser(null);
        }

        return applyRole;
    }
}
