package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CPCommentsDto;
import hbg.rrssbackend.dto.SupportTicketDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.model.SupportTicket;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.CommunityPostsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class SupportTicketMapper {
    AppUserService appUserService;

    public SupportTicketDto toDto(SupportTicket supportTicket) {
        if (supportTicket == null) {
            return null;
        }
        SupportTicketDto dto = new SupportTicketDto();
        dto.setTicketId(supportTicket.getTicketId());
        dto.setText(supportTicket.getText());
        dto.setAnswer(supportTicket.getAnswer());
        dto.setUserId(supportTicket.getAppUser() != null ? supportTicket.getAppUser().getUserId() : null);
        dto.setUsername(supportTicket.getAppUser() != null ? supportTicket.getAppUser().getUsername() : null);
        dto.setTTime(supportTicket.getTTime());
        dto.setTitle(supportTicket.getTitle());
        return dto;
    }

    public  SupportTicket fromDto(SupportTicketDto dto) {
        if (dto == null) {
            return null;
        }

        SupportTicket supportTicket = new SupportTicket();
        supportTicket.setTicketId(dto.getTicketId());
        supportTicket.setText(dto.getText());
        supportTicket.setAnswer(dto.getAnswer());
        supportTicket.setTTime(dto.getTTime());
        supportTicket.setTitle(dto.getTitle());
        Optional<AppUser> user = appUserService.getUserById(dto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            supportTicket.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        return supportTicket;
    }

}
