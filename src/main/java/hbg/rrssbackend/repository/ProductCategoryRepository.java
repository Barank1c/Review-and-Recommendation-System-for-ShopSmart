package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryID> {

    @Query("SELECT u.productId FROM ProductCategory u where u.categoryId = :categoryId")
    List<Long> getAllProductsOfCategory(long categoryId);


    @Query(value = "SELECT product_id FROM product_category  where category_id = :categoryId ORDER BY product_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<Long> getPagedProductsOfCategory(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("categoryId") long categoryId);

    @Query("SELECT COUNT(p) FROM ProductCategory p where p.categoryId = :categoryId")
    int getPagedProductsOfCategoryCount(long categoryId);


    @Transactional
    @Modifying
    @Query(value = "insert into product_category (category_id,product_id) VALUES (:categoryId,:productId)",nativeQuery = true)
    void addCategoryToProduct(long categoryId, long productId);

    @Transactional
    @Modifying
    @Query(value = "delete from product_category where product_id = :productId",nativeQuery = true)
    void removeCategoriesOfProduct(long productId);

}