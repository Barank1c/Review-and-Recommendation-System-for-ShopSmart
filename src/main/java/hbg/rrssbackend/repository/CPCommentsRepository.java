package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.CPComments;
import hbg.rrssbackend.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CPCommentsRepository extends JpaRepository<CPComments, Long> {

    @Query(value = "SELECT * FROM CPComments WHERE cp_id = :postId ORDER BY cpc_time ASC LIMIT :pageSize OFFSET :offset", nativeQuery = true)//BURAYA DİİKKAT
    List<CPComments> findPostCommentsByPage(@Param("postId") long postId, @Param("pageSize") int pageSize, @Param("offset") int offset);

    //List<CPComments> findPostCommentsByPage(long postId, int page, int pageSize);//BURAYA DİİKKAT

    @Query("DELETE FROM CPComments c WHERE c.cpcId = :commentId")
    @Modifying
    @Transactional
    void deleteByCommentId(@Param("commentId") Long commentId);

    @Query("SELECT COUNT(c) FROM CPComments c WHERE c.communityPosts.cpId = :cpId")//BURDA HATA ÇIKABİLİR DİKKAT
    long countCommentsByPostId(@Param("cpId") Long cpId);
}
