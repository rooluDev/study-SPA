package com.study.mapper;

import com.study.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * boardId로 댓글 가져오기
     * @param boardId
     * @return
     */
    List<CommentDTO> findByBoardId(Long boardId);

    /**
     * 댓글 등록
     * @param boardId
     * @param comment
     */
    void createComment(@Param("boardId") Long boardId, @Param("comment") String comment);

    /**
     * boardId가 일치하는 댓글 삭제
     * @param boardId
     */
    void deleteByBoardId(Long boardId);

}
