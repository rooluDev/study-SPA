package com.study.service;

import com.study.condition.BoardSelectCondition;
import com.study.dto.BoardCategoryNameDTO;
import com.study.dto.BoardDTO;
import com.study.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 게시물 관련 비지니스 로직
 */
@Service
public class BoardService {
    private final BoardMapper boardMapper;

    @Autowired
    public BoardService(BoardMapper boardMapper){
        this.boardMapper = boardMapper;
    }

    /**
     * 게시물 찾기
     * @param boardId
     * @return
     */
    public BoardDTO findBoard(long boardId){
        return boardMapper.findById(boardId);
    }

    /**
     * 검색조건에 따라 게시물들 찾기
     * @param boardSelectCondition
     * @return
     */
    public List<BoardCategoryNameDTO> getBoardList(BoardSelectCondition boardSelectCondition){
        return boardMapper.getBoardList(boardSelectCondition);
    }

    /**
     * 검색조건에 따라 게시물 총 수
     * @param boardSelectCondition
     * @return
     */
    public int getBoardCount(BoardSelectCondition boardSelectCondition){
        return boardMapper.getBoardCount(boardSelectCondition);
    }

    /**
     * 게시물 추가
     * @param board
     * @return
     */
    public void addBoard(BoardDTO board){
        boardMapper.createBoard(board);
    }

    /**
     * 게시물 조회수 증가
     * @param boardId
     */
    public void increaseView(Long boardId){
        boardMapper.updateView(boardId);
    }

    /**
     * 게시물 삭제
     * @param boardId
     */
    public void deleteBoardById(Long boardId){
        boardMapper.deleteById(boardId);
    }

    /**
     * 게시물 수정
     * @param board
     */
    public void updateBoard(BoardDTO board){
        boardMapper.updateBoard(board);
    }
}
