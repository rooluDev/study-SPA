package com.study.service;

import com.study.dto.CommentDTO;
import com.study.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 댓글 관련 비지니스 로직
 */
@Service
public class CommentService {
    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentMapper commentMapper){
        this.commentMapper = commentMapper;
    }

    /**
     * 댓글 가져오기
     * @param boardId
     * @return
     */
    public List<CommentDTO> getComments(Long boardId){
        return commentMapper.findByBoardId(boardId);
    }

    /**
     * 댓글 등록
     * @param boardId
     * @param comment
     */
    public void registerComment(Long boardId, String comment){
        commentMapper.createComment(boardId, comment);
    }

    /**
     * 댓글 모두 삭제
     */
    public void deleteByBoardId(Long boardId){
        commentMapper.deleteByBoardId(boardId);
    }
}
