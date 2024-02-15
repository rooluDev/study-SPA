package com.study.controller;

import com.study.dto.FileDTO;
import com.study.service.FileService;
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
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    /**
     * 파일 다운로드
     * @param fileId
     * @return
     * @throws IOException
     */
    @GetMapping("/file/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {
        // 파일 정보 가져오기
        FileDTO file = fileService.findOne(fileId);
        // 파일 정보 설정
        String filePathString = file.getFilePath() + file.getPhysicalName()+"."+file.getExtension();
        File filePath = Paths.get(filePathString).toFile();
        Resource resource = new InputStreamResource(Files.newInputStream(filePath.toPath()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getName() + "\"")
                .body(resource);
    }

    @GetMapping("/files/{boardId}")
    public ResponseEntity<List<FileDTO>> getFileList(@PathVariable Long boardId){
        List<FileDTO> fileList = fileService.getFiles(boardId);
        return new ResponseEntity<>(fileList,HttpStatus.OK);
    }

    /**
     * 파일 삭제
     * @param fileId
     * @return
     */
    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId){
        fileService.deleteFileById(fileId);

        return new ResponseEntity<>("success",HttpStatus.NO_CONTENT);
    }


}
