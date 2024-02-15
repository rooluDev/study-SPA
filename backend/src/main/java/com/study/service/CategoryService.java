package com.study.service;

import com.study.dto.CategoryDTO;
import com.study.exception.CategoryNotFoundException;
import com.study.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 카테고리 관련 비지니스 로직
 */
@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper){
        this.categoryMapper = categoryMapper;
    }

    /**
     * DB에 있는 Category 전부 가져오기
     * @return
     */
    public List<CategoryDTO> getCategoryList(){
        return categoryMapper.getCategoryList();
    }

    /**
     * pk로 Category 하나 가져오기
     * @param categoryId
     * @return
     */
    public CategoryDTO getCategory(Long categoryId) throws Exception{
        CategoryDTO categoryDTO = categoryMapper.findById(categoryId);
        if(categoryDTO == null){
            throw new CategoryNotFoundException("No category in DB");
        }
        return categoryDTO;
    }
}
