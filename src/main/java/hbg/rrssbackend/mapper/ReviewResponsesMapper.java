package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.ReviewDto;
import hbg.rrssbackend.dto.ReviewResponsesDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.model.ReviewResponses;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.ProductService;
import hbg.rrssbackend.service.RRUpvoteService;
import hbg.rrssbackend.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ReviewResponsesMapper {
    AppUserService appUserService;
    ReviewService reviewService;
    RRUpvoteService rrUpvoteService;


    public ReviewResponsesDto toDto(ReviewResponses reviewResponses) {
        if (reviewResponses == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isupvoted = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            long userId = appUserService.getUserByUsername(userDetails.getUsername()).get().getUserId();
            isupvoted= rrUpvoteService.isRRUpvotedByUser(userId, reviewResponses.getRrId());
        }

        long id = reviewResponses.getRrId();
        return ReviewResponsesDto.builder()
                .rrId(reviewResponses.getRrId())
                .text(reviewResponses.getText())
                .userId(reviewResponses.getAppUser() != null ? reviewResponses.getAppUser().getUserId() : null)
                .username(reviewResponses.getAppUser() != null ? reviewResponses.getAppUser().getUsername() : null)
                .RRTime(reviewResponses.getRRTime())
                .rrUpvotes(rrUpvoteService.getTotalUpvotesOfRR(id))
                .isUpvotedByUser(isupvoted)
                //.upvotes(review.getUpvotes())
                .reviewId(reviewResponses.getReview() != null ? reviewResponses.getReview().getReviewId() : null)
                .build();
    }



    public  ReviewResponses fromDto(ReviewResponsesDto dto) {
        if (dto == null) {
            return null;
        }
        ReviewResponses reviewResponses = new ReviewResponses();
        reviewResponses.setRrId(dto.getRrId());
        reviewResponses.setText(dto.getText());
        Optional<AppUser> appUser = appUserService.getUserById(dto.getUserId());
        if (appUser.isPresent()) {
            reviewResponses.setAppUser(appUser.get());
        }
        else{
            reviewResponses.setAppUser(null);
        }
        Optional<Review> review = reviewService.getReviewById(dto.getReviewId());
        if (review.isPresent()) {
            reviewResponses.setReview(review.get());
        }
        else {
            reviewResponses.setReview(null);
        }
        reviewResponses.setRRTime(dto.getRRTime());
        //review.setUpvotes(dto.getUpvotes());
        return reviewResponses;
    }
}
