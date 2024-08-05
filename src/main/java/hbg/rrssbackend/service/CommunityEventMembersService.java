package hbg.rrssbackend.service;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CommunityEventMembersID;
import hbg.rrssbackend.model.CommunityEvent;
import hbg.rrssbackend.model.CommunityEventMembers;
import hbg.rrssbackend.repository.CommunityEventMembersRepository;
import hbg.rrssbackend.repository.AppUserRepository;
import hbg.rrssbackend.repository.CommunityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommunityEventMembersService {

    private final CommunityEventMembersRepository communityEventMembersRepository;
    private final AppUserRepository appUserRepository;
    private final CommunityEventRepository communityEventRepository;

    @Autowired
    public CommunityEventMembersService(CommunityEventMembersRepository communityEventMembersRepository, AppUserRepository appUserRepository, CommunityEventRepository communityEventRepository) {
        this.communityEventMembersRepository = communityEventMembersRepository;
        this.appUserRepository = appUserRepository;
        this.communityEventRepository = communityEventRepository;
    }

    public CommunityEventMembers joinEvent(Long ceId, UserDetails userDetails) {
        Optional<AppUser> user = appUserRepository.findByUsername(userDetails.getUsername());
        Optional<CommunityEvent> eventOpt = communityEventRepository.findById(ceId);
        if (eventOpt.isPresent()) {
            CommunityEvent event = eventOpt.get();
            long currentMembersCount = communityEventMembersRepository.countByCeId(ceId);
            if (currentMembersCount >= event.getMaxUser()) {
                throw new IllegalStateException("Max user limit reached for this event");
            }
            CommunityEventMembers eventMember = new CommunityEventMembers();
            eventMember.setCeId(event.getCeId());
            eventMember.setUserId(user.get().getUserId());
            eventMember.setAppUser(user.get());
            eventMember.setCommunityEvent(event);
            return communityEventMembersRepository.save(eventMember);
        } else {
            throw new IllegalArgumentException("Event not found");
        }
    }

    public void unjoinEvent(Long ceId, UserDetails userDetails) {
        Optional<AppUser> user = appUserRepository.findByUsername(userDetails.getUsername());
        CommunityEventMembersID eventMemberId = new CommunityEventMembersID(ceId, user.get().getUserId());
        communityEventMembersRepository.deleteById(eventMemberId);
    }
}
