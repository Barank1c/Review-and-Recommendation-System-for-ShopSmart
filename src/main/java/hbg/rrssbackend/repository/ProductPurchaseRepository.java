package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.ProductPurchase;
import hbg.rrssbackend.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, Long> {

    @Query(value = "SELECT p from ProductPurchase p where p.appUser.userId = :userId and p.product.productId = :productId")
    List<ProductPurchase> getUserBoughtProduct(long userId,long productId);

    @Query(value = "SELECT pp.product.productId from ProductPurchase pp where DATE_TRUNC('month', pp.PPTime) = DATE_TRUNC('month', CURRENT_DATE)   group by pp.product.productId order by   count (pp) desc ")
    List<Long> findMostPopularProducts();

    @Query(value = "SELECT p.product_id FROM product_purchase p where p.user_id = :userId ORDER BY p.ppid OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<Long> findUserProductsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("userId") long userId);

    @Query("SELECT COUNT(p) FROM ProductPurchase p where p.appUser.userId = :userId")
    int getUserProductsCount(long userId);

    @Transactional
    @Modifying
    @Query(value = "insert into product_purchase (user_id,product_id,cost,pptime) VALUES (:userId,:productId,:price,CURRENT_TIMESTAMP)",nativeQuery = true)
    void buyProduct(long userId,long productId,int price);
}
