package hbg.rrssbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import hbg.rrssbackend.model.Product;
import hbg.rrssbackend.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Optional<Product> getProductById(long productId) {
        return productRepository.findById(productId);
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }




    public void removeProduct(long productId) {
        productRepository.deleteById(productId);
    }

    public void updateProduct(Product updatedProduct) {
        productRepository.save(updatedProduct);
    }


    @Transactional
    public List<Product> getProductsByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return productRepository.findProductsByPage(offset, pageSize);
    }

    @Transactional
    public List<Product> getMerchantProductsByPage(int page, int pageSize,long userId) {
        int offset = (page - 1) * pageSize;
        return productRepository.findMerchantProductsByPage(offset, pageSize, userId);
    }


    public int getTotalProductCount() {
        return productRepository.getTotalProductCount();
    }

    public int getMerchantProductCount(long userId) {
        return productRepository.getMerchantProductCount(userId);
    }

    public int findSearchedProductByPageCount(String keyword) {
        String newKeyword = "%" + keyword + "%";
        return productRepository.findSearchedProductByPageCount(newKeyword);
    }


    @Transactional
    public List<Product> findSearchedProductByPage(int page, int pageSize,String keyword) {
        int offset = (page - 1) * pageSize;
        String newKeyword = "%" + keyword + "%";
        return productRepository.findSearchedProductByPage(offset, pageSize,newKeyword);
    }

}
