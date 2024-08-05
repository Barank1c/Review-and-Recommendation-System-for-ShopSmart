package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.model.ReviewUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT COUNT(p) FROM Review p where p.product.productId = :productId")
    int getTotalReviewCount(long productId);

    @Query(value = "SELECT * FROM review r where r.product_id = :productId ORDER BY r.review_time  OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<Review> findReviewsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("productId") long productId);


}
