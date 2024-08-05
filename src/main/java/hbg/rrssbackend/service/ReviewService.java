package hbg.rrssbackend.service;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.model.ReviewUpvote;
import hbg.rrssbackend.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    public void removeReview(long reviewId) {
        reviewRepository.deleteById(reviewId);
    }


    public Optional<Review> getReviewById(long reviewId) {
        return reviewRepository.findById(reviewId);
    }


    @Transactional
    public List<Review> getReviewsByPage(int page, int pageSize,long productId) {
        int offset = (page - 1) * pageSize;
        return reviewRepository.findReviewsByPage(offset, pageSize, productId);
    }

    public int getTotalReviewCount(long productId) {
        return reviewRepository.getTotalReviewCount( productId);
    }





}
