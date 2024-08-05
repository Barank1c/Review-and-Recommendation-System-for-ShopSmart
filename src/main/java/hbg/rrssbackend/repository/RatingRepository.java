package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.Rating;
import hbg.rrssbackend.model.RatingID;
import hbg.rrssbackend.model.ReviewUpvote;
import hbg.rrssbackend.model.ReviewUpvoteID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingID> {

    @Query(value = "select r from Rating r where r.userId = :userId and r.productId=:productId")
    Rating isProductRatedByUser(long userId, long productId);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM rating  WHERE product_id = :productId AND user_id = :userId",nativeQuery = true)
    void removeRating(long userId, long productId);

    @Query(value = "select AVG(rating) from Rating  where productId=:productId")
    Double getRatingOfProduct(long productId);
}
