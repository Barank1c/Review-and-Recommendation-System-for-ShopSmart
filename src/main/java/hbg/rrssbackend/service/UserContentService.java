package hbg.rrssbackend.service;

import hbg.rrssbackend.dto.UserContentDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.UserContent;
import hbg.rrssbackend.repository.UserContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserContentService {
    private final UserContentRepository userContentRepository;
    public UserContentService(UserContentRepository userContentRepository) {
        this.userContentRepository = userContentRepository;
    }

    public List<UserContent> getUserContentByPage(long userId, int offset, int pageSize) {
        return userContentRepository.findUserContentByPage(userId, offset, pageSize);
    }

    public UserContent addUserContent(long userId, UserContentDto userContentDto) {
        AppUser user = new AppUser();
        user.setUserId(userId);
        UserContent userContent = new UserContent();
        userContent.setTitle(userContentDto.getTitle());
        userContent.setContent(userContentDto.getContent());
        userContent.setAppUser(user);
        userContent.setCTime(userContentDto.getCTime());
        return userContentRepository.save(userContent);
    }


    public UserContent editUserContent(long userContentId, UserContentDto userContentDto) {
        Optional<UserContent> optionalUserContent = userContentRepository.findById(userContentId);
        if (optionalUserContent.isPresent()) {
            UserContent existingUserContent = optionalUserContent.get();
            existingUserContent.setTitle(userContentDto.getTitle());
            existingUserContent.setContent(userContentDto.getContent());
            return userContentRepository.save(existingUserContent);
        } else {
            throw new IllegalArgumentException("User content not found with id: " + userContentId);//burayı sil mümkünse
        }
    }


    public void deleteUserContent(long userContentId) {
        userContentRepository.deleteById(userContentId);
    }



}

