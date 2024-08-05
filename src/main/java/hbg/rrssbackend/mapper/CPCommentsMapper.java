package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CPCommentsDto;
import hbg.rrssbackend.dto.CommunityPostsDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.CommunityPostsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CPCommentsMapper {

    CommunityPostsService communityPostsService;
    AppUserService appUserService;

    public CPCommentsDto toDto(CPComments cpComments) {
        if (cpComments == null) {
            return null;
        }
        CPCommentsDto dto = new CPCommentsDto();
        dto.setCpcId(cpComments.getCpcId());
        dto.setCpId(cpComments.getCommunityPosts().getCpId());
        dto.setUserId(cpComments.getAppUser() != null ? cpComments.getAppUser().getUserId() : null);
        dto.setUsername(cpComments.getAppUser() != null ? cpComments.getAppUser().getUsername() : null);
        dto.setComment(cpComments.getComment());
        dto.setCpcTime(cpComments.getCpcTime());
        return dto;
    }

    public  CPComments fromDto(CPCommentsDto dto) {
        if (dto == null) {
            return null;
        }

        CPComments cpComments = new CPComments();
        cpComments.setComment(dto.getComment());
        //cpComments.setCpcId(dto.getCpcId());
        cpComments.setCpcTime(dto.getCpcTime());
        Optional<AppUser> user = appUserService.getUserById(dto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            cpComments.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        Optional<CommunityPosts> communityPosts = communityPostsService.getCommunityPostById(dto.getCpId());
        if (communityPosts.isPresent()) {
            cpComments.setCommunityPosts(communityPosts.get());
        }
        else {
            cpComments.setCommunityPosts(null);
        }
        return cpComments;
    }
}
