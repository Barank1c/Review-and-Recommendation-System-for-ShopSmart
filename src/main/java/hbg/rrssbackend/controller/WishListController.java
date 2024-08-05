package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.ProductDto;
import hbg.rrssbackend.dto.WishListDto;
import hbg.rrssbackend.mapper.ProductMapper;
import hbg.rrssbackend.mapper.WishListMapper;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.WishList;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.ProductService;
import hbg.rrssbackend.service.WishListItemService;
import hbg.rrssbackend.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/wishlists")
public class WishListController {
    private final WishListService wishListService;
    private final WishListMapper wishListMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final WishListItemService wishListItemService;
    private final AppUserService appUserService;

    @Autowired
    public WishListController(ProductMapper productMapper, WishListItemService wishListItemService, ProductService productService, WishListService wishListService, WishListMapper wishListMapper, AppUserService appUserService) {
        this.wishListService = wishListService;
        this.wishListMapper = wishListMapper;
        this.productService = productService;
        this.wishListItemService = wishListItemService;
        this.productMapper = productMapper;
        this.appUserService = appUserService;
    }


    @GetMapping
    public ResponseEntity<LinkedHashMap<WishListDto,List<ProductDto>>> getWishLists(@AuthenticationPrincipal UserDetails user) {
        List<WishList> wishLists = wishListService.getAllWishListsOfUser(appUserService.getUserByUsername(user.getUsername()).get().getUserId()).get();
        LinkedHashMap<WishListDto,List<ProductDto>> all = new LinkedHashMap<>();
        for(WishList i: wishLists){
            List<Long> productIds = wishListItemService.getProductsOfWL(i.getWishListId());
            List<ProductDto> products = new ArrayList<>();
            for(Long j: productIds){
                Optional<Product> product = productService.getProductById(j);
                if(product.isPresent()){
                    products.add(productMapper.productToProductDto(product.get()));
                }
            }
            all.put(wishListMapper.wishListToWishListDTO(i),products);
        }
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public ResponseEntity<String> addWishlist(@AuthenticationPrincipal UserDetails user,@RequestParam("wishListName") String wishListName
    ) {
        try{
            AppUser appUser = appUserService.getUserByUsername(user.getUsername()).get();
            Optional<WishList> optionalWishList = wishListService.getWLByWLName(wishListName,appUser.getUserId());
            if (optionalWishList.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("WishList already exists");
            }

            WishList wishList = new WishList();
            wishList.setWishListName(wishListName);
            wishList.setAppUser(appUserService.getUserByUsername(user.getUsername()).get());
            wishListService.addWL(wishList);
            return ResponseEntity.status(HttpStatus.CREATED).body("WishList added successfully");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }

    @PostMapping("/aptwl/{wishListId}")
    public ResponseEntity<String> addProductsToWL(@AuthenticationPrincipal UserDetails user,@PathVariable long wishListId,@RequestBody List<Long> productIDs
    ) {
        try{
            Optional<WishList> optionalWishList = wishListService.getWLByWLID(wishListId);
            if (!optionalWishList.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("WishList does not exists");
            }

            for(Long i:productIDs){
                Optional<Product> temp = productService.getProductById(i);
                if (!temp.isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product does not exists");
                }
                if(optionalWishList.get().getAppUser().getUserId() != appUserService.getUserByUsername(user.getUsername()).get().getUserId()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the owner of this wishlist");
                }
                wishListItemService.addProductToWL(wishListId,i);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Products added successfully");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }

    @GetMapping("/aptwl/{wishListId}")
    public ResponseEntity<List<ProductDto>> getProductsForWL(@AuthenticationPrincipal UserDetails user,@PathVariable long wishListId) {
        Optional<WishList> optionalWishList = wishListService.getWLByWLID(wishListId);
        if(!optionalWishList.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(optionalWishList.get().getAppUser().getUserId() != appUserService.getUserByUsername(user.getUsername()).get().getUserId()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<Product> products = productService.getAllProducts();
        List<ProductDto> realProducts = new ArrayList<>();
        for(Product i: products){
            if(wishListItemService.getWLItembyID(wishListId,i.getProductId()).isPresent()){
                continue;
            }
            realProducts.add(productMapper.productToProductDto(i));
        }
        return ResponseEntity.ok(realProducts);
    }

    @DeleteMapping("/aptwl/{wishListId}")
    public ResponseEntity<String> deleteProductFromWL(@AuthenticationPrincipal UserDetails user,@PathVariable long wishListId,@RequestParam Long productId
    ) {
        try{
            Optional<WishList> optionalWishList = wishListService.getWLByWLID(wishListId);
            if (!optionalWishList.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("WishList does not exists");
            }
            if(optionalWishList.get().getAppUser().getUserId() != appUserService.getUserByUsername(user.getUsername()).get().getUserId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the owner of this wishlist");
            }
            wishListItemService.deleteProductFromWL(wishListId,productId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product removed successfully");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }

    @DeleteMapping("/{wishListId}")
    public ResponseEntity<String> deleteWL(@AuthenticationPrincipal UserDetails user,@PathVariable long wishListId) {
        try{
            Optional<WishList> optionalWishList = wishListService.getWLByWLID(wishListId);
            if (!optionalWishList.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("WishList does not exists");
            }
            if(optionalWishList.get().getAppUser().getUserId() != appUserService.getUserByUsername(user.getUsername()).get().getUserId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the owner of this wishlist");
            }
            if(Objects.equals(optionalWishList.get().getWishListName(), "Favourites")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Favourites can not be deleted!");
            }
            wishListService.removeWL(wishListId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Wishlist removed successfully");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }

    @PutMapping("/cwln/{wishListId}")
    public ResponseEntity<String> changeWLName(@AuthenticationPrincipal UserDetails user,@PathVariable long wishListId,@RequestParam("wishListName") String wishListName
    ) {
        try{
            Optional<WishList> optionalWishList = wishListService.getWLByWLID(wishListId);
            if (!optionalWishList.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("WishList does not exists");
            }
            if(optionalWishList.get().getAppUser().getUserId() != appUserService.getUserByUsername(user.getUsername()).get().getUserId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the owner of this wishlist");
            }
            WishList wishList = optionalWishList.get();
            List<WishList> temp = wishListService.getAllWishLists();
            for(WishList i : temp){
                if(i.getWishListId() == wishList.getWishListId()) continue;
                if(Objects.equals(i.getWishListName(), wishListName)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This wishlist already exists!");
            }
            if(Objects.equals(wishList.getWishListName(), "Favourites")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Favourites name can not be changed!");
            }
            wishList.setWishListName(wishListName);
            wishListService.saveWishList(wishList);
            return ResponseEntity.status(HttpStatus.CREATED).body("WishList name changed  successfully");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }


}
