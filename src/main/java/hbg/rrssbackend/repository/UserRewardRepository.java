package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.SupportTicket;
import hbg.rrssbackend.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {

    @Query(value = "SELECT * FROM user_reward p where p.user_id = :userId ORDER BY p.user_reward_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<UserReward> findUserRewardsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("userId") long userId);


    @Query("SELECT COUNT(p) FROM UserReward p where p.appUser.userId = :userId")
    int getUserRewardCount(long userId);

}
