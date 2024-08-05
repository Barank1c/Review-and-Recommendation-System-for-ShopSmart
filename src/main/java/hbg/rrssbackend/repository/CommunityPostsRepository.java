package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.CommunityPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostsRepository extends JpaRepository<CommunityPosts, Long> {

    @Query(value = "SELECT * FROM community_posts ORDER BY cp_id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<CommunityPosts> findCommunityPostsByPage(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(cp) FROM CommunityPosts cp")
    long countAllPosts();


}
