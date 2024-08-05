package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.SupportTicket;
import hbg.rrssbackend.model.UserContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserContentRepository extends JpaRepository<UserContent, Long> {
    @Query("SELECT uc FROM UserContent uc WHERE uc.appUser.userId = :userId")
    List<UserContent> findUserContentByUserId(@Param("userId") long userId);

    @Query(value = "SELECT * FROM user_content WHERE user_id = :userId ORDER BY user_content_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<UserContent> findUserContentByPage(@Param("userId") long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

}
