package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {


    @Query("SELECT u FROM WishList u where u.wishListName = :wishListName and u.appUser.userId = :userId")
    Optional<WishList> findByWLName(String wishListName,Long userId);

    @Query("SELECT u FROM WishList u where u.wishListId = :wishListId")
    Optional<WishList> findByWLID(long wishListId);

    @Query("SELECT u FROM WishList u where u.appUser.userId = :userId")
    Optional<List<WishList>> getAllWishListsOfUser(long userId);

}
