package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT * FROM Product p ORDER BY p.product_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<Product> findProductsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Query(value = "SELECT * FROM Product p where p.user_id = :userId ORDER BY p.product_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<Product> findMerchantProductsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("userId") long userId);

    @Query("SELECT COUNT(p) FROM Product p")
    int getTotalProductCount();

    @Query("SELECT COUNT(p) FROM Product p where p.appUser.userId = :userId")
    int getMerchantProductCount(long userId);

    @Query(value = "SELECT * FROM Product p where lower(p.product_name) like lower(:keyword) or " +
            "lower(p.description) like lower(:keyword)" +
            "ORDER BY p.product_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<Product> findSearchedProductByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("keyword") String keyword);

    @Query("SELECT COUNT(p) FROM Product p where lower(p.productName) like lower(:keyword) or  lower(p.description) like lower(:keyword)")
    int findSearchedProductByPageCount(@Param("keyword") String keyword);



}
