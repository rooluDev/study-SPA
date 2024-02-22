package com.study.controller;

import com.study.dto.FileDto;
import com.study.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * file rest api controller
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 첨부파일 클릭 시 파일 다운로드
     *
     * @param fileId
     * @return
     * @throws IOException
     */
    @GetMapping("/file/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws Exception {
        // 파일 정보 가져오기
        FileDto file = fileService.findByFileId(fileId);

        // 파일 정보 설정
        String filePathString = file.getFilePath() + file.getPhysicalName() + "." + file.getExtension();
        File filePath = Paths.get(filePathString).toFile();
        Resource resource = new InputStreamResource(Files.newInputStream(filePath.toPath()));

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getName() + "\"")
                .body(resource);
    }

    /**
     * 게시물에 첨부되어있는 파일 리스트 요청
     *
     * @param boardId
     * @return
     */
    @GetMapping("/files/{boardId}")
    public ResponseEntity<List<FileDto>> getFileList(@PathVariable Long boardId) {
        List<FileDto> fileList = fileService.findFileListByBoardId(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(fileList);
    }

    /**
     * 파일 삭제
     *
     * @param fileId
     * @return
     */
    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFileByFileId(fileId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("success");
    }
}
