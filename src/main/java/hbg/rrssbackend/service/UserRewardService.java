package hbg.rrssbackend.service;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.UserReward;
import hbg.rrssbackend.repository.UserRewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRewardService {
    private final UserRewardRepository userRewardRepository;
    public UserRewardService(UserRewardRepository userRewardRepository) {
        this.userRewardRepository = userRewardRepository;
    }

    public void save(UserReward userReward) {
        userRewardRepository.save(userReward);
    }

    @Transactional
    public List<UserReward> getUserRewardsByPage(int page, int pageSize, long userId) {
        int offset = (page - 1) * pageSize;
        return userRewardRepository.findUserRewardsByPage(offset, pageSize, userId);
    }

    public int getUserRewardCount(long userId) {
        return userRewardRepository.getUserRewardCount(userId);
    }
}
