package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.ProductQuestions;
import hbg.rrssbackend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductQuestionsRepository extends JpaRepository<ProductQuestions, Long> {

    @Query("SELECT COUNT(p) FROM ProductQuestions p where p.product.productId = :productId")
    int getTotalPQCount(long productId);

    @Query(value = "SELECT * FROM product_questions r where r.product_id = :productId ORDER BY r.q_time OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<ProductQuestions> findPQByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("productId") long productId);

}
