package hbg.rrssbackend.service;

import hbg.rrssbackend.model.ReviewUpvote;
import hbg.rrssbackend.repository.ReviewUpvoteRepository;
import org.springframework.stereotype.Service;


@Service
public class ReviewUpvoteService {

    private final ReviewUpvoteRepository reviewUpvoteRepository;

    public ReviewUpvoteService(ReviewUpvoteRepository reviewUpvoteRepository) {
        this.reviewUpvoteRepository = reviewUpvoteRepository;
    }

    public ReviewUpvoteRepository getReviewUpvoteRepository() {
        return reviewUpvoteRepository;
    }

    public boolean isReviewUpvotedByUser(long userId, long reviewId) {
        ReviewUpvote reviewUpvote = reviewUpvoteRepository.isReviewUpvotedByUser(userId,reviewId);
        return reviewUpvote != null;
    }

    public Integer getTotalUpvotesOfReview(long reviewId){
        return reviewUpvoteRepository.getTotalUpvotesOfReview(reviewId);
    }

    public void upvoteReview(long userId,long reviewId){
        reviewUpvoteRepository.upvoteReview( userId, reviewId);
    }

    public void unvoteReview(long userId,long reviewId){
        reviewUpvoteRepository.unvoteReview( userId, reviewId);
    }
}
