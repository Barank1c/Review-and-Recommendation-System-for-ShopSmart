package hbg.rrssbackend.service;

import hbg.rrssbackend.dto.AuthenticationRequest;
import hbg.rrssbackend.dto.AuthenticationResponse;
import hbg.rrssbackend.dto.RegisterRequest;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Role;
import hbg.rrssbackend.model.WishList;
import hbg.rrssbackend.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WishListService wishListService;
    private final AppUserService appUserService;

    public AuthenticationResponse register(RegisterRequest request) {

        String name = request.getName();
        String email = request.getEmail();
        String username = request.getUsername();
        String phoneNumber = request.getPhoneNumber();
        String gender = request.getGender();
        Date dateOfBirth = request.getDateOfBirth();
        String hashedPassword = request.getHashedPassword();

        if(name.isBlank() || email.isBlank() || username.isBlank() ||
                phoneNumber.isBlank() || gender.isBlank() ||
                dateOfBirth == null || dateOfBirth.toString().isBlank() || hashedPassword.isBlank() ||
                !phoneNumber.matches("^\\+90\\d{10}$") ||
                !(Objects.equals(gender, "male") || Objects.equals(gender, "female"))
        ) {
            return new AuthenticationResponse("Invalid User Details");
        }

        var user = AppUser.builder()
                .name(name)
                .email(email)
                .username(username)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .dateOfBirth(dateOfBirth)
                .theme("light")
                .role(Role.USER)
                .notificationEnabled(true)
                .hashedPassword(passwordEncoder.encode(hashedPassword))
                .build();
        appUserRepository.save(user);
        WishList wishList = new WishList();
        wishList.setWishListName("Favourites");
        wishList.setAppUser(appUserService.getUserByUsername(username).get());
        wishListService.addWL(wishList);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getHashedPassword())
        );
        var user = appUserRepository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
