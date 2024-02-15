package com.study.mapper;

import com.study.dto.CategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Category Mapper
 */
@Mapper
public interface CategoryMapper {
    /**
     * DB에 있는 모든 카테고리 리스트 검색
     * @return
     */
    List<CategoryDTO> getCategoryList();

    /**
     * pk로 카테고리 검색
     * @param categoryId
     * @return
     */
    CategoryDTO findById(Long categoryId);
}
