package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.AppUserDto;
import hbg.rrssbackend.dto.UserContentDto;
import hbg.rrssbackend.mapper.UserContentMapper;
import hbg.rrssbackend.mapper.UserMapper;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Role;
import hbg.rrssbackend.model.UserContent;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.UserContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private UserContentService userContentService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserContentMapper userContentMapper;

    @GetMapping("/blogs")
    public ResponseEntity<List<AppUserDto>> getUsersWithContentByPage(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(appUserService.getUsersWithContentByPage(page));
    }

    @GetMapping("/blogPageCount")
    public ResponseEntity<Integer> getTotalUserPages(@RequestParam(defaultValue = "10") int pageSize) {
        int totalPages = appUserService.getContentfulUserPageCount(pageSize);
        return ResponseEntity.ok(totalPages);
    }

    @GetMapping("/contents/{userId}")
    public ResponseEntity<List<UserContentDto>> getUserContentByPage(@PathVariable long userId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<UserContent> pagedUserContent = userContentService.getUserContentByPage(userId, offset, pageSize);
        List<UserContentDto> pagedUserContentDtos = pagedUserContent.stream()
                .map(userContentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pagedUserContentDtos);
    }

    @PostMapping("/addContent/{userId}")
    public ResponseEntity<UserContentDto> addUserContent(@AuthenticationPrincipal UserDetails user,
                                                         @PathVariable long userId,
                                                         @RequestParam String title,
                                                         @RequestParam String content) {
        Optional<AppUser> optionalAppUser = appUserService.getUserByUsername(user.getUsername());
        if (optionalAppUser.isPresent()) {
            AppUser authenticatedUser = optionalAppUser.get();
            if (authenticatedUser.getUserId() == userId ||
                    authenticatedUser.getRole() == Role.ADMIN ||
                    authenticatedUser.getRole() == Role.COMMUNITY_MODERATOR) {
                if (title == null || content == null) {
                    return ResponseEntity.badRequest().build();
                }
                UserContentDto userContentDto = UserContentDto.builder()
                        .title(title)
                        .content(content)
                        .userId(userId)
                        .username(appUserService.getUsernameByUserId(userId))
                        .build();
                UserContent userContent = userContentService.addUserContent(userId, userContentDto);
                return ResponseEntity.ok(userContentMapper.toDto(userContent));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/editContent/{userId}")
    public ResponseEntity<UserContentDto> editUserContent(@AuthenticationPrincipal UserDetails user,
                                                          @PathVariable long userId,
                                                          @RequestParam String title,
                                                          @RequestParam String content,
                                                          @RequestParam long userContentId) {
        Optional<AppUser> optionalAppUser = appUserService.getUserByUsername(user.getUsername());
        if (optionalAppUser.isPresent()) {
            AppUser authenticatedUser = optionalAppUser.get();
            if (authenticatedUser.getUserId() == userId ||
                    authenticatedUser.getRole() == Role.ADMIN ||
                    authenticatedUser.getRole() == Role.COMMUNITY_MODERATOR) {
                if (title == null || content == null) {
                    return ResponseEntity.badRequest().build();
                }
                UserContentDto userContentDto = UserContentDto.builder()
                        .title(title)
                        .content(content)
                        .userId(userId)
                        .build();
                UserContent userContent = userContentService.editUserContent(userContentId, userContentDto);
                if (userContent == null) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(userContentMapper.toDto(userContent));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{userId}/deleteContent/{userContentId}")
    public ResponseEntity<Void> deleteUserContent(@AuthenticationPrincipal UserDetails user,
                                                  @PathVariable long userId,
                                                  @PathVariable long userContentId) {
        Optional<AppUser> optionalAppUser = appUserService.getUserByUsername(user.getUsername());
        if (optionalAppUser.isPresent()) {
            AppUser authenticatedUser = optionalAppUser.get();
            if (authenticatedUser.getUserId() == userId ||
                    authenticatedUser.getRole() == Role.ADMIN ||
                    authenticatedUser.getRole() == Role.COMMUNITY_MODERATOR) {
                userContentService.deleteUserContent(userContentId);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}

