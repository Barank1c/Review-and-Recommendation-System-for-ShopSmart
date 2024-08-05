package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.model.ReviewResponses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewResponsesRepository extends JpaRepository<ReviewResponses, Long> {

    @Query(value = "select rr from ReviewResponses rr where rr.review.reviewId = :reviewId order by rr.RRTime")
    List<ReviewResponses> getAllResponses(long reviewId);
}
