package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WishListItemRepository extends JpaRepository<WishListItem, WishListItemID> {

    @Transactional
    @Query(value = "SELECT u.productId FROM WishListItem u where u.wishListId = :wishListId")
    List<Long> getProductsOfWL(long wishListId);

    @Transactional
    @Modifying
    @Query(value = "insert into wish_list_item (product_id,wish_list_id) VALUES (:productId,:wishListId)",nativeQuery = true)
    void addProductToWL(long wishListId,long productId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM wish_list_item  WHERE product_id = :productId AND wish_list_id = :wishListId",nativeQuery = true)
    void deleteProductFromWL(long wishListId,long productId);


}
