package com.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 게시판 목록에 필요한 정보들
 */
@Getter
@Setter
@Builder
public class BoardCategoryFileDTO {
    private String categoryName;
    private Long boardId;
    private String title;
    private String userName;
    private Long views;
    private Timestamp createdAt;
    private Timestamp editedAt;
    private Long boardIdInFile;
}


