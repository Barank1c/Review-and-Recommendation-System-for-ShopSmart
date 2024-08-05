package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.ReviewDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.ProductService;
import hbg.rrssbackend.service.ReviewUpvoteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor

public class ReviewMapper {

    private final ReviewUpvoteService reviewUpvoteService;
    AppUserService appUserService;
    ProductService productService;

    public  ReviewDto toDto(Review review) {
        if (review == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isupvoted = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            long userId = appUserService.getUserByUsername(userDetails.getUsername()).get().getUserId();
            isupvoted= reviewUpvoteService.isReviewUpvotedByUser(userId, review.getReviewId());
        }

        long reviewId = review.getReviewId();
        return ReviewDto.builder()
                .reviewId(reviewId)
                .text(review.getText())
                .userId(review.getAppUser() != null ? review.getAppUser().getUserId() : null)
                .username(review.getAppUser() != null ? review.getAppUser().getUsername() : null)
                .reviewTime(review.getReviewTime())
                //.upvotes(review.getUpvotes())
                .reviewUpvotes(reviewUpvoteService.getTotalUpvotesOfReview(reviewId))
                .isUpvotedByUser(isupvoted)
                .productId(review.getProduct() != null ? review.getProduct().getProductId() : null)
                .build();
    }

    public  Review fromDto(ReviewDto dto) {
        if (dto == null) {
            return null;
        }
        Review review = new Review();
        review.setReviewId(dto.getReviewId());
        review.setText(dto.getText());
        Optional<AppUser> appUser = appUserService.getUserById(dto.getUserId());
        if (appUser.isPresent()) {
            review.setAppUser(appUser.get());
        }
        else{
            review.setAppUser(null);
        }
        Optional<Product> product = productService.getProductById(dto.getProductId());
        if (product.isPresent()) {
            review.setProduct(product.get());
        }
        else {
            review.setProduct(null);
        }
        review.setReviewTime(dto.getReviewTime());
        //review.setUpvotes(dto.getUpvotes());
        return review;
    }
}
