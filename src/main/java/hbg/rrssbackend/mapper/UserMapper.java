package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.AppUserDto;
import hbg.rrssbackend.model.AppUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class UserMapper {

    public  AppUserDto userToUserDto(AppUser user) {
        if (user == null) {
            return null;
        }
        return AppUserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .hashedPassword(user.getHashedPassword())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .joinTime(user.getJoinTime())
                .role(user.getRole())
                .theme(user.getTheme())
                .notificationEnabled(user.isNotificationEnabled())
                .isBannedFromForum(user.isBannedFromForum())
                .build();
    }

    public  AppUser userDtoToUser(AppUserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return AppUser.builder()
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .hashedPassword(userDto.getHashedPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .gender(userDto.getGender())
                .dateOfBirth(userDto.getDateOfBirth())
                .joinTime(userDto.getJoinTime())
                .role(userDto.getRole())
                .theme(userDto.getTheme())
                .notificationEnabled(userDto.getNotificationEnabled())
                .build();
    }


}
