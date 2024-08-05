package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.ProductQuestionsDto;
import hbg.rrssbackend.dto.ReviewResponsesDto;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.PQUpvoteService;
import hbg.rrssbackend.service.ProductService;
import hbg.rrssbackend.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor

public class ProductQuestionsMapper {
    AppUserService appUserService;
    ProductService productService;
    PQUpvoteService pqUpvoteService;

    public ProductQuestionsDto toDto(ProductQuestions productQuestions) {
        if (productQuestions == null) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isupvoted = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            long userId = appUserService.getUserByUsername(userDetails.getUsername()).get().getUserId();
            isupvoted= pqUpvoteService.isQUpvotedByUser(userId, productQuestions.getPqId());
        }

        long id = productQuestions.getPqId();
        return ProductQuestionsDto.builder()
                .pqId(productQuestions.getPqId())
                .text(productQuestions.getText())
                .answer(productQuestions.getAnswer())
                .userId(productQuestions.getAppUser() != null ? productQuestions.getAppUser().getUserId() : null)
                .username(productQuestions.getAppUser() != null ? productQuestions.getAppUser().getUsername() : null)
                .qTime(productQuestions.getQTime())
                .pqUpvote(pqUpvoteService.getTotalUpvotesOfQ(id))
                .isUpvotedByUser(isupvoted)
                //.upvotes(review.getUpvotes())
                .productId(productQuestions.getProduct() != null ? productQuestions.getProduct().getProductId() : null)
                .build();
    }



    public  ProductQuestions fromDto(ProductQuestionsDto dto) {
        if (dto == null) {
            return null;
        }
        ProductQuestions productQuestions = new ProductQuestions();
        productQuestions.setPqId(dto.getPqId());
        productQuestions.setText(dto.getText());
        productQuestions.setAnswer(dto.getAnswer());
        Optional<AppUser> appUser = appUserService.getUserById(dto.getUserId());
        if (appUser.isPresent()) {
            productQuestions.setAppUser(appUser.get());
        }
        else{
            productQuestions.setAppUser(null);
        }
        Optional<Product> product = productService.getProductById(dto.getProductId());
        if (product.isPresent()) {
            productQuestions.setProduct(product.get());
        }
        else {
            productQuestions.setProduct(null);
        }
        productQuestions.setQTime(dto.getQTime());
        //review.setUpvotes(dto.getUpvotes());
        return productQuestions;
    }


}
