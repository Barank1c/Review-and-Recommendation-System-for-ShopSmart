package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.ProductQuestionsDto;
import hbg.rrssbackend.dto.ReviewDto;
import hbg.rrssbackend.dto.ReviewResponsesDto;
import hbg.rrssbackend.mapper.ProductQuestionsMapper;
import hbg.rrssbackend.mapper.ReviewMapper;
import hbg.rrssbackend.mapper.ReviewResponsesMapper;
import hbg.rrssbackend.model.ProductQuestions;
import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.model.ReviewResponses;
import hbg.rrssbackend.model.ReviewUpvote;
import hbg.rrssbackend.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/viewPQa")

public class ViewRQaController {

    private final ReviewService reviewService;
    private final ReviewResponsesService reviewResponsesService;
    private final ReviewMapper reviewMapper;
    private final ReviewResponsesMapper reviewResponsesMapper;
    private final ProductQuestionsService productQuestionsService;
    private final ProductQuestionsMapper productQuestionsMapper;

    public ViewRQaController(ReviewService reviewService, ReviewResponsesService reviewResponsesService, ReviewMapper reviewMapper, ReviewResponsesMapper reviewResponsesMapper, ProductQuestionsService productQuestionsService, ProductQuestionsMapper productQuestionsMapper) {
        this.reviewService = reviewService;
        this.reviewResponsesService = reviewResponsesService;
        this.reviewMapper = reviewMapper;
        this.reviewResponsesMapper = reviewResponsesMapper;
        this.productQuestionsService = productQuestionsService;
        this.productQuestionsMapper = productQuestionsMapper;
    }


    @GetMapping("/viewReviews/{productId}")
    public ResponseEntity<LinkedHashMap<ReviewDto, List<ReviewResponsesDto>>> viewReviews(@PathVariable("productId") int productId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 4;
        List<Review> reviews = reviewService.getReviewsByPage(page,pageSize,productId);
        LinkedHashMap<ReviewDto,List<ReviewResponsesDto>> finalMap = new LinkedHashMap<>();
        for(Review review: reviews){
            List<ReviewResponses> reviewResponses = reviewResponsesService.getAllResponses(review.getReviewId());
            List<ReviewResponsesDto> reviewResponsesDtos = new ArrayList<>();
            for(ReviewResponses i : reviewResponses) reviewResponsesDtos.add(reviewResponsesMapper.toDto(i));
            finalMap.put(reviewMapper.toDto(review),reviewResponsesDtos);
        }
        return ResponseEntity.ok(finalMap);
    }


    @GetMapping("/viewReviews/page-count/{productId}")
    public ResponseEntity<Integer> viewReviewsPageCount(@PathVariable("productId") int productId) {
        int pageSize = 4;
        int totalReviewCount = reviewService.getTotalReviewCount(productId);
        int pageCount = (int) Math.ceil((double) totalReviewCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }



    @GetMapping("/viewPQ/{productId}")
    public ResponseEntity< List<ProductQuestionsDto>> viewPQ(@PathVariable("productId") int productId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 4;
        List<ProductQuestions> productQuestions = productQuestionsService.getPQByPage(page,pageSize,productId);
        List<ProductQuestionsDto> productQuestionsDtos = new ArrayList<>();
        for(ProductQuestions i : productQuestions) productQuestionsDtos.add(productQuestionsMapper.toDto(i));
        return ResponseEntity.ok(productQuestionsDtos);
    }


    @GetMapping("/viewPQ/page-count/{productId}")
    public ResponseEntity<Integer> viewPQPageCount(@PathVariable("productId") int productId) {
        int pageSize = 4;
        int totalPQCount = productQuestionsService.getTotalPQCount(productId);
        int pageCount = (int) Math.ceil((double) totalPQCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }


}
