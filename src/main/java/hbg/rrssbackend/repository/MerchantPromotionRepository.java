package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.MerchantPromotion;
import hbg.rrssbackend.model.PQUpvote;
import hbg.rrssbackend.model.PQUpvoteID;
import hbg.rrssbackend.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface MerchantPromotionRepository extends JpaRepository<MerchantPromotion, Long> {

    @Query(value = "SELECT * FROM merchant_promotion p where p.user_id = :userId ORDER BY p.mp_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<MerchantPromotion> findMerchantPromotionsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("userId") long userId);


    @Query("SELECT COUNT(p) FROM MerchantPromotion p where p.appUser.userId = :userId")
    int getMerchantPromotionCount(long userId);


    @Query("SELECT p FROM MerchantPromotion p where p.product.productId = :productId")
    MerchantPromotion isProductHasPromotion(long productId);
}
