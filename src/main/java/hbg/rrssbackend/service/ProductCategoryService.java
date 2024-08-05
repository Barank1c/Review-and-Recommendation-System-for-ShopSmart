package hbg.rrssbackend.service;

import hbg.rrssbackend.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<Long> getAllProductsOfCategory(long categoryId){
        return productCategoryRepository.getAllProductsOfCategory(categoryId);
    }

    public List<Long> getPagedProductsOfCategory(int page,int pageSize,long categoryId){
        int offset = (page - 1) * pageSize;
        return productCategoryRepository.getPagedProductsOfCategory( offset, pageSize,categoryId);
    }

    public int getPagedProductsOfCategoryCount(long categoryId) {
        return productCategoryRepository.getPagedProductsOfCategoryCount(categoryId);
    }

    public void addCategoriesToProduct(List<Long> categoryIds,long productId){
        productCategoryRepository.removeCategoriesOfProduct(productId);
        for(Long i : categoryIds){
            if(i!=null) productCategoryRepository.addCategoryToProduct(i,productId);
        }
    }

}
