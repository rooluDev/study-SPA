package com.study.mapper;

import com.study.condition.BoardSelectCondition;
import com.study.dto.BoardCategoryFileDTO;
import com.study.dto.BoardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Board mapper
 */
@Mapper
public interface BoardMapper {
    /**
     * 검색조건과 페이지네이션에 따라 목록에 보여줄 데이터 검색
     * @param boardSelectCondition
     * @return
     */
    List<BoardCategoryFileDTO> getBoardList(BoardSelectCondition boardSelectCondition);

    /**
     * 검색조건에 따른 전체 Board rowcount 가져오기
     * @param boardSelectCondition
     * @return
     */
    int getBoardCount(BoardSelectCondition boardSelectCondition);

    /**
     * pk로 board 데이터 가져오기
     * @param boardId
     * @return
     */
    BoardDTO findById(Long boardId);

    /**
     * board 추가
     * @param boardDTO
     */
    void createBoard(BoardDTO boardDTO);

    /**
     * view 1 증가
     * @param boardId
     */
    void updateView(Long boardId);

    /**
     * pk로 board 삭제
     * @param boardId
     */
    void deleteById(Long boardId);

    /**
     * board update
     * @param board
     */
    void updateBoard(BoardDTO board);
}
