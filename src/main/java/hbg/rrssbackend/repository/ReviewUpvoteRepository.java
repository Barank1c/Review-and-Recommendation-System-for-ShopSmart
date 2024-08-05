package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.ReviewUpvote;
import hbg.rrssbackend.model.ReviewUpvoteID;
import hbg.rrssbackend.model.WishListItem;
import hbg.rrssbackend.model.WishListItemID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewUpvoteRepository extends JpaRepository<ReviewUpvote, ReviewUpvoteID> {

    @Query(value = "select r from ReviewUpvote r where r.userId = :userId and r.reviewId=:reviewId")
    ReviewUpvote isReviewUpvotedByUser(long userId, long reviewId);

    @Transactional
    @Modifying
    @Query(value = "insert into review_upvote (review_id,user_id) VALUES (:reviewId,:userId)",nativeQuery = true)
    void upvoteReview(long userId, long reviewId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM review_upvote  WHERE review_id = :reviewId AND user_id = :userId",nativeQuery = true)
    void unvoteReview(long userId, long reviewId);

    @Query(value = "select count(r) from ReviewUpvote r where r.reviewId=:reviewId")
    Integer getTotalUpvotesOfReview(long reviewId);

}
