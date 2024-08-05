package hbg.rrssbackend.mapper;

import hbg.rrssbackend.dto.CategoryDto;
import hbg.rrssbackend.dto.CommunityEventDto;
import hbg.rrssbackend.model.Category;
import hbg.rrssbackend.model.CommunityEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CategoryMapper {
    public CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        return dto;
    }

    public Category fromDto(CategoryDto dto) {
        if (dto == null) {
            return null;
        }
        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        category.setCategoryName(dto.getCategoryName());
        return category;
    }
}
