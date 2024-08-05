package hbg.rrssbackend.service;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CommunityEvent;
import hbg.rrssbackend.repository.CommunityEventRepository;
import hbg.rrssbackend.repository.AppUserRepository; // Bu satırı ekledik.
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityEventService {
    private final CommunityEventRepository communityEventRepository;
    private final AppUserRepository appUserRepository; // Bu satırı ekledik.

    public CommunityEventService(CommunityEventRepository communityEventRepository, AppUserRepository appUserRepository) {
        this.communityEventRepository = communityEventRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<CommunityEvent> getAllEvents() {
        return communityEventRepository.findAll();
    }

    public CommunityEvent addEvent(Integer maxUser, String title, String description, UserDetails userDetails) {
        Optional<AppUser> user = appUserRepository.findByUsername(userDetails.getUsername());
        CommunityEvent event = CommunityEvent.builder()
                .maxUser(maxUser)
                .title(title)
                .description(description)
                .appUser(user.get())
                .build();
        return communityEventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        communityEventRepository.deleteById(id);
    }

    public CommunityEvent editEvent(Long id, Integer maxUser, String title, String description, UserDetails userDetails) {
        CommunityEvent event = communityEventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));//buraya dikkat
        event.setMaxUser(maxUser);
        event.setTitle(title);
        event.setDescription(description);
        return communityEventRepository.save(event);
    }
}
