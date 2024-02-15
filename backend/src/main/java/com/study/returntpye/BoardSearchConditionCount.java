package com.study.returntpye;

import com.study.condition.SearchCondition;
import com.study.dto.BoardCategoryNameDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 게시판 - 목록에 필요한 정보들
 */
@Getter
@Setter
@Builder
public class BoardSearchConditionCount {
    private List<BoardCategoryNameDTO> boardList;
    private SearchCondition searchCondition;
    private int countRow;
}
