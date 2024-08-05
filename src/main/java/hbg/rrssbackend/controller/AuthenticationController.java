package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.AppUserDto;
import hbg.rrssbackend.dto.AuthenticationRequest;
import hbg.rrssbackend.dto.AuthenticationResponse;
import hbg.rrssbackend.dto.RegisterRequest;
import hbg.rrssbackend.mapper.UserMapper;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.AuthenticationService;
import hbg.rrssbackend.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AppUserService appUserService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@AuthenticationPrincipal UserDetails user, @RequestBody RegisterRequest request){
        if(user==null){
            return ResponseEntity.ok(authenticationService.register(request));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@AuthenticationPrincipal UserDetails user,@RequestBody AuthenticationRequest request, HttpServletRequest httpRequest, HttpServletResponse response){
        if(user==null) {
            AuthenticationResponse authResponse = authenticationService.authenticate(request);
            ResponseCookie newJwtCookie = ResponseCookie.from("jwt", authResponse.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, newJwtCookie.toString());

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newJwtCookie.toString()).body(authResponse);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails user,HttpServletResponse response) {
        if(user==null) {
            return ResponseEntity.badRequest().body("You are not logged in");
        }
        ResponseCookie deleteJwtCookie = ResponseCookie.from("jwt")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, deleteJwtCookie.toString());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteJwtCookie.toString())
                .body("Successfully logged out");
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails user,@RequestParam String oldPassword,@RequestParam String newPassword) {
        if(user==null) {
            return ResponseEntity.badRequest().body("You are not logged in");
        }
        AppUser appUser = appUserService.getUserByUsername(user.getUsername()).get();
        if(passwordEncoder.matches(oldPassword, appUser.getPassword())) {
            appUser.setHashedPassword(passwordEncoder.encode(newPassword));
            appUserService.updateUser(appUser);
            return ResponseEntity.ok().body("Successfully changed password");
        }else{
            return ResponseEntity.badRequest().body("Password does not match");
        }
    }



    @GetMapping("/getCurrentUser")
    public ResponseEntity<AppUserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            AppUserDto appUserDto = userMapper.userToUserDto(appUserService.getUserByUsername(userDetails.getUsername()).get()) ;
            appUserDto.setHashedPassword(null);
            return ResponseEntity.ok(appUserDto);
        }

        return ResponseEntity.badRequest().body(null);
    }


}
