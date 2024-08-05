package hbg.rrssbackend.service;

import hbg.rrssbackend.model.WishList;
import hbg.rrssbackend.repository.WishListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WishListService {
    private final WishListRepository wishListRepository;

    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    public List<WishList> getAllWishLists() {
        return wishListRepository.findAll();
    }

    public Optional<List<WishList>> getAllWishListsOfUser(long userId) {
        return wishListRepository.getAllWishListsOfUser(userId);
    }


    public void addWL(WishList wishList) {
        wishListRepository.save(wishList);
    }


    public Optional<WishList> getWLByWLName(String wishListName,Long userId) {
        return wishListRepository.findByWLName(wishListName,userId);
    }

    public Optional<WishList> getWLByWLID(Long wishListId) {
        return wishListRepository.findByWLID(wishListId);
    }

    public void removeWL(long wishListId) {
        wishListRepository.deleteById(wishListId);
    }

    @Transactional
    public void saveWishList(WishList wishList) {
        wishListRepository.save(wishList);
    }



}
