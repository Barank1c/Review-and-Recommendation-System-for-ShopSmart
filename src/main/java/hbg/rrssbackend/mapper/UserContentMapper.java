package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CPCommentsDto;
import hbg.rrssbackend.dto.UserContentDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.model.UserContent;
import hbg.rrssbackend.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor

public class UserContentMapper {
    AppUserService appUserService;

    public UserContentDto toDto(UserContent userContent) {
        if (userContent == null) {
            return null;
        }
        UserContentDto dto = new UserContentDto();
        dto.setUserContentId(userContent.getUserContentId());
        dto.setTitle(userContent.getTitle());
        dto.setContent(userContent.getContent());
        dto.setUserId(userContent.getAppUser() != null ? userContent.getAppUser().getUserId() : null);
        dto.setUsername(userContent.getAppUser() != null ? userContent.getAppUser().getUsername() : null);
        dto.setCTime(userContent.getCTime());
        return dto;
    }

    public  UserContent fromDto(UserContentDto dto) {
        if (dto == null) {
            return null;
        }

        UserContent userContent = new UserContent();
        userContent.setCTime(dto.getCTime());
        userContent.setUserContentId(dto.getUserContentId());
        userContent.setTitle(dto.getTitle());
        userContent.setContent(dto.getContent());
        Optional<AppUser> user = appUserService.getUserById(dto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            userContent.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        return userContent;
    }

}
