package hbg.rrssbackend.service;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.WishList;
import hbg.rrssbackend.model.WishListItem;
import hbg.rrssbackend.model.WishListItemID;
import hbg.rrssbackend.repository.WishListItemRepository;
import hbg.rrssbackend.repository.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishListItemService {
    private final WishListItemRepository wishListItemRepository;

    public WishListItemService(WishListItemRepository wishListItemRepository) {
        this.wishListItemRepository = wishListItemRepository;
    }


    public List<Long> getProductsOfWL(long wishListId) {
        return wishListItemRepository.getProductsOfWL(wishListId);
    }

    public void addProductToWL(long wishListId,long productId) {
        wishListItemRepository.addProductToWL(wishListId,productId);
    }

    public Optional<WishListItem> getWLItembyID(long wishListId, long productId) {
        return wishListItemRepository.findById(new WishListItemID(productId,wishListId));
    }

    public void deleteProductFromWL(long wishListId,long productId) {
        wishListItemRepository.deleteProductFromWL(wishListId,productId);
    }


}
