package hbg.rrssbackend.service;



import hbg.rrssbackend.dto.AppUserDto;
import hbg.rrssbackend.mapper.UserMapper;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.Role;
import hbg.rrssbackend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppUserService  {

    private final AppUserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public AppUserService(AppUserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<AppUser> getUserById(long userId) {
        return userRepository.findById(userId);
    }

    public Optional<AppUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public void addUser(AppUser user) {
        userRepository.save(user);
    }

    public void updateUser(AppUser user) {
        userRepository.save(user);
    }

    public void removeUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public List<AppUser> getAppUsersByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return userRepository.findAppUsersByPage(offset, pageSize);
    }


    @Transactional
    public List<AppUserDto> getUsersWithContentByPage(int page) {
        int pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<AppUser> pagedUsers = userRepository.findAppUsersByPage(offset, pageSize);
        System.out.println(pagedUsers.size());
        return pagedUsers.stream()
                .filter(user -> !user.getUserContents().isEmpty())
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    public int getContentfulUserPageCount(int pageSize) {
        List<AppUser> allUsers = userRepository.findAll();
        int totalContentfulUsers = (int) allUsers.stream()
                .filter(user -> !user.getUserContents().isEmpty())
                .count();
        return (int) Math.ceil((double) totalContentfulUsers / pageSize);
    }




    public int getTotalAppUserCount() {
        return userRepository.getTotalAppUserCount();
    }


    public String getUsernameByUserId(Long userId) {
        Optional<AppUser> user = userRepository.findById(userId);
        return user.map(AppUser::getUsername).orElse(null);
    }


    public void banUser(Long userId) {
        Optional<AppUser> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            if(user.getRole() != Role.ADMIN && user.getRole() != Role.COMMUNITY_MODERATOR) {
                user.setBannedFromForum(true);
                userRepository.save(user);
            }
        }
    }

    public void unbanUser(Long userId) {
        Optional<AppUser> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            user.setBannedFromForum(false);
            userRepository.save(user);
        }
    }

}

