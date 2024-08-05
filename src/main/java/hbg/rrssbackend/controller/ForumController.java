package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.CPCommentsDto;
import hbg.rrssbackend.dto.CommunityPostsDto;
import hbg.rrssbackend.mapper.CPCommentsMapper;
import hbg.rrssbackend.mapper.CommunityPostsMapper;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.model.CommunityPosts;
import hbg.rrssbackend.model.Role;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.CPCommentsService;
import hbg.rrssbackend.service.CommunityPostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private CommunityPostsService communityPostsService;

    @Autowired
    private CommunityPostsMapper communityPostsMapper;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private CPCommentsMapper cpCommentsMapper;

    @Autowired
    private CPCommentsService cpCommentsService;

    @GetMapping("/viewTopics")
    public ResponseEntity<List<CommunityPostsDto>> getCommunityPosts(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<CommunityPosts> communityPostsList = communityPostsService.getCommunityPostsByPage(page, pageSize);
        List<CommunityPostsDto> result = communityPostsList.stream()
                .map(communityPostsMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/viewTopic/{cpId}")
    public ResponseEntity<CommunityPostsDto> getCommunityPostById(@PathVariable Long cpId) {
        Optional<CommunityPosts> communityPost = communityPostsService.getCommunityPostById(cpId);
        return communityPost.map(post -> ResponseEntity.ok(communityPostsMapper.toDto(post)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping("/pageCount")
    public ResponseEntity<Integer> getPageCount() {
        int pageSize = 10;
        long totalPosts = communityPostsService.countTotalPosts();
        int pageCount = (int) Math.ceil((double) totalPosts / pageSize);
        return ResponseEntity.ok(pageCount);
    }

    @PostMapping("/addNewTopic")
    public ResponseEntity<CommunityPostsDto> addNewTopic(@AuthenticationPrincipal UserDetails user,
                                                         @RequestParam String title,
                                                         @RequestParam String content) {
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        if (temp.isPresent()) {
            System.out.println("çağrıldııııı");
            if (temp.get().isBannedFromForum()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            String username = appUserService.getUsernameByUserId(temp.get().getUserId());
            CommunityPostsDto communityPostsDto = CommunityPostsDto.builder()
                    .title(title)
                    .content(content)
                    .userId(temp.get().getUserId())
                    .username(username)
                    .build();
            CommunityPosts communityPosts = communityPostsMapper.fromDto(communityPostsDto);
            CommunityPosts savedPost = communityPostsService.saveCommunityPost(communityPosts);
            return ResponseEntity.status(HttpStatus.CREATED).body(communityPostsMapper.toDto(savedPost));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/deleteTopic/{cpId}")
    public ResponseEntity<Void> deleteTopic(@AuthenticationPrincipal UserDetails user, @PathVariable Long cpId) {
        Optional<CommunityPosts> temp = communityPostsService.getCommunityPostById(cpId);
        if (temp.isPresent()) {
            if (temp.get().getAppUser().isBannedFromForum()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            Long currentUserId = appUserService.getUserByUsername(user.getUsername()).get().getUserId();
            Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();

            if (temp.get().getAppUser().getUserId() == currentUserId ||
                    currentUserRole == Role.ADMIN ||
                    currentUserRole == Role.COMMUNITY_MODERATOR) {
                communityPostsService.deleteCommunityPost(cpId);
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/editTopic/{cpId}")
    public ResponseEntity<CommunityPostsDto> editTopic(@AuthenticationPrincipal UserDetails user,
                                                       @PathVariable Long cpId,
                                                       @RequestParam String title,
                                                       @RequestParam String content) {
        Optional<CommunityPosts> temp = communityPostsService.getCommunityPostById(cpId);
        if (temp.isPresent()) {
            if (temp.get().getAppUser().isBannedFromForum()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            Long currentUserId = appUserService.getUserByUsername(user.getUsername()).get().getUserId();
            Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();

            if (temp.get().getAppUser().getUserId() == currentUserId ||
                    currentUserRole == Role.ADMIN ||
                    currentUserRole == Role.COMMUNITY_MODERATOR) {
                CommunityPosts existingPost = temp.get();
                existingPost.setTitle(title);
                existingPost.setContent(content);
                CommunityPosts updatedPost = communityPostsService.saveCommunityPost(existingPost);
                return ResponseEntity.ok(communityPostsMapper.toDto(updatedPost));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/addComment/{cpId}")
    public ResponseEntity<CommunityPostsDto> addCommentToPost(@AuthenticationPrincipal UserDetails user,
                                                              @PathVariable Long cpId,
                                                              @RequestParam String comment) {
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        if (temp.isPresent()) {
            if (temp.get().isBannedFromForum()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            Optional<CommunityPosts> post = communityPostsService.getCommunityPostById(cpId);
            if (post.isPresent()) {
                CPCommentsDto cpCommentsDto = CPCommentsDto.builder()
                        .cpId(cpId)
                        .comment(comment)
                        .userId(temp.get().getUserId())
                        .username(temp.get().getUsername())
                        .build();
                CPComments newComment = cpCommentsMapper.fromDto(cpCommentsDto);
                post.get().getCpCommentsList().add(newComment);
                CommunityPosts savedPost = communityPostsService.saveCommunityPost(post.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(communityPostsMapper.toDto(savedPost));
            }
            System.out.println("bad req1");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        System.out.println("bad req2");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/deleteComment/{postId}")
    public ResponseEntity<CommunityPostsDto> deleteComment(@AuthenticationPrincipal UserDetails user,
                                                           @PathVariable Long postId,
                                                           @RequestParam Long commentId) {
        Optional<CommunityPosts> temp = communityPostsService.getCommunityPostById(postId);
        Optional<AppUser> tempUser = appUserService.getUserByUsername(user.getUsername());
        if (temp.isPresent()) {
            if (tempUser.get().isBannedFromForum()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            Long currentUserId = appUserService.getUserByUsername(user.getUsername()).get().getUserId();
            Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();

            CPComments commentToDelete = temp.get().getCpCommentsList().stream()
                    .filter(comment -> comment.getCpcId() == commentId)
                    .findFirst()
                    .orElse(null);

            if (commentToDelete.getAppUser().getUserId() == currentUserId ||
                    currentUserRole == Role.ADMIN ||
                    currentUserRole == Role.COMMUNITY_MODERATOR) {


                if (commentToDelete != null) {
                    cpCommentsService.deleteCommentById(commentId);
                    temp.get().getCpCommentsList().remove(commentToDelete);
                    communityPostsService.saveCommunityPost(temp.get());
                    return ResponseEntity.ok(communityPostsMapper.toDto(temp.get()));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/editComment/{postId}")
    public ResponseEntity<CommunityPostsDto> editComment(@AuthenticationPrincipal UserDetails user,
                                                         @PathVariable Long postId,
                                                         @RequestParam Long commentId,
                                                         @RequestParam String newComment) {
        Optional<CommunityPosts> temp = communityPostsService.getCommunityPostById(postId);
        Optional<AppUser> tempUser = appUserService.getUserByUsername(user.getUsername());
        if (temp.isPresent()) {
            if (tempUser.get().isBannedFromForum()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            Long currentUserId = appUserService.getUserByUsername(user.getUsername()).get().getUserId();
            Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();

            CPComments commentToEdit = temp.get().getCpCommentsList().stream()
                    .filter(comment -> comment.getCpcId() == commentId)
                    .findFirst()
                    .orElse(null);

            if (commentToEdit.getAppUser().getUserId() == currentUserId ||
                    currentUserRole == Role.ADMIN ||
                    currentUserRole == Role.COMMUNITY_MODERATOR) {


                if (commentToEdit != null) {
                    commentToEdit.setComment(newComment);
                    commentToEdit.setCpcTime(new Timestamp(System.currentTimeMillis()));
                    communityPostsService.saveCommunityPost(temp.get());
                    return ResponseEntity.ok(communityPostsMapper.toDto(temp.get()));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/viewComments/{postId}")
    public ResponseEntity<List<CPCommentsDto>> getPostComments(@PathVariable Long postId,
                                                               @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        Optional<CommunityPosts> post = communityPostsService.getCommunityPostById(postId);
        if (post.isPresent()) {
            List<CPComments> postComments = cpCommentsService.getPostCommentsByPage(postId, page, pageSize);
            List<CPCommentsDto> result = postComments.stream()
                    .map(cpCommentsMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/commentPageCount/{postId}")
    public ResponseEntity<Integer> getCommentPageCount(@PathVariable Long postId) {
        int pageSize = 10;
        long totalComments = cpCommentsService.countCommentsByPostId(postId);
        int pageCount = (int) Math.ceil((double) totalComments / pageSize);
        return ResponseEntity.ok(pageCount);
    }

    @PutMapping("/banUser/{userId}")
    public ResponseEntity<Void> banUser(@AuthenticationPrincipal UserDetails user, @PathVariable Long userId) {
        Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();
        if (currentUserRole == Role.ADMIN || currentUserRole == Role.COMMUNITY_MODERATOR) {
            Optional<AppUser> temp = appUserService.getUserById(userId);
            if (temp.isPresent()) {
                appUserService.banUser(userId);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/unbanUser/{userId}")
    public ResponseEntity<Void> unbanUser(@AuthenticationPrincipal UserDetails user, @PathVariable Long userId) {
        Role currentUserRole = appUserService.getUserByUsername(user.getUsername()).get().getRole();
        if (currentUserRole == Role.ADMIN || currentUserRole == Role.COMMUNITY_MODERATOR) {
            Optional<AppUser> temp = appUserService.getUserById(userId);
            if (temp.isPresent()) {
                appUserService.unbanUser(userId);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
