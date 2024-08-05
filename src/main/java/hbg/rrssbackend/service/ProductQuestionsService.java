package hbg.rrssbackend.service;

import hbg.rrssbackend.model.ProductQuestions;
import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.repository.ProductQuestionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductQuestionsService {

    private final ProductQuestionsRepository productQuestionsRepository;

    public ProductQuestionsService(ProductQuestionsRepository productQuestionsRepository) {
        this.productQuestionsRepository = productQuestionsRepository;
    }

    public void savePQ(ProductQuestions productQuestions) {
        productQuestionsRepository.save(productQuestions);
    }

    public Optional<ProductQuestions> getPQById(long pqId) {
        return productQuestionsRepository.findById(pqId);
    }

    public void removeQ(long pqId) {
        productQuestionsRepository.deleteById(pqId);
    }

    @Transactional
    public List<ProductQuestions> getPQByPage(int page, int pageSize, long productId) {
        int offset = (page - 1) * pageSize;
        return productQuestionsRepository.findPQByPage(offset, pageSize, productId);
    }

    public int getTotalPQCount(long productId) {
        return productQuestionsRepository.getTotalPQCount( productId);
    }


}
