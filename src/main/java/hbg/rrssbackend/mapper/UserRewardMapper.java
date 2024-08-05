package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CPCommentsDto;
import hbg.rrssbackend.dto.UserRewardDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.model.UserReward;
import hbg.rrssbackend.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor

public class UserRewardMapper {
    AppUserService appUserService;


    public UserRewardDto toDto(UserReward userReward) {
        if (userReward == null) {
            return null;
        }
        UserRewardDto dto = new UserRewardDto();
        dto.setUserRewardId(userReward.getUserRewardId());
        dto.setDescription(userReward.getDescription());
        dto.setUserId(userReward.getAppUser() != null ? userReward.getAppUser().getUserId() : null);
        dto.setUsername(userReward.getAppUser() != null ? userReward.getAppUser().getUsername() : null);
        dto.setRewardName(userReward.getRewardName());
        dto.setGivenTime(userReward.getGivenTime());
        return dto;
    }

    public  UserReward fromDto(UserRewardDto dto) {
        if (dto == null) {
            return null;
        }

        UserReward userReward = new UserReward();
        userReward.setUserRewardId(dto.getUserRewardId());
        userReward.setDescription(dto.getDescription());
        userReward.setGivenTime(dto.getGivenTime());
        userReward.setRewardName(dto.getRewardName());
        Optional<AppUser> user = appUserService.getUserById(dto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            userReward.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        return userReward;
    }
}
