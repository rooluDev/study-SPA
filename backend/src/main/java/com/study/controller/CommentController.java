package com.study.controller;

import com.study.dto.CommentCreateFormDto;
import com.study.dto.CommentDto;
import com.study.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * comment rest api controller
 */
@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 게시물에 등록된 댓글들 요청
     *
     * @param boardId
     * @return
     */
    @GetMapping("/comment/{boardId}")
    public ResponseEntity<List<CommentDto>> getCommentList(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getComments(boardId));
    }

    /**
     * 댓글 등록
     * @param commentCreateFormDto
     */
    @PostMapping("/comment")
    public ResponseEntity<String> registerComment(@RequestBody CommentCreateFormDto commentCreateFormDto){
        commentService.addComment(commentCreateFormDto);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
