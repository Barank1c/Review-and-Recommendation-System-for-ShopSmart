package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.WishListDto;
import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.WishList;
import hbg.rrssbackend.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class WishListMapper {

    AppUserService appUserService;

    public  WishListDto wishListToWishListDTO(WishList wishList) {
        if (wishList == null) {
            return null;
        }
        WishListDto wishListDto = new WishListDto();

        wishListDto.setWishListId(wishList.getWishListId());
        wishListDto.setUserId(wishList.getAppUser().getUserId());
        wishListDto.setWishListName(wishList.getWishListName());
        wishListDto.setCreateTime(wishList.getCreateTime());
        return wishListDto;
    }
    public  WishList wishListDTOToWishList(WishListDto wishListDto) {
        if (wishListDto == null) {
            return null;
        }
        WishList wishList = new WishList();
        wishList.setWishListId(wishListDto.getWishListId());
        Optional<AppUser> user = appUserService.getUserById(wishListDto.getUserId());
        if (user.isPresent()) {
            AppUser user1 = user.get();
            wishList.setAppUser(user1);
        } else {
            throw new UsernameNotFoundException("No user present");
        }
        wishList.setWishListName(wishListDto.getWishListName());
        wishList.setCreateTime(wishListDto.getCreateTime());
        return wishList;
    }
}
