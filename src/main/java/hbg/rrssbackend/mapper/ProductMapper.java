package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CategoryDto;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.dto.ProductDto;
import hbg.rrssbackend.service.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProductMapper {

    private final RatingService ratingService;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final ProductPurchaseService productPurchaseService;
    private final WishListService wishListService;
    private final MerchantPromotionService merchantPromotionService;
    AppUserService appUserService;

    public  ProductDto productToProductDto(Product product) {
        if (product == null) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer ratingOfUser = null;
        Boolean isProductPurchasedByUser = false;
        Boolean isProductBookmarkedByUser = false;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            long userId = appUserService.getUserByUsername(userDetails.getUsername()).get().getUserId();
            ratingOfUser= ratingService.getRatingOfUser(userId, product.getProductId());
            isProductPurchasedByUser = productPurchaseService.getUserBoughtProduct(userId, product.getProductId());
            Optional<List<WishList>> wishLists = wishListService.getAllWishListsOfUser(userId);
            if (wishLists.isPresent()) {
                List<WishList> wishList = wishLists.get();
                for(WishList i: wishList){
                    if(Objects.equals(i.getWishListName(), "Favourites")){
                        for(WishListItem j: i.getWishListItemList()){
                            if(j.getProductId()==product.getProductId()){
                                isProductBookmarkedByUser = true;
                            }
                        }
                    }
                }
            }
        }

        ProductDto productDto = new ProductDto();
        productDto.setProductId(product.getProductId());
        productDto.setUser_id(product.getAppUser().getUserId());
        productDto.setProductName(product.getProductName());
        productDto.setDescription(product.getDescription());
        productDto.setRating(ratingService.getRatingOfProduct(product.getProductId()));
        productDto.setPrice(product.getPrice());
        productDto.setIsProductHasPromotion(merchantPromotionService.ProductsPromotion(productDto.getProductId()));
        productDto.setUploadTimestamp(product.getUploadTimestamp());
        productDto.setReview_count(product.getReviews().size());
        productDto.setIsProductPurchasedByUser(isProductPurchasedByUser);
        productDto.setIsProductBookmarkedByUser(isProductBookmarkedByUser);
        List<CategoryDto> categories = new ArrayList<>();
        for(ProductCategory i: product.getProductCategories()){
            categories.add(categoryMapper.toDto(categoryService.getCategoryById(i.getCategoryId()).get()));
        }
        productDto.setCategories(categories);
        productDto.setRatingOfUser(ratingOfUser);
        productDto.setProductImage(product.getProductImage());
        return productDto;
    }

    public  Product productDtoToProduct(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }
        Product product = new Product();
        product.setProductId(productDto.getProductId());
        Optional<AppUser> user = appUserService.getUserById(productDto.getUser_id());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            product.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setUploadTimestamp(productDto.getUploadTimestamp());
        product.setProductImage(productDto.getProductImage());
        return product;
    }


}

