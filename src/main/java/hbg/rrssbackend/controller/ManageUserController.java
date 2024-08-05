package hbg.rrssbackend.controller;



import hbg.rrssbackend.dto.*;
import hbg.rrssbackend.mapper.ApplyRoleMapper;
import hbg.rrssbackend.mapper.SupportTicketMapper;
import hbg.rrssbackend.mapper.UserMapper;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class ManageUserController {


    private final AppUserService appUserService;
    private final ProductQuestionsService productQuestionsService;
    private final ApplyRoleService applyRoleService;
    private final ApplyRoleMapper applyRoleMapper;
    private final WishListService wishListService;
    private final UserMapper appUserMapper;
    private final SupportTicketService supportTicketService;
    private final SupportTicketMapper supportTicketMapper;
    private final UserRewardService userRewardService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ManageUserController(AppUserService appUserService, UserMapper userMapper, WishListService wishListService, ProductQuestionsService productQuestionsService, ApplyRoleService applyRoleService, ApplyRoleMapper applyRoleMapper, SupportTicketService supportTicketService, SupportTicketMapper supportTicketMapper, UserRewardService userRewardService, PasswordEncoder passwordEncoder) {
        this.appUserService = appUserService;
        this.appUserMapper = userMapper;
        this.wishListService = wishListService;
        this.productQuestionsService = productQuestionsService;
        this.applyRoleService = applyRoleService;
        this.applyRoleMapper = applyRoleMapper;
        this.supportTicketService = supportTicketService;
        this.supportTicketMapper = supportTicketMapper;
        this.userRewardService = userRewardService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<AppUserDto>> getAllUsers() {
        List<AppUser> users = appUserService.getAllUsers();
        List<AppUserDto> appUserDtos = new ArrayList<>();
        for(AppUser appUser : users) {
            appUserDtos.add(appUserMapper.userToUserDto(appUser));
        }
        return ResponseEntity.ok(appUserDtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AppUserDto> getUserById(@PathVariable long userId) {
        Optional<AppUser> user = appUserService.getUserById(userId);
        if(user.isPresent()) {
            AppUser user1 = user.get();
            user1.setHashedPassword("");
            return ResponseEntity.ok(appUserMapper.userToUserDto(user1));
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addUser(@RequestParam("name") String name,
                                              @RequestParam("username") String username,
                                              @RequestParam("email") @Valid @Email(message = "Invalid email format") String email,
                                              @RequestParam("hashedPassword") String hashedPassword,
                                              @RequestParam("phoneNumber") String phoneNumber,
                                              @RequestParam("gender") String gender,
                                              @RequestParam("dateOfBirth") Date dateOfBirth,
                                              @RequestParam("theme") String theme,
                                              @RequestParam("role") Role role,
                                              @RequestParam("notificationEnabled") boolean notificationEnabled
                                              ) {
        try{
            Optional<AppUser> optionalAppUser = appUserService.getUserByUsername(username);
            if (optionalAppUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }
            AppUser appUser = new AppUser();
            appUser.setName(name);
            appUser.setUsername(username);
            appUser.setEmail(email);
            appUser.setHashedPassword(passwordEncoder.encode(hashedPassword));
            if (!(phoneNumber != null && phoneNumber.matches("^\\+90\\d{10}$"))){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Turkish phone number");
            }
            appUser.setPhoneNumber(phoneNumber);
            if(Objects.equals(gender, "male") || Objects.equals(gender, "female")){
                appUser.setGender(gender);
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid gender");
            }
            appUser.setDateOfBirth(dateOfBirth);
            if(Objects.equals(theme, "dark") || Objects.equals(theme, "light")){
                appUser.setTheme(theme);
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid theme");
            }
            appUser.setNotificationEnabled(notificationEnabled);
            appUser.setRole(role);
            appUserService.addUser(appUser);
            if(role!=Role.ADMIN){
                WishList wishList = new WishList();
                wishList.setAppUser(appUserService.getUserByUsername(username).get());
                wishList.setWishListName("Favourites");
                wishListService.saveWishList(wishList);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("User added successfully");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserDetails user,
                                             @PathVariable long userId,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("username") String username,
                                                 @RequestParam("email") @Valid @Email(message = "Invalid email format") String email,
                                                 @RequestParam("hashedPassword") String hashedPassword,
                                                 @RequestParam("phoneNumber") String phoneNumber,
                                                 @RequestParam("gender") String gender,
                                                 @RequestParam("dateOfBirth") Date dateOfBirth,
                                                 @RequestParam("theme") String theme,
                                                 @RequestParam("notificationEnabled") boolean notificationEnabled)
    {
        try{
            Optional<AppUser> optionalAppUser = appUserService.getUserById(userId);
            if (!optionalAppUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            AppUser appUser = optionalAppUser.get();
            AppUser appUser1 = appUserService.getUserByUsername(user.getUsername()).get();
            if(appUser1.getUserId() == appUser.getUserId() && !Objects.equals(username, appUser1.getUsername())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You can not change your own username");
            }
            appUser.setUserId(userId);
            appUser.setName(name);
            appUser.setUsername(username);
            appUser.setEmail(email);
            appUser.setHashedPassword(passwordEncoder.encode(hashedPassword));
            if (!(phoneNumber != null && phoneNumber.matches("^\\+90\\d{10}$"))){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Turkish phone number");
            }
            appUser.setPhoneNumber(phoneNumber);
            if(Objects.equals(gender, "male") || Objects.equals(gender, "female")){
                appUser.setGender(gender);
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid gender");
            }
            appUser.setDateOfBirth(dateOfBirth);
            if(Objects.equals(theme, "dark") || Objects.equals(theme, "light")){
                appUser.setTheme(theme);
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid theme");
            }
            appUser.setNotificationEnabled(notificationEnabled);
            appUserService.updateUser(appUser);
            return ResponseEntity.ok("User updated successfully1");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable long userId) {
        appUserService.removeUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public ResponseEntity<List<AppUserDto>> getAppUsersByPage(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 6; // Sayfa başına gösterilecek ürün sayısı, fronta göre değiştir
        List<AppUser> appUsers = appUserService.getAppUsersByPage(page, pageSize);
        List<AppUserDto> appUserDtos = new ArrayList<>();
        for(AppUser appUser : appUsers) {
            appUserDtos.add(appUserMapper.userToUserDto(appUser));
        }
        return ResponseEntity.ok(appUserDtos);
    }


    @GetMapping("/page-count")
    public ResponseEntity<Integer> getAppUsersPageCount() {
        int pageSize = 6; // Sayfa başına gösterilecek ürün sayısı
        int totalAppUserCount = appUserService.getTotalAppUserCount(); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalAppUserCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }



    @GetMapping("/getApplyRolesByPage/paged")
    public ResponseEntity<List<ApplyRoleDto>> getApplyRolesByPage(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 5; // Sayfa başına gösterilecek ürün sayısı, fronta göre değiştir
        List<ApplyRole> applyRoles = applyRoleService.getApplyRolesByPage(page, pageSize);
        List<ApplyRoleDto> applyRoleDtos = new ArrayList<>();
        for(ApplyRole applyRole : applyRoles) {
            applyRoleDtos.add(applyRoleMapper.toDto(applyRole));
        }
        return ResponseEntity.ok(applyRoleDtos);
    }


    @GetMapping("/getApplyRolesByPage/page-count")
    public ResponseEntity<Integer> getApplyRolesCount() {
        int pageSize = 5; // Sayfa başına gösterilecek ürün sayısı
        int totalApplyRoleCount = applyRoleService.getApplyRolesCount(); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalApplyRoleCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }

    @PostMapping("/changeRoleOfUser")
    public ResponseEntity<String> changeRoleOfUser(@RequestParam long arId) {
        Optional<ApplyRole> applyRole = applyRoleService.findById(arId);
        if(applyRole.isPresent()) {
            Role role = applyRole.get().getRole();
            AppUser user = applyRole.get().getAppUser();
            user.setRole(role);
            appUserService.updateUser(user);
            applyRoleService.removeUserRequests(user.getUserId());
            return ResponseEntity.ok("Role changed successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is not such an application");
    }

    @DeleteMapping("/rejectRoleChangeRequest")
    public ResponseEntity<String> rejectRoleChangeRequest(@RequestParam long arId) {
        Optional<ApplyRole> applyRole = applyRoleService.findById(arId);
        if(applyRole.isPresent()) {
            applyRoleService.removeById(arId);
            return ResponseEntity.ok("Role request rejected successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is not such an application");
    }



    @GetMapping("/getAllSupportTickets/paged")
    public ResponseEntity<List<SupportTicketDto>> getAllSupportTickets( @RequestParam(defaultValue = "1") int page) {
        int pageSize = 5;
        List<SupportTicket> supportTickets = supportTicketService.getAllTicketsByPage(page, pageSize);
        List<SupportTicketDto> supportTicketDtos = new ArrayList<>();
        for(SupportTicket supportTicket : supportTickets) {
            supportTicketDtos.add(supportTicketMapper.toDto(supportTicket));
        }
        return ResponseEntity.ok(supportTicketDtos);
    }


    @GetMapping("/getAllSupportTickets/page-count")
    public ResponseEntity<Integer> getAllSupportTicketsCount() {
        int pageSize = 5; // Sayfa başına gösterilecek ürün sayısı
        int totalTicketCount = supportTicketService.getAllTicketCount(); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalTicketCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }

    @PostMapping("/addUpdateAnswerToTicket/{ticketId}")
    public ResponseEntity<String> addUpdateAnswerToTicket(@RequestParam("answer") String answer,
                                                  @PathVariable("ticketId") long ticketId) {


        if( answer.isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Answer cannot be blank");


        Optional<SupportTicket> supportTicket = supportTicketService.findById(ticketId);
        if(!supportTicket.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket is not found");
        }
        SupportTicket supportTicketTemp = supportTicket.get();
        supportTicketTemp.setAnswer(answer);
        supportTicketService.save(supportTicketTemp);
        return ResponseEntity.status(HttpStatus.CREATED).body("Answer added succesfully");
    }


    @PostMapping("/addRewardToUser/{userId}")
    public ResponseEntity<String> addRewardToUser(@PathVariable("userId") Long userId,
                                                  @RequestParam("rewardName") String rewardName,
                                                  @RequestParam("description") String description) {

        UserReward userReward = new UserReward();
        Optional<AppUser> appUser = appUserService.getUserById(userId);
        if(!appUser.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not found");
        }
        if(appUser.get().getRole() == Role.ADMIN){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not add reward to an admin");
        }

        if(rewardName.isBlank() || description.isBlank()) return ResponseEntity.badRequest().body("Reward Name or Description is null");
        userReward.setRewardName(rewardName);
        userReward.setDescription(description);
        userReward.setAppUser(appUser.get());
        userRewardService.save(userReward);
        return ResponseEntity.status(HttpStatus.OK).body("Reward saved succesfully");
    }


}

