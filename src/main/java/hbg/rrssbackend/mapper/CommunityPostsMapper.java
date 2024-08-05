package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CommunityPostsDto;
import hbg.rrssbackend.dto.ProductDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CommunityPostsMapper {

    AppUserService appUserService;

    public CommunityPostsDto toDto(CommunityPosts communityPosts) {
        if (communityPosts == null) {
            return null;
        }
        CommunityPostsDto dto = new CommunityPostsDto();
        dto.setCpId(communityPosts.getCpId());
        dto.setUserId(communityPosts.getAppUser() != null ? communityPosts.getAppUser().getUserId() : null);
        dto.setUsername(communityPosts.getAppUser() != null ? communityPosts.getAppUser().getUsername() : null);
        dto.setTitle(communityPosts.getTitle());
        dto.setContent(communityPosts.getContent());
        dto.setCpTime(communityPosts.getCpTime());
        return dto;
    }

    public  CommunityPosts fromDto(CommunityPostsDto dto) {
        if (dto == null) {
            return null;
        }

        CommunityPosts communityPosts = new CommunityPosts();
        //communityPosts.setCpId(dto.getCpId());
        communityPosts.setTitle(dto.getTitle());
        communityPosts.setContent(dto.getContent());
        communityPosts.setCpTime(dto.getCpTime());
        Optional<AppUser> user = appUserService.getUserById(dto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            communityPosts.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        return communityPosts;
    }



}
