package hbg.rrssbackend.service;

import hbg.rrssbackend.model.MerchantPromotion;
import hbg.rrssbackend.repository.MerchantPromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantPromotionService {
    private final MerchantPromotionRepository merchantPromotionRepository;

    public MerchantPromotionService(MerchantPromotionRepository merchantPromotionRepository){
        this.merchantPromotionRepository = merchantPromotionRepository;
    }

    @Transactional
    public List<MerchantPromotion> getMerchantPromotionsByPage(int page, int pageSize, long userId) {
        int offset = (page - 1) * pageSize;
        return merchantPromotionRepository.findMerchantPromotionsByPage(offset, pageSize, userId);
    }

    public int getMerchantPromotionCount(long userId) {
        return merchantPromotionRepository.getMerchantPromotionCount(userId);
    }

    public void saveMerchantPromotion(MerchantPromotion merchantPromotion) {
        merchantPromotionRepository.save(merchantPromotion);
    }

    public boolean isProductHasPromotion(long productId) {
        MerchantPromotion merchantPromotion = merchantPromotionRepository.isProductHasPromotion(productId);
        return merchantPromotion != null;
    }

    public Long ProductsPromotion(long productId) {
        MerchantPromotion merchantPromotion = merchantPromotionRepository.isProductHasPromotion(productId);
        if(merchantPromotion == null){
            return null;
        }
        return merchantPromotion.getMpId();
    }

    public MerchantPromotion productsMerchantPromotion(long productId) {
        return merchantPromotionRepository.isProductHasPromotion(productId);
    }

    public Optional<MerchantPromotion> getMerchantPromotionById(long mpId) {
        return merchantPromotionRepository.findById(mpId);
    }

    public void removeMP(long mpId) {
        merchantPromotionRepository.deleteById(mpId);
    }

}
