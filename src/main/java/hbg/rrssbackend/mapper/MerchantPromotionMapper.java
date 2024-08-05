package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CPCommentsDto;
import hbg.rrssbackend.dto.MerchantPromotionDto;
import hbg.rrssbackend.model.*;
import hbg.rrssbackend.service.AppUserService;
import hbg.rrssbackend.service.CommunityPostsService;
import hbg.rrssbackend.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor

public class MerchantPromotionMapper {
    ProductService productService;
    AppUserService appUserService;

    public MerchantPromotionDto toDto(MerchantPromotion merchantPromotion) {
        if (merchantPromotion == null) {
            return null;
        }
        MerchantPromotionDto dto = new MerchantPromotionDto();
        dto.setDiscount(merchantPromotion.getDiscount());
        dto.setMpId(merchantPromotion.getMpId());
        dto.setOriginalPrice(merchantPromotion.getOriginalPrice());
        dto.setMpCreateTime(merchantPromotion.getMpCreateTime());
        dto.setUserId(merchantPromotion.getAppUser() != null ? merchantPromotion.getAppUser().getUserId() : null);
        dto.setUsername(merchantPromotion.getAppUser() != null ? merchantPromotion.getAppUser().getUsername() : null);
        dto.setProductId(merchantPromotion.getProduct().getProductId());
        return dto;
    }

    public  MerchantPromotion fromDto(MerchantPromotionDto dto) {
        if (dto == null) {
            return null;
        }

        MerchantPromotion merchantPromotion = new MerchantPromotion();
        merchantPromotion.setDiscount(dto.getDiscount());
        merchantPromotion.setMpId(dto.getMpId());
        merchantPromotion.setMpCreateTime(dto.getMpCreateTime());
        merchantPromotion.setOriginalPrice(dto.getOriginalPrice());

        Optional<AppUser> user = appUserService.getUserById(dto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            merchantPromotion.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        Optional<Product> product = productService.getProductById(dto.getProductId());
        if (product.isPresent()) {
            merchantPromotion.setProduct(product.get());
        }
        else {
            merchantPromotion.setProduct(null);
        }
        return merchantPromotion;
    }

}
