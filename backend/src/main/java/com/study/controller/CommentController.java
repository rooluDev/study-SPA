package com.study.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dto.CommentDTO;
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
    public ResponseEntity<List<CommentDTO>> getCommentList(@PathVariable Long boardId) {
        return new ResponseEntity<>(commentService.getComments(boardId), HttpStatus.OK);
    }

    /**
     * 댓글 등록
     * @param commentData
     */
    @PostMapping("/comment")
    public ResponseEntity<String> registerComment(@RequestBody String commentData) throws JsonProcessingException {
        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(commentData);
        Long boardId = jsonNode.get("boardId").asLong();
        String comment = jsonNode.get("comment").asText();

        commentService.registerComment(boardId,comment);
        return new ResponseEntity<>("success",HttpStatus.OK);
    }
}
