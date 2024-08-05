package hbg.rrssbackend.controller;

import hbg.rrssbackend.dto.MerchantPromotionDto;
import hbg.rrssbackend.dto.ProductQuestionsDto;
import hbg.rrssbackend.mapper.MerchantPromotionMapper;
import hbg.rrssbackend.mapper.ProductQuestionsMapper;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import hbg.rrssbackend.dto.ProductDto;
import hbg.rrssbackend.mapper.ProductMapper;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/products")
public class ManageProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final AppUserService appUserService;
    private final ProductQuestionsService productQuestionsService;
    private final ProductQuestionsMapper productQuestionsMapper;
    private final MerchantPromotionService merchantPromotionService;
    private final MerchantPromotionMapper merchantPromotionMapper;
    private final ProductCategoryService productCategoryService;


    @Autowired
    public ManageProductController(ProductService productService, ProductMapper productMapper, AppUserService appUserService, ProductQuestionsService productQuestionsService, ProductQuestionsMapper productQuestionsMapper, MerchantPromotionService merchantPromotionService, MerchantPromotionMapper merchantPromotionMapper, ProductCategoryService productCategoryService) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.appUserService = appUserService;
        this.productQuestionsService = productQuestionsService;
        this.productQuestionsMapper = productQuestionsMapper;
        this.merchantPromotionService = merchantPromotionService;
        this.merchantPromotionMapper = merchantPromotionMapper;
        this.productCategoryService = productCategoryService;
    }



    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getMerchantProductById(@AuthenticationPrincipal UserDetails user,@PathVariable long productId) {
        Optional<Product> product = productService.getProductById(productId);
        if(product.isPresent()) {
            Product product1 = product.get();
            if(product1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()) {
                return ResponseEntity.ok(productMapper.productToProductDto(product1));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@AuthenticationPrincipal UserDetails user,
                                                 @RequestParam("productImage") MultipartFile productImage,
                                                 @RequestParam("productName") String productName,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("price") int price,
                                                 @RequestParam("categoryIds") List<Long> categoryIds) {
        try {
            if(categoryIds.isEmpty()) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            ProductDto productDto = new ProductDto();
            Optional<AppUser> temp = appUserService.getUserByUsername(user.getUsername());
            productDto.setUser_id(temp.get().getUserId());
            byte[] imageBytes = productImage.getBytes();
            productDto.setProductName(productName);
            productDto.setDescription(description);
            productDto.setPrice(price);
            productDto.setProductImage(imageBytes);
            Product product = productMapper.productDtoToProduct(productDto);
            productService.addProduct(product);
            productCategoryService.addCategoriesToProduct(categoryIds,product.getProductId());
            return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable long productId,
                                                @AuthenticationPrincipal UserDetails user,
                                                    @RequestParam("productName") String productName,
                                                    @RequestParam("description") String description,
                                                    @RequestParam("price") int price,
                                                    @RequestParam("productImage") MultipartFile productImage,
                                                    @RequestParam("categoryIds") List<Long> categoryIds) {
        try {
            if(categoryIds.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categories cannot be null");
            Optional<Product> optionalProduct = productService.getProductById(productId);
            if (!optionalProduct.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
            Product product1 = optionalProduct.get();
            if(product1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()) {
                byte[] imageBytes = productImage.getBytes();
                Product product = optionalProduct.get();
                MerchantPromotion merchantPromotion = merchantPromotionService.productsMerchantPromotion(productId);
                if (merchantPromotion!=null){
                    merchantPromotion.setOriginalPrice(price);
                    double temp = price*((double) (100 - merchantPromotion.getDiscount()) /100);
                    product.setPrice((int) temp);
                    merchantPromotionService.saveMerchantPromotion(merchantPromotion);
                }else{
                    product.setPrice(price);
                }
                product.setProductName(productName);
                product.setDescription(description);
                product.setProductImage(imageBytes);
                productService.updateProduct(product);
                productCategoryService.addCategoriesToProduct(categoryIds,product.getProductId());
                return ResponseEntity.status(HttpStatus.CREATED).body("Product successfully updated");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You do not have permission");

        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProduct(@AuthenticationPrincipal UserDetails user,@PathVariable long productId) {
        Optional<Product> product = productService.getProductById(productId);
        if(product.isPresent()) {
            Product product1 = product.get();
            if(product1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()) {
                productService.removeProduct(productId);
            }
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public ResponseEntity<List<ProductDto>> getMerchantProductsByPage(@AuthenticationPrincipal UserDetails user,@RequestParam(defaultValue = "1") int page) {
        int pageSize = 4;
        List<Product> products = productService.getMerchantProductsByPage(page, pageSize,appUserService.getUserByUsername(user.getUsername()).get().getUserId());
        List<ProductDto> productDtos = new ArrayList<>();
        for(Product product : products) {
            productDtos.add(productMapper.productToProductDto(product));
        }
        return ResponseEntity.ok(productDtos);
    }



    @GetMapping("/page-count")
    public ResponseEntity<Integer> getMerchantProductPageCount(@AuthenticationPrincipal UserDetails user) {
        int pageSize = 4; // Sayfa başına gösterilecek ürün sayısı
        int totalProductCount = productService.getMerchantProductCount(appUserService.getUserByUsername(user.getUsername()).get().getUserId()); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalProductCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }



    @GetMapping("/merchantPromotions/paged")
    public ResponseEntity<List<MerchantPromotionDto>> getMerchantPromotions(@AuthenticationPrincipal UserDetails user, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 5;
        List<MerchantPromotion> merchantPromotions = merchantPromotionService.getMerchantPromotionsByPage(page,pageSize,appUserService.getUserByUsername(user.getUsername()).get().getUserId());
        List<MerchantPromotionDto> merchantPromotionDtos = new ArrayList<>();
        for(MerchantPromotion merchantPromotion : merchantPromotions) {
            merchantPromotionDtos.add(merchantPromotionMapper.toDto(merchantPromotion));
        }
        return ResponseEntity.ok(merchantPromotionDtos);
    }

    @GetMapping("/merchantPromotions/page-count")
    public ResponseEntity<Integer> getMerchantPromotionsCount(@AuthenticationPrincipal UserDetails user) {
        int pageSize = 5; // Sayfa başına gösterilecek ürün sayısı
        int totalPromotionCount = merchantPromotionService.getMerchantPromotionCount(appUserService.getUserByUsername(user.getUsername()).get().getUserId()); // Veritabanındaki toplam ürün sayısı
        int pageCount = (int) Math.ceil((double) totalPromotionCount / pageSize); // Toplam ürün sayısını sayfa boyutuna böl ve yukarıya yuvarla
        return ResponseEntity.ok(pageCount);
    }

    @PostMapping("/addMerchantPromotion/{productId}")
    public ResponseEntity<String> addMerchantPromotion(@AuthenticationPrincipal UserDetails user, @PathVariable long productId,@RequestParam int discount) {
        Optional<Product> product = productService.getProductById(productId);
        AppUser appUser = appUserService.getUserByUsername(user.getUsername()).get();
        if(product.isPresent()) {
            Product product1 = product.get();
            if(product1.getAppUser().getUserId() == appUser.getUserId()) {
                if(merchantPromotionService.isProductHasPromotion(productId)){
                    return ResponseEntity.badRequest().body("Product already has a promotion");
                }
                MerchantPromotion merchantPromotion = new MerchantPromotion();
                merchantPromotion.setDiscount(discount);
                merchantPromotion.setProduct(product1);
                merchantPromotion.setAppUser(appUser);
                merchantPromotion.setOriginalPrice(product1.getPrice());
                double temp = product1.getPrice()*((double) (100 - discount) /100);
                product1.setPrice((int) temp);
                productService.updateProduct(product1);
                merchantPromotionService.saveMerchantPromotion(merchantPromotion);
                return ResponseEntity.ok("Promotion successfully added");
            }
            return ResponseEntity.badRequest().body("You are not the owner of this product");
        }
        return ResponseEntity.badRequest().body("Product is not valid");
    }



    @PostMapping("/updateMerchantPromotion/{mp_id}")
    public ResponseEntity<String> updateMerchantPromotion(@AuthenticationPrincipal UserDetails user, @PathVariable long mp_id,@RequestParam int discount) {
        Optional<MerchantPromotion> merchantPromotion = merchantPromotionService.getMerchantPromotionById(mp_id);
        AppUser appUser = appUserService.getUserByUsername(user.getUsername()).get();
        if(merchantPromotion.isPresent()) {
            MerchantPromotion merchantPromotion1 = merchantPromotion.get();
            if(merchantPromotion1.getAppUser().getUserId() == appUser.getUserId()) {
                double temp = merchantPromotion1.getOriginalPrice()*((double) (100 - discount) /100);
                merchantPromotion1.setDiscount(discount);
                Product product = productService.getProductById(merchantPromotion1.getProduct().getProductId()).get();
                product.setPrice((int) temp);
                productService.updateProduct(product);
                merchantPromotionService.saveMerchantPromotion(merchantPromotion1);
                return ResponseEntity.ok("Promotion successfully updated");
            }
            return ResponseEntity.badRequest().body("You are not the owner of this promotion");
        }
        return ResponseEntity.badRequest().body("Promotion is not valid");
    }

    @DeleteMapping("/removeMerchantPromotion/{mp_id}")
    public ResponseEntity<Void> removeMerchantPromotion(@AuthenticationPrincipal UserDetails user,@PathVariable long mp_id) {
        Optional<MerchantPromotion> merchantPromotion = merchantPromotionService.getMerchantPromotionById(mp_id);
        if(merchantPromotion.isPresent()) {
            MerchantPromotion merchantPromotion1 = merchantPromotion.get();
            if(merchantPromotion1.getAppUser().getUserId() == appUserService.getUserByUsername(user.getUsername()).get().getUserId()) {
                Product product = productService.getProductById(merchantPromotion1.getProduct().getProductId()).get();
                product.setPrice(merchantPromotion1.getOriginalPrice());
                productService.updateProduct(product);
                merchantPromotionService.removeMP(mp_id);
            }
        }
        return ResponseEntity.noContent().build();
    }





}
