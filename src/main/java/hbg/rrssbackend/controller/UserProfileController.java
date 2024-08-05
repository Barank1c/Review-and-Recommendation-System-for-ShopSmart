package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.ProductDto;
import hbg.rrssbackend.dto.ReviewDto;
import hbg.rrssbackend.dto.SupportTicketDto;
import hbg.rrssbackend.dto.UserRewardDto;
import hbg.rrssbackend.mapper.ProductMapper;
import hbg.rrssbackend.mapper.SupportTicketMapper;
import hbg.rrssbackend.mapper.UserRewardMapper;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final AppUserService appUserService;
    private final ApplyRoleService applyRoleService;
    private final UserRewardService userRewardService;
    private final UserRewardMapper userRewardMapper;
    private final ProductPurchaseService productPurchaseService;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final SupportTicketService supportTicketService;
    private final SupportTicketMapper supportTicketMapper;

    @GetMapping("/getAppliedForRole")
    public ResponseEntity<Boolean> getAppliedForRole(@AuthenticationPrincipal UserDetails user, @RequestParam Role role) {
        if (user==null){
            return ResponseEntity.badRequest().body(null);
        }
        long userId = appUserService.getUserByUsername(user.getUsername()).get().getUserId();
        ApplyRole applyRole = applyRoleService.findIsApplied(userId, role);
        if(applyRole==null) return ResponseEntity.ok(false);
        else return ResponseEntity.ok(true);
    }

    @PostMapping("/applyRole")
    public ResponseEntity<String> applyRole(@AuthenticationPrincipal UserDetails user, @RequestParam Role role) {
        if (user==null){
            return ResponseEntity.badRequest().body("You are not logged in");
        }
        if(role == Role.USER || role == Role.ADMIN){
            return ResponseEntity.badRequest().body("You can not apply for User or Admin role");
        }
        AppUser user1 = appUserService.getUserByUsername(user.getUsername()).get();
        if(user1.getRole() == Role.ADMIN){
            return ResponseEntity.badRequest().body("You can not apply if you are an Admin");
        }
        if(user1.getRole() != Role.USER){
            return ResponseEntity.badRequest().body("You can not apply if you are not an User");
        }
        ApplyRole applyRole = applyRoleService.findIsApplied(user1.getUserId(), role);
        if(applyRole==null) {
            ApplyRole applyRole1 = new ApplyRole();
            applyRole1.setRole(role);
            applyRole1.setAppUser(user1);
            applyRoleService.save(applyRole1);
            return ResponseEntity.ok("Successfully applied");
        }
        else return ResponseEntity.badRequest().body("You already applied for this role");
    }


    @GetMapping("/userRewards/paged")
    public ResponseEntity<List<UserRewardDto>> getUserRewardsByPage(@AuthenticationPrincipal UserDetails user, @RequestParam(defaultValue = "1") int page) {
        if(appUserService.getUserByUsername(user.getUsername()).get().getRole()==Role.ADMIN){
            return  ResponseEntity.badRequest().body(null);
        }
        int pageSize = 5;
        List<UserReward> userRewards = userRewardService.getUserRewardsByPage(page, pageSize,appUserService.getUserByUsername(user.getUsername()).get().getUserId());
        List<UserRewardDto> userRewardDtos = new ArrayList<>();
        for(UserReward userReward : userRewards) {
            userRewardDtos.add(userRewardMapper.toDto(userReward));
        }
        return ResponseEntity.ok(userRewardDtos);
    }



    @GetMapping("/userRewards/page-count")
    public ResponseEntity<Integer> getUserRewardsPageCount(@AuthenticationPrincipal UserDetails user) {
        if(appUserService.getUserByUsername(user.getUsername()).get().getRole()==Role.ADMIN){
            return  ResponseEntity.badRequest().body(null);
        }
        int pageSize = 5; // Sayfa başına gösterilecek ürün sayısı
        int totalRewardCount = userRewardService.getUserRewardCount(appUserService.getUserByUsername(user.getUsername()).get().getUserId()); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalRewardCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }



    @GetMapping("/userProducts/paged")
    public ResponseEntity<List<ProductDto>> getUserProductsByPage(@AuthenticationPrincipal UserDetails user, @RequestParam(defaultValue = "1") int page) {
        if(appUserService.getUserByUsername(user.getUsername()).get().getRole()==Role.ADMIN){
            return  ResponseEntity.badRequest().body(null);
        }
        int pageSize = 5;
        List<Long> userProductIds = productPurchaseService.findUserProductsByPage(page, pageSize,appUserService.getUserByUsername(user.getUsername()).get().getUserId());
        List<ProductDto> userProductDtos = new ArrayList<>();
        for(Long userProductId : userProductIds) {
            userProductDtos.add(productMapper.productToProductDto(productService.getProductById(userProductId).get()));
        }
        return ResponseEntity.ok(userProductDtos);
    }



    @GetMapping("/userProducts/page-count")
    public ResponseEntity<Integer> getUserProductsPageCount(@AuthenticationPrincipal UserDetails user) {
        if(appUserService.getUserByUsername(user.getUsername()).get().getRole()==Role.ADMIN){
            return  ResponseEntity.badRequest().body(null);
        }
        int pageSize = 5; // Sayfa başına gösterilecek ürün sayısı
        int totalProductCount = productPurchaseService.getUserProductsCount(appUserService.getUserByUsername(user.getUsername()).get().getUserId()); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalProductCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }



    @GetMapping("/getUserSupportTickets/paged")
    public ResponseEntity<List<SupportTicketDto>> getUserSupportTickets(@AuthenticationPrincipal UserDetails user, @RequestParam(defaultValue = "1") int page) {
        if(appUserService.getUserByUsername(user.getUsername()).get().getRole()==Role.ADMIN){
            return  ResponseEntity.badRequest().body(null);
        }
        int pageSize = 5;
        List<SupportTicket> supportTickets = supportTicketService.getUserTicketsByPage(page, pageSize,appUserService.getUserByUsername(user.getUsername()).get().getUserId());
        List<SupportTicketDto> supportTicketDtos = new ArrayList<>();
        for(SupportTicket supportTicket : supportTickets) {
            supportTicketDtos.add(supportTicketMapper.toDto(supportTicket));
        }
        return ResponseEntity.ok(supportTicketDtos);
    }


    @GetMapping("/getUserSupportTickets/page-count")
    public ResponseEntity<Integer> getUserSupportTicketsCount(@AuthenticationPrincipal UserDetails user) {
        if(appUserService.getUserByUsername(user.getUsername()).get().getRole()==Role.ADMIN){
            return  ResponseEntity.badRequest().body(null);
        }
        int pageSize = 5; // Sayfa başına gösterilecek ürün sayısı
        int totalTicketCount = supportTicketService.getUserTicketCount(appUserService.getUserByUsername(user.getUsername()).get().getUserId()); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalTicketCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }



    @PostMapping("/addSupportTicket")
    public ResponseEntity<String> addSupportTicket(@AuthenticationPrincipal UserDetails user,
                                                      @RequestParam("title") String title,
                                                      @RequestParam("text") String text) {

        SupportTicket supportTicket = new SupportTicket();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if(appUser.getRole()==Role.ADMIN){
            return  ResponseEntity.badRequest().body("You can not add a ticket if you are an admin");
        }

        if(title.isBlank() || text.isBlank()) return ResponseEntity.badRequest().body("title or text is null");
        supportTicket.setAppUser(appUser);
        supportTicket.setTitle(title);
        supportTicket.setText(text);
        supportTicketService.save(supportTicket);
        return ResponseEntity.status(HttpStatus.CREATED).body("ticket saved succesfully");
    }


    @PutMapping("/updateSupportTicket/{ticketId}")
    public ResponseEntity<String> updateTicket(@AuthenticationPrincipal UserDetails user,
                                               @RequestParam("title") String title,
                                               @RequestParam("text") String text,
                                               @PathVariable("ticketId") long ticketId) {

        Optional<SupportTicket> supportTicket = supportTicketService.findById(ticketId);
        if(!supportTicket.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
        }
        SupportTicket supportTicket1 = supportTicket.get();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();

        if( !((supportTicket1.getAppUser().getUserId() == appUser.getUserId()) && !text.isBlank() && !title.isBlank())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bad Request");
        }
        supportTicket1.setText(text);
        supportTicket1.setTitle(title);
        supportTicketService.save(supportTicket1);
        return ResponseEntity.status(HttpStatus.OK).body("Ticket updated successfully");
    }

    @DeleteMapping("/removeTicket/{ticketId}")
    public ResponseEntity<Void> removeTicket(@AuthenticationPrincipal UserDetails user,@PathVariable long ticketId) {
        Optional<SupportTicket> supportTicket = supportTicketService.findById(ticketId);
        if(supportTicket.isPresent()) {
            SupportTicket supportTicket1 = supportTicket.get();
            if(supportTicket1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()) {
                supportTicketService.removeById(ticketId);
            }
        }
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal UserDetails user,
                                                @RequestParam("name") String name,
                                                @RequestParam("email") String email,
                                                @RequestParam("phoneNumber") String phoneNumber,
                                                @RequestParam("gender") String gender,
                                                @RequestParam("dateOfBirth") Date dateOfBirth,
                                                @RequestParam("theme") String theme,
                                                @RequestParam("notificationEnabled") boolean notificationEnabled){


        AppUser appUser = appUserService.getUserByUsername(user.getUsername()).get();
        appUser.setName(name);
        appUser.setEmail(email);
        if(Objects.equals(gender, "male") || Objects.equals(gender, "female")){
            appUser.setGender(gender);
        }
        if(Objects.equals(theme, "dark") || Objects.equals(theme, "light")){
            appUser.setTheme(theme);
        }
        appUser.setNotificationEnabled(notificationEnabled);
        appUser.setDateOfBirth(dateOfBirth);
        if ((phoneNumber != null && phoneNumber.matches("^\\+90\\d{10}$"))){
            appUser.setPhoneNumber(phoneNumber);
        }
        appUserService.updateUser(appUser);
        return ResponseEntity.ok("User updated!");
    }





}
