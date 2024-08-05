package hbg.rrssbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/html/User/**").authenticated()
                        .requestMatchers("/css/User/**").authenticated()
                        .requestMatchers("/js/User/**").authenticated()
                        .requestMatchers("/html/Admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/css/Admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/js/Admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/html/Merchant/**").hasAnyAuthority("MERCHANT")
                        .requestMatchers("/css/Merchant/**").hasAnyAuthority("MERCHANT")
                        .requestMatchers("/js/Merchant/**").hasAnyAuthority("MERCHANT")
                        .requestMatchers("/api/products/**").hasAnyAuthority("MERCHANT")
                        .requestMatchers("/api/users/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/wishlists/**").hasAnyAuthority("USER","MERCHANT","COMMUNITY_MODERATOR")
                        .requestMatchers("/api/addRR/**").authenticated()
                        .requestMatchers("/photos/**").permitAll()
                        .requestMatchers("/api/viewPQa/**").permitAll()
                        .requestMatchers("/api/forum/**").permitAll()
                        .requestMatchers("/api/blog/**").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


}
