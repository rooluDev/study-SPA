package com.study.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.condition.BoardSelectCondition;
import com.study.condition.SearchCondition;
import com.study.dto.BoardDTO;
import com.study.exception.BoardNotFoundException;
import com.study.dto.BoardSearchConditionDTO;
import com.study.service.BoardService;
import com.study.service.CategoryService;
import com.study.service.CommentService;
import com.study.service.FileService;
import com.study.utils.EncryptUtils;
import com.study.utils.StringUtils;
import com.study.validate.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * board rest api controller
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class BoardController {
    @Value("#{pagination['pagination.pageSize']}")
    private int pageSize;
    private final BoardService boardService;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final CommentService commentService;

    @Autowired
    public BoardController(BoardService boardService, CategoryService categoryService, FileService fileService, CommentService commentService) {
        this.boardService = boardService;
        this.categoryService = categoryService;
        this.fileService = fileService;
        this.commentService = commentService;
    }

    /**
     * 검색 조건에 따른 게시물 리스트 요청
     *
     * @param searchCondition
     * @param pageNum
     * @return
     */
    @GetMapping("/boards")
    public ResponseEntity<BoardSearchConditionDTO> getBoardList(SearchCondition searchCondition, @RequestParam(defaultValue = "1") int pageNum) {
        // 처음 /boards/free/list 검색 조건 설정
        if (StringUtils.isSearchConditionNull(searchCondition)) {
            searchCondition = new SearchCondition();
        }

        // 페이지네이션 정보 설정
        int startRow = (pageNum - 1) * pageSize;

        // boardSelectCondition 설정
        BoardSelectCondition boardSelectCondition = BoardSelectCondition.builder()
                .startDate(StringUtils.parseToTimestampStart(searchCondition.getStartDate()))
                .endDate(StringUtils.parseToTimestampEnd(searchCondition.getEndDate()))
                .categoryId(searchCondition.getCategoryId())
                .searchText(searchCondition.getSearchText())
                .pageSize(pageSize)
                .startRow(startRow)
                .build();


        // 필요한 정보 가져오기
        BoardSearchConditionDTO boardSearchConditionDTO = BoardSearchConditionDTO.builder()
                .searchCondition(searchCondition)
                .boardList(boardService.getBoardList(boardSelectCondition))
                .build();

        return new ResponseEntity<>(boardSearchConditionDTO,HttpStatus.OK);
    }

    /**
     * 페이지네이션 상관없이 게시물 수 요청
     * @param searchCondition
     * @return
     */
    @GetMapping("/boards/count")
    public ResponseEntity<Integer> getBoardsCount(SearchCondition searchCondition){
        // 처음 /boards/free/list 검색 조건 설정
        if (StringUtils.isSearchConditionNull(searchCondition)) {
            searchCondition = new SearchCondition();
        }

        BoardSelectCondition boardSelectCondition = BoardSelectCondition.builder()
                .startDate(StringUtils.parseToTimestampStart(searchCondition.getStartDate()))
                .endDate(StringUtils.parseToTimestampEnd(searchCondition.getEndDate()))
                .categoryId(searchCondition.getCategoryId())
                .searchText(searchCondition.getSearchText())
                .build();

        int count = boardService.getBoardCount(boardSelectCondition);

        return new ResponseEntity<>(count,HttpStatus.OK);
    }

    /**
     * 게시물 요청
     *
     * @param boardId
     * @return
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long boardId) {
        try {
            // 필요한 정보 요청
            BoardDTO boardDTO = boardService.findBoard(boardId);
            return new ResponseEntity<>(boardDTO,HttpStatus.OK);
        }catch (BoardNotFoundException e){
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * 게시물 생성
     *
     * @param files
     * @param categoryId
     * @param userName
     * @param password
     * @param passwordCheck
     * @param title
     * @param content
     * @throws IOException
     */
    @PostMapping("/board")
    public ResponseEntity<String> uploadBoard(@RequestParam(value = "files", required = false) MultipartFile[] files,
                            @RequestParam("categoryId") Long categoryId,
                            @RequestParam("userName") String userName,
                            @RequestParam("password") String password,
                            @RequestParam("passwordCheck") String passwordCheck,
                            @RequestParam("title") String title,
                            @RequestParam("content") String content) {

        // 저장할 Board DTO 생성
        BoardDTO boardDTO = BoardDTO.builder()
                .categoryId(categoryId)
                .userName(userName)
                .password(password)
                .title(title)
                .content(content)
                .build();

        // 유효성 검사
        try {
            Validator.validateBoardInput(boardDTO, passwordCheck);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }
        boardDTO.setPassword(EncryptUtils.encryptPassword(password));
        // DB에 board 저장
        boardService.addBoard(boardDTO);

        // File 저장
        try {
            fileService.uploadFile(files, boardDTO.getBoardId());
        }catch (IOException e){
            log.error(e.getMessage());
            return new ResponseEntity<>("File Upload Fail",HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>("success",HttpStatus.OK);
    }

    /**
     * 게시물 조회수 증가
     *
     * @param boardId
     */
    @PatchMapping("/board/{boardId}/increase-views")
    public ResponseEntity<String> updateView(@PathVariable Long boardId) {
        boardService.increaseView(boardId);
        return new ResponseEntity<>("success",HttpStatus.OK);
    }

    /**
     * 비밀번호 확인
     *
     * @param boardId
     * @param password
     * @return
     */
    @PostMapping("/board/{boardId}/check-password")
    public ResponseEntity<String> checkPassword(@PathVariable Long boardId, @RequestBody String password) throws Exception {
        // boardId로 비밀번호 가져오기
        BoardDTO boardDTO = boardService.findBoard(boardId);

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(password);
        password = jsonNode.get("password").asText();

        // 비밀번호 확인
        password = EncryptUtils.encryptPassword(password);
        if (!boardDTO.getPassword().equals(password)) {
            return new ResponseEntity<>("password incorrect", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("success", HttpStatus.ACCEPTED);
    }

    /**
     * 게시물 삭제
     * @param boardId
     * @return
     */
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        // 삭제
        commentService.deleteByBoardId(boardId);
        fileService.deleteFiles(boardId);
        boardService.deleteBoardById(boardId);

        return new ResponseEntity<>("success", HttpStatus.NO_CONTENT);
    }

    /**
     * 게시물 업데이트
     * @param boardId
     * @param updateBoard
     * @return
     * @throws JsonProcessingException
     */
    @PutMapping("/board/{boardId}")
    public ResponseEntity<String> updateBoard(@PathVariable Long boardId, @RequestBody String updateBoard){
        // JSON 파싱
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(updateBoard);
            String userName = jsonNode.get("userName").asText();
            String title = jsonNode.get("title").asText();
            String content = jsonNode.get("content").asText();

            // boardDTO 설정
            BoardDTO board = BoardDTO.builder()
                    .boardId(boardId)
                    .userName(userName)
                    .title(title)
                    .content(content)
                    .build();

            // 업데이트
            boardService.updateBoard(board);

            return new ResponseEntity<>("success", HttpStatus.OK);
        }catch (JsonProcessingException e){
            return new ResponseEntity<>("JSON data parse",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
