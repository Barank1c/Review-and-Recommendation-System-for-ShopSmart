package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CPCommentsDto;
import hbg.rrssbackend.dto.CommunityEventDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.model.CommunityEvent;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CommunityEventMapper {
    AppUserService appUserService;

    public CommunityEventDto toDto(CommunityEvent communityEvent) {
        if (communityEvent == null) {
            return null;
        }
        CommunityEventDto dto = new CommunityEventDto();
        dto.setCeId(communityEvent.getCeId());
        dto.setMaxUser(communityEvent.getMaxUser());
        dto.setTitle(communityEvent.getTitle());
        dto.setDescription(communityEvent.getDescription());
        dto.setUserId(communityEvent.getAppUser() != null ? communityEvent.getAppUser().getUserId() : null);
        dto.setUsername(communityEvent.getAppUser() != null ? communityEvent.getAppUser().getUsername() : null);
        dto.setCeTime(communityEvent.getCeTime());
        return dto;
    }

    public  CommunityEvent fromDto(CommunityEventDto dto) {
        if (dto == null) {
            return null;
        }

        CommunityEvent communityEvent = new CommunityEvent();
        communityEvent.setCeId(dto.getCeId());
        communityEvent.setMaxUser(dto.getMaxUser());
        communityEvent.setTitle(dto.getTitle());
        communityEvent.setDescription(dto.getDescription());
        communityEvent.setCeTime(dto.getCeTime());
        Optional<AppUser> user = appUserService.getUserById(dto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            communityEvent.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }

        return communityEvent;
    }

}
