package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.AppUserDto;
import hbg.rrssbackend.dto.CategoryDto;
import hbg.rrssbackend.dto.ProductDto;
import hbg.rrssbackend.mapper.CategoryMapper;
import hbg.rrssbackend.mapper.ProductMapper;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/browseFilterP")
@RequiredArgsConstructor
public class BrowseAndFilterProducts {
    private final ProductService productService;
    private final AppUserService appUserService;
    private final ProductMapper productMapper;
    private final RatingService ratingService;
    private final ProductPurchaseService productPurchaseService;
    private final CategoryService categoryService;
    private final ProductCategoryService productCategoryService;
    private final CategoryMapper categoryMapper;
    private final WishListService wishListService;
    private final WishListItemService wishListItemService;

    @GetMapping("/recommendedProducts")
    public ResponseEntity<List<ProductDto> > getRecommendedProducts(@AuthenticationPrincipal UserDetails user)  {
        //Our recommendation algorithm is based on ratings of user on products
        if (user==null){
            return ResponseEntity.badRequest().body(null);
        }

        List<ProductDto> recommended = new ArrayList<>();
        AppUser user1 = appUserService.getUserByUsername(user.getUsername()).get();
        if (user1.getRole() == Role.ADMIN) {
            return ResponseEntity.badRequest().body(null);
        }
        //Calculating total ratings of user in categories
        HashMap<Long,Double> categoryCounts = new HashMap<>();
        HashMap<Long,Long> categoryRatingCount = new HashMap<>();
        List<Rating> ratingsOfUser = user1.getRatingList();
        if(ratingsOfUser.isEmpty()){
            return ResponseEntity.badRequest().body(null);
        }
        for(Rating i: ratingsOfUser){
            List<ProductCategory> categories = productService.getProductById(i.getProductId()).get().getProductCategories();
            for(ProductCategory j: categories){
                long categoryId = j.getCategoryId();
                if(categoryCounts.containsKey(categoryId)){
                    categoryCounts.put(categoryId,categoryCounts.get(categoryId) + (double) i.getRating());
                }else{
                    categoryCounts.put(categoryId, (double) i.getRating());
                }
                if(categoryRatingCount.containsKey(categoryId)){
                    categoryRatingCount.put(categoryId,categoryRatingCount.get(categoryId) + 1L);
                }else{
                    categoryRatingCount.put(categoryId, 1L);
                }

            }
        }

        //calculating mean scores
        for (Map.Entry<Long, Double> entry : categoryCounts.entrySet()) {
            Long categoryId = entry.getKey();
            Double count = entry.getValue();
            categoryCounts.put(categoryId, count / categoryRatingCount.get(categoryId));
        }

        //Getting weights of categories for a user
        double totalWeight= categoryCounts.values().stream().mapToDouble(Double::doubleValue).sum();
        for (Map.Entry<Long, Double> entry : categoryCounts.entrySet()) {
            Long categoryId = entry.getKey();
            Double count = entry.getValue();
            categoryCounts.put(categoryId, count / totalWeight);
        }

        //For every product we will calculate product score as sum of WeightOfCategory*ProductRating
        HashMap<Long,Double> productScores = new HashMap<>();
        List<Product> products = productService.getAllProducts();
        for(Product i:products){
            if(productPurchaseService.getUserBoughtProduct(user1.getUserId(),i.getProductId())) continue;
            if(user1.getRole() == Role.MERCHANT){
                boolean temp = false;
                for(Product j: user1.getProducts()){
                    if(j.getProductId() == i.getProductId()){
                        temp = true;
                    }
                }
                if(temp){
                    continue;
                }
            }
            double totalScore = 0;
            for(ProductCategory j: i.getProductCategories()){
                long categoryId = j.getCategoryId();
                double rating = (ratingService.getRatingOfProduct(i.getProductId())!= null) ? ratingService.getRatingOfProduct(i.getProductId()) : 0;
                totalScore +=  ((categoryCounts.get(categoryId)!= null) ? categoryCounts.get(categoryId)  : 0) *rating;

            }
            productScores.put(i.getProductId(),totalScore);
        }

        //sorting product scores
        List<Map.Entry<Long, Double>> entries = new ArrayList<>(productScores.entrySet());
        entries.sort(Map.Entry.comparingByValue());
        int count = 0;
        for(Map.Entry<Long, Double> i: entries.reversed()){
            recommended.add(productMapper.productToProductDto(productService.getProductById(i.getKey()).get()));
            count++;
            if(count==6) break;
        }

        return ResponseEntity.ok(recommended);
    }


    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable long productId) {
        Optional<Product> product = productService.getProductById(productId);
        if(product.isPresent()) {
            Product product1 = product.get();
            return ResponseEntity.ok(productMapper.productToProductDto(product1));
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/currently-popular")
    public ResponseEntity<List<ProductDto>> getCurrentlyPopularProducts() {
        List<Long> productIds = productPurchaseService.getMostPopularProducts();
        List<ProductDto> productDtos = new ArrayList<>();
        int counter = 0;
        for(Long productId : productIds) {
            productDtos.add(productMapper.productToProductDto(productService.getProductById(productId).get()));
            counter++;
            if(counter == 6) {
                break;
            }
        }
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/all/paged")
    public ResponseEntity<List<ProductDto>> getProductsByPage(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 12;
        List<Product> products = productService.getProductsByPage(page, pageSize);
        List<ProductDto> productDtos = new ArrayList<>();
        for(Product product : products) {
            productDtos.add(productMapper.productToProductDto(product));
        }
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/all/page-count")
    public ResponseEntity<Integer> getProductPageCount() {
        int pageSize = 12;
        int totalProductCount = productService.getTotalProductCount(); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalProductCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for(Category i: categories){
            categoryDtos.add(categoryMapper.toDto(i));
        }
        return ResponseEntity.ok().body(categoryDtos);
    }


    @GetMapping("/getCategory/paged")
    public ResponseEntity<List<ProductDto>> getCategory(@RequestParam String category,@RequestParam(defaultValue = "1") int page) {
        Optional<Category> category1 = categoryService.getCategoryByName(category);
        if(category1.isPresent()) {
            long categoryId = category1.get().getCategoryId();
            int pageSize = 12;
            List<Long> productIds = productCategoryService.getPagedProductsOfCategory(page,pageSize,categoryId);
            List<ProductDto> productDtos = new ArrayList<>();
            for(Long productId : productIds) {
                productDtos.add(productMapper.productToProductDto(productService.getProductById(productId).get()));
            }
            System.out.println("category api");
            return ResponseEntity.ok(productDtos);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/getCategory/page-count")
    public ResponseEntity<Integer> getCategoryPageCount(@RequestParam String category) {
        int pageSize = 12;
        Optional<Category> category1 = categoryService.getCategoryByName(category);
        if(category1.isPresent()) {
            long categoryId = category1.get().getCategoryId();
            int totalProductCount = productCategoryService.getPagedProductsOfCategoryCount(categoryId); // Veritabanındaki toplam ürün sayısı
            int pageCount = (int) Math.ceil((double) totalProductCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
            System.out.println("category sayı api");
            return ResponseEntity.ok(pageCount);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/getSearched/paged")
    public ResponseEntity<List<ProductDto>> getSearched(@RequestParam String keyword,@RequestParam(defaultValue = "1") int page) {
        int pageSize = 12;
        List<Product> products = productService.findSearchedProductByPage(page,pageSize,keyword);
        List<ProductDto> productDtos = new ArrayList<>();
        for(Product product : products) {
            productDtos.add(productMapper.productToProductDto(product));
        }
        return ResponseEntity.ok(productDtos);

    }


    @GetMapping("/getSearched/page-count")
    public ResponseEntity<Integer> getSearchedPageCount(@RequestParam String keyword) {
        int pageSize = 12;
        int totalProductCount = productService.findSearchedProductByPageCount(keyword);
        int pageCount = (int) Math.ceil((double) totalProductCount / pageSize);
        return ResponseEntity.ok(pageCount);
    }


    @PostMapping("/buyProduct/{productId}")
    public ResponseEntity<String> buyProduct(@AuthenticationPrincipal UserDetails user,
                                             @PathVariable Long productId) {
        Optional<AppUser> appUserOptinal = appUserService.getUserByUsername(user.getUsername());
        if(appUserOptinal.isPresent()) {
            AppUser appUser = appUserOptinal.get();
            if (appUser.getRole() == Role.ADMIN){
                return ResponseEntity.badRequest().body("Admins cannot buy product");
            }
            Optional<Product> productOptional = productService.getProductById(productId);
            if(productOptional.isPresent()) {
                Product product = productOptional.get();
                if(product.getAppUser().getUserId()==appUser.getUserId()){
                    return ResponseEntity.badRequest().body("You cannot buy your own product");
                }
                productPurchaseService.buyProduct(appUser.getUserId(),productId,product.getPrice());
                return ResponseEntity.ok("Successfully bought");
            }
            return ResponseEntity.badRequest().body("Invalid product id");
        }
        return ResponseEntity.badRequest().body("You are not logged in");
    }



    @PostMapping("/bookmarkProduct/{productId}")
    public ResponseEntity<String> bookmarkProduct(@AuthenticationPrincipal UserDetails user,
                                             @PathVariable Long productId) {
        Optional<AppUser> appUserOptinal = appUserService.getUserByUsername(user.getUsername());
        if(appUserOptinal.isPresent()) {
            AppUser appUser = appUserOptinal.get();
            if (appUser.getRole() == Role.ADMIN){
                return ResponseEntity.badRequest().body("Admins cannot bookmark product");
            }
            Optional<Product> productOptional = productService.getProductById(productId);
            if(productOptional.isPresent()) {
                Product product = productOptional.get();
                if(product.getAppUser().getUserId()==appUser.getUserId()){
                    return ResponseEntity.badRequest().body("You cannot bookmark your own product");
                }
                WishList favourites = wishListService.getWLByWLName("Favourites",appUser.getUserId()).get();
                for(Long i :wishListItemService.getProductsOfWL(favourites.getWishListId())){
                    if(i==productId){
                        wishListItemService.deleteProductFromWL(favourites.getWishListId(),productId);
                        return ResponseEntity.ok("Successfully unbookmarked");
                    }
                }
                wishListItemService.addProductToWL(favourites.getWishListId(),productId);
                return ResponseEntity.ok("Successfully bookmarked");
            }
            return ResponseEntity.badRequest().body("Invalid product id");
        }
        return ResponseEntity.badRequest().body("You are not logged in");
    }





}
