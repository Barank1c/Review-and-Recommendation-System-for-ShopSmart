package hbg.rrssbackend.service;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.model.ProductPurchase;
import hbg.rrssbackend.model.UserReward;
import hbg.rrssbackend.repository.ProductPurchaseRepository;
import hbg.rrssbackend.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProductPurchaseService {

    ProductPurchaseRepository productPurchaseRepository;

    public ProductPurchaseService(ProductPurchaseRepository productPurchaseRepository) {
        this.productPurchaseRepository = productPurchaseRepository;
    }

    public boolean getUserBoughtProduct(long userId,long productId){
        List<ProductPurchase> productPurchase = productPurchaseRepository.getUserBoughtProduct(userId,productId);
        return !productPurchase.isEmpty();
    }

    public List<Long> getMostPopularProducts() {
        return productPurchaseRepository.findMostPopularProducts();
    }


    @Transactional
    public List<Long> findUserProductsByPage(int page, int pageSize, long userId) {
        int offset = (page - 1) * pageSize;
        return productPurchaseRepository.findUserProductsByPage(offset, pageSize, userId);
    }

    public int getUserProductsCount(long userId) {
        return productPurchaseRepository.getUserProductsCount(userId);
    }

    public void buyProduct(long userId,long productId,int price){
        productPurchaseRepository.buyProduct(userId,productId,price);
    }
}
