package hbg.rrssbackend.service;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.Rating;
import hbg.rrssbackend.model.ReviewUpvote;
import hbg.rrssbackend.repository.RatingRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository){
        this.ratingRepository = ratingRepository;
    }

    public void addRating(Rating rating) {
        ratingRepository.save(rating);
    }


    public Integer getRatingOfUser(long userId, long productId) {
        Rating rating = ratingRepository.isProductRatedByUser(userId,productId);
        if(rating!=null) return rating.getRating();
        else return null;
    }

    public Double getRatingOfProduct(long productId){
        return ratingRepository.getRatingOfProduct(productId);
    }

    public void removeRating(long userId,long productId){
        ratingRepository.removeRating( userId, productId);
    }

}
