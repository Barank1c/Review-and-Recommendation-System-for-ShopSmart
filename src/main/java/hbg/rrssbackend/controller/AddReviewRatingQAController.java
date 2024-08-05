package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.ProductDto;
import hbg.rrssbackend.dto.ProductQuestionsDto;
import hbg.rrssbackend.dto.ReviewDto;
import hbg.rrssbackend.dto.ReviewResponsesDto;
import hbg.rrssbackend.mapper.ProductMapper;
import hbg.rrssbackend.mapper.ProductQuestionsMapper;
import hbg.rrssbackend.mapper.ReviewMapper;
import hbg.rrssbackend.mapper.ReviewResponsesMapper;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.repository.ProductPurchaseRepository;
import hbg.rrssbackend.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.synth.SynthToolTipUI;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/addRR")

public class AddReviewRatingQAController {

    private final AppUserService appUserService;
    private final ReviewMapper reviewMapper;
    private final ReviewResponsesMapper reviewResponsesMapper;
    private final ReviewService reviewService;
    private final ProductPurchaseService productPurchaseService;
    private final ReviewUpvoteService reviewUpvoteService;
    private final RatingService ratingService;
    private final ProductService productService;
    private final ReviewResponsesService reviewResponsesService;
    private final RRUpvoteService rrUpvoteService;
    private final ProductQuestionsMapper productQuestionsMapper;
    private final ProductQuestionsService productQuestionsService;
    private final PQUpvoteService pqUpvoteService;
    private final ProductMapper productMapper;

    public AddReviewRatingQAController(AppUserService appUserService,
                                       ReviewMapper reviewMapper,
                                       ReviewService reviewService,
                                       ProductPurchaseService productPurchaseService,
                                       ReviewUpvoteService reviewUpvoteService,
                                       RatingService ratingService,
                                       ProductService productService,
                                       ReviewResponsesMapper reviewResponsesMapper,
                                       ReviewResponsesService reviewResponsesService,
                                       RRUpvoteService rrUpvoteService, ProductQuestionsMapper productQuestionsMapper, ProductQuestionsService productQuestionsService, PQUpvoteService pqUpvoteService, ProductMapper productMapper) {
        this.appUserService = appUserService;
        this.reviewMapper = reviewMapper;
        this.reviewService = reviewService;
        this.productPurchaseService = productPurchaseService;
        this.reviewUpvoteService = reviewUpvoteService;
        this.ratingService = ratingService;
        this.productService = productService;
        this.reviewResponsesMapper = reviewResponsesMapper;
        this.reviewResponsesService = reviewResponsesService;
        this.rrUpvoteService = rrUpvoteService;
        this.productQuestionsMapper = productQuestionsMapper;
        this.productQuestionsService = productQuestionsService;
        this.pqUpvoteService = pqUpvoteService;
        this.productMapper = productMapper;
    }

    @PostMapping("/addReview/{productId}")
    public ResponseEntity<ReviewDto> addReview(@AuthenticationPrincipal UserDetails user,
                                               @RequestParam("text") String text,
                                               @PathVariable("productId") long productId) {


        ReviewDto reviewDto = new ReviewDto();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if (appUser.getRole()== Role.ADMIN){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Product> product1 = productService.getProductById(productId);
        if(product1.isPresent()){
            if(product1.get().getAppUser().getUserId() == appUser.getUserId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(!((productPurchaseService.getUserBoughtProduct(temp.get().getUserId(),productId)) && !text.isBlank()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        reviewDto.setText(text);
        reviewDto.setProductId(productId);
        reviewDto.setUserId(appUser.getUserId());
        reviewDto.setUsername(appUser.getUsername());
        Review review = reviewMapper.fromDto(reviewDto);
        reviewService.saveReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    @PutMapping("/updateReview/{reviewId}")
    public ResponseEntity<String> updateReview(@AuthenticationPrincipal UserDetails user,
                                               @RequestParam("text") String text,
                                               @PathVariable("reviewId") long reviewId) {

        Optional<Review> review = reviewService.getReviewById(reviewId);
        if(!review.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
        Review reviewObj = review.get();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();

        if( !((reviewObj.getAppUser().getUserId() == appUser.getUserId() ||
                appUser.getRole()== Role.ADMIN) && !text.isBlank())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bad Request");
        }
        reviewObj.setText(text);
        reviewService.saveReview(reviewObj);
        return ResponseEntity.status(HttpStatus.OK).body("Review updated successfully");
    }

    @DeleteMapping("/removeReview/{reviewId}")
    public ResponseEntity<Void> removeReview(@AuthenticationPrincipal UserDetails user,@PathVariable long reviewId) {
        Optional<Review> review = reviewService.getReviewById(reviewId);
        if(review.isPresent()) {
            Review review1 = review.get();
            if(review1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()
            || review1.getAppUser().getRole()== Role.ADMIN) {
                reviewService.removeReview(reviewId);
            }
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/upvoteReview/{reviewId}")
    public ResponseEntity<Void> upvoteReview(@AuthenticationPrincipal UserDetails user,
                                               @PathVariable("reviewId") long reviewId) {
        Optional<Review> review = reviewService.getReviewById(reviewId);
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if(review.isPresent() && appUser.getRole() != Role.ADMIN) {
            Review review1 = review.get();
            if(!reviewUpvoteService.isReviewUpvotedByUser(appUser.getUserId(),review1.getReviewId())){
                reviewUpvoteService.upvoteReview(appUser.getUserId(),review1.getReviewId());
            }
            else{
                reviewUpvoteService.unvoteReview(appUser.getUserId(),review1.getReviewId());
            }
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rateProduct/{productId}")
    public ResponseEntity<String> rateProduct(@AuthenticationPrincipal UserDetails user,
                                                     @PathVariable("productId") long productId,
                                                     @RequestParam int rating) {
        if(!(rating==1||rating==2||rating==3||rating==4||rating==5)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Value should be 1,2,3,4,5");
        }

        Optional<Product> product = productService.getProductById(productId);
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if (appUser.getRole()==Role.ADMIN){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admins cannot rate product");
        }
        if(product.isPresent()) {
            Product product1 = product.get();
            if(product1.getAppUser().getUserId() == appUser.getUserId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not rate your own product");
            }
            if(!productPurchaseService.getUserBoughtProduct(appUser.getUserId(),productId))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User did not buy product");


            Integer temp1 = ratingService.getRatingOfUser(appUser.getUserId(),product1.getProductId());
            if(temp1==null ||  temp1 != rating ){
                Rating rating1 = new Rating(productId,appUser.getUserId(),rating,productService.getProductById(productId).get(),appUserService.getUserById(appUser.getUserId()).get());
                ratingService.addRating(rating1);
            }
            else{
                ratingService.removeRating(appUser.getUserId(),product1.getProductId());
            }
        }
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/addRR/{productId}/{reviewId}")
    public ResponseEntity<ReviewResponsesDto> addRR(@AuthenticationPrincipal UserDetails user,
                                               @RequestParam("text") String text,
                                               @PathVariable("reviewId") long reviewId,
                                               @PathVariable("productId") long productId) {


        ReviewResponsesDto reviewResponsesDto = new ReviewResponsesDto();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if(appUser.getRole()== Role.ADMIN){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Product> product1 = productService.getProductById(productId);
        if(!product1.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(!((productPurchaseService.getUserBoughtProduct(temp.get().getUserId(),productId) ) && !text.isBlank()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        reviewResponsesDto.setText(text);
        reviewResponsesDto.setReviewId(reviewId);
        reviewResponsesDto.setUserId(appUser.getUserId());
        reviewResponsesDto.setUsername(appUser.getUsername());
        ReviewResponses rr = reviewResponsesMapper.fromDto(reviewResponsesDto);
        reviewResponsesService.saveRR(rr);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponsesDto);
    }

    @PutMapping("/updateRR/{rrId}")
    public ResponseEntity<String> updateRR(@AuthenticationPrincipal UserDetails user,
                                               @RequestParam("text") String text,
                                               @PathVariable("rrId") long rrId) {

        Optional<ReviewResponses> reviewResponses = reviewResponsesService.getRRById(rrId);
        if(!reviewResponses.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review Response not found");
        }
        ReviewResponses reviewResponses1 = reviewResponses.get();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();

        if(!((reviewResponses1.getAppUser().getUserId() == appUser.getUserId() ||
                appUser.getRole()== Role.ADMIN) && !text.isBlank())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bad Request");
        }
        reviewResponses1.setText(text);
        reviewResponsesService.saveRR(reviewResponses1);
        return ResponseEntity.status(HttpStatus.OK).body("Review Response updated successfully");
    }



    @DeleteMapping("/removeRR/{rrId}")
    public ResponseEntity<Void> removeRR(@AuthenticationPrincipal UserDetails user,@PathVariable long rrId) {
        Optional<ReviewResponses> reviewResponses = reviewResponsesService.getRRById(rrId);
        if(reviewResponses.isPresent()) {
            ReviewResponses reviewResponses1 = reviewResponses.get();
            if(reviewResponses1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()
            || appUserService.getUserByUsername(user.getUsername()).get().getRole() == Role.ADMIN) {
                reviewResponsesService.removeRR(rrId);
            }
        }
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/upvoteRR/{rrId}")
    public ResponseEntity<Void> upvoteRR(@AuthenticationPrincipal UserDetails user,
                                             @PathVariable("rrId") long rrId) {
        Optional<ReviewResponses> reviewResponses = reviewResponsesService.getRRById(rrId);
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if(reviewResponses.isPresent() && appUser.getRole() != Role.ADMIN) {
            ReviewResponses reviewResponses1 = reviewResponses.get();
            if(!rrUpvoteService.isRRUpvotedByUser(appUser.getUserId(),reviewResponses1.getRrId())){
                rrUpvoteService.upvoteRR(appUser.getUserId(),reviewResponses1.getRrId());
            }
            else{
                rrUpvoteService.unvoteRR(appUser.getUserId(),reviewResponses1.getRrId());
            }
        }
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/addQ/{productId}")
    public ResponseEntity<ProductQuestionsDto> addQ(@AuthenticationPrincipal UserDetails user,
                                                    @RequestParam("text") String text,
                                                    @PathVariable("productId") long productId) {


        ProductQuestionsDto productQuestionsDto = new ProductQuestionsDto();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if( text.isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        Optional<Product> product = productService.getProductById(productId);
        if(!product.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(product.get().getAppUser().getUserId() == appUser.getUserId() || appUser.getRole() == Role.ADMIN){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        productQuestionsDto.setText(text);
        productQuestionsDto.setUserId(appUser.getUserId());
        productQuestionsDto.setUsername(appUser.getUsername());
        productQuestionsDto.setProductId(productId);
        ProductQuestions rr = productQuestionsMapper.fromDto(productQuestionsDto);
        productQuestionsService.savePQ(rr);
        return ResponseEntity.status(HttpStatus.CREATED).body(productQuestionsDto);
    }

    @PutMapping("/updateQ/{pqId}")
    public ResponseEntity<String> updateQ(@AuthenticationPrincipal UserDetails user,
                                          @RequestParam("text") String text,
                                          @RequestParam("answer") String answer,
                                          @PathVariable("pqId") long pqId) {

        Optional<ProductQuestions> productQuestions = productQuestionsService.getPQById(pqId);
        if(!productQuestions.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Question is not found");
        }
        ProductQuestions productQuestions1 = productQuestions.get();
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();

        ProductQuestionsDto productQuestionsDto = productQuestionsMapper.toDto(productQuestions1);

        long user_id = productService.getProductById(productQuestionsDto.getProductId()).get().getAppUser().getUserId();

        if(appUser.getRole() == Role.ADMIN || user_id == appUser.getUserId()){
            if(!answer.isBlank()){
                productQuestions1.setAnswer(answer);
            }
        }
        if(productQuestions1.getProduct().getAppUser().getUserId() != appUser.getUserId() || appUser.getRole()== Role.ADMIN
                || productQuestions1.getAppUser().getUserId() == appUser.getUserId()){
            if(!text.isBlank()){
                productQuestions1.setText(text);
            }
        }
        productQuestionsService.savePQ(productQuestions1);
        return ResponseEntity.status(HttpStatus.OK).body("Product Question updated successfully");
    }

    @DeleteMapping("/removeQ/{pqId}")
    public ResponseEntity<Void> removeQ(@AuthenticationPrincipal UserDetails user,@PathVariable long pqId) {
        Optional<ProductQuestions> productQuestions = productQuestionsService.getPQById(pqId);
        if(productQuestions.isPresent()) {
            ProductQuestions productQuestions1 = productQuestions.get();
            if(productQuestions1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()
                    || appUserService.getUserByUsername(user.getUsername()).get().getRole() == Role.ADMIN) {
                productQuestionsService.removeQ(pqId);
            }
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/removeAnswer/{pqId}")
    public ResponseEntity<Void> removeAnswer(@AuthenticationPrincipal UserDetails user,@PathVariable long pqId) {
        Optional<ProductQuestions> productQuestions = productQuestionsService.getPQById(pqId);
        if(productQuestions.isPresent()) {
            ProductQuestions productQuestions1 = productQuestions.get();

            ProductQuestionsDto productQuestionsDto = productQuestionsMapper.toDto(productQuestions1);

            long user_id = productService.getProductById(productQuestionsDto.getProductId()).get().getAppUser().getUserId();

            if(user_id == appUserService.getUserByUsername(user.getUsername()).get().getUserId()
                    || appUserService.getUserByUsername(user.getUsername()).get().getRole() == Role.ADMIN) {
                productQuestions1.setAnswer(null);
                productQuestionsService.savePQ(productQuestions1);
            }
        }
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/upvoteQ/{pqId}")
    public ResponseEntity<Void> upvoteQ(@AuthenticationPrincipal UserDetails user,
                                         @PathVariable("pqId") long pqId) {
        Optional<ProductQuestions> productQuestions = productQuestionsService.getPQById(pqId);
        Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
        AppUser appUser = temp.get();
        if(productQuestions.isPresent() && appUser.getRole() != Role.ADMIN) {
            ProductQuestions productQuestions1 = productQuestions.get();
            if(!pqUpvoteService.isQUpvotedByUser(appUser.getUserId(),productQuestions1.getPqId())){
                pqUpvoteService.upvoteQ(appUser.getUserId(),productQuestions1.getPqId());
            }
            else{
                pqUpvoteService.unvoteQ(appUser.getUserId(),productQuestions1.getPqId());
            }
        }
        return ResponseEntity.noContent().build();
    }

    }


