package hbg.rrssbackend.service;

import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.model.ReviewResponses;
import hbg.rrssbackend.repository.ReviewResponsesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewResponsesService {

    private final ReviewResponsesRepository reviewResponsesRepository;

    public ReviewResponsesService(ReviewResponsesRepository reviewResponsesRepository){
        this.reviewResponsesRepository = reviewResponsesRepository;
    }

    public void saveRR(ReviewResponses rr) {
        reviewResponsesRepository.save(rr);
    }

    public void removeRR(long rrId) {
        reviewResponsesRepository.deleteById(rrId);
    }


    public Optional<ReviewResponses> getRRById(long rrId) {
        return reviewResponsesRepository.findById(rrId);
    }

    public List<ReviewResponses> getAllResponses(long reviewId){
        return reviewResponsesRepository.getAllResponses(reviewId);
    }


}
