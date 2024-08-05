package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.CommunityEventDto;
import hbg.rrssbackend.mapper.CommunityEventMapper;
import hbg.rrssbackend.model.CommunityEvent;
import hbg.rrssbackend.model.Role;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.CommunityEventMembersService;
import hbg.rrssbackend.service.CommunityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community-events")
public class ComEventController {

    private final CommunityEventService communityEventService;
    private final CommunityEventMapper communityEventMapper;
    private final AppUserService appUserService;
    private final CommunityEventMembersService communityEventMembersService;


    @Autowired
    public ComEventController(CommunityEventService communityEventService, CommunityEventMapper communityEventMapper, AppUserService appUserService, CommunityEventMembersService communityEventMembersService) {
        this.communityEventService = communityEventService;
        this.communityEventMapper = communityEventMapper;
        this.appUserService = appUserService;
        this.communityEventMembersService = communityEventMembersService;
    }

    @GetMapping("/allEvents")
    public ResponseEntity<List<CommunityEventDto>> getAllEvents() {
        List<CommunityEventDto> events = communityEventService.getAllEvents().stream()
                .map(communityEventMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping("/addEvent")
    public ResponseEntity<CommunityEventDto> addEvent(
            @RequestParam Integer maxUser,
            @RequestParam String title,
            @RequestParam String description,
            @AuthenticationPrincipal UserDetails user) {
        Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();
        if (currentUserRole == Role.ADMIN || currentUserRole == Role.COMMUNITY_MODERATOR) {
            CommunityEvent communityEvent = communityEventService.addEvent(maxUser, title, description, user);
            CommunityEventDto eventDto = communityEventMapper.toDto(communityEvent);
            return new ResponseEntity<>(eventDto, HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();
        if (currentUserRole == Role.ADMIN || currentUserRole == Role.COMMUNITY_MODERATOR) {
            communityEventService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/editEvent/{id}")
    public ResponseEntity<CommunityEventDto> editEvent(
            @PathVariable Long id,
            @RequestParam Integer maxUser,
            @RequestParam String title,
            @RequestParam String description,
            @AuthenticationPrincipal UserDetails user) {
        Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();
        if (currentUserRole == Role.ADMIN || currentUserRole == Role.COMMUNITY_MODERATOR) {
            CommunityEvent communityEvent = communityEventService.editEvent(id, maxUser, title, description, user);
            CommunityEventDto eventDto = communityEventMapper.toDto(communityEvent);
            return new ResponseEntity<>(eventDto, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/joinEvent/{ceId}")
    public ResponseEntity<String> joinEvent(@PathVariable Long ceId, @AuthenticationPrincipal UserDetails user) {
        try {
            communityEventMembersService.joinEvent(ceId, user);
            return new ResponseEntity<>("Successfully joined the event", HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/unjoinEvent/{ceId}")
    public ResponseEntity<Void> unjoinEvent(@PathVariable Long ceId, @AuthenticationPrincipal UserDetails user) {
        communityEventMembersService.unjoinEvent(ceId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}