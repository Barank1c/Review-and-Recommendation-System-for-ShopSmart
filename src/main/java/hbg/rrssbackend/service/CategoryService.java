package hbg.rrssbackend.service;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Category;
import hbg.rrssbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Optional<Category> getCategoryById(long id) {
        return categoryRepository.findById(id);
    }


    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }


}
