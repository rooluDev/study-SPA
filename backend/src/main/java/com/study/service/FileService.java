package com.study.service;

import com.study.dto.FileDTO;
import com.study.mapper.FileMapper;
import com.study.utils.MultipartFileUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * 파일 관련 비지니스 로직
 */
@Service
public class FileService {
    private final FileMapper fileMapper;
    static final String REAL_PATH = "/Users/user/upload/";

    @Autowired
    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * board와 관련된 file들 가져오기
     * @param boardId
     * @return
     */
    public List<FileDTO> getFiles(Long boardId){
        return fileMapper.findByBoardId(boardId);
    }

    /**
     * File Upload
     *
     * @param multipartFiles
     * @param boardId
     */
    public void uploadFile(MultipartFile[] multipartFiles, Long boardId) throws IOException {
        if(multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (!multipartFile.isEmpty()) {
                    // File DTO 생성
                    FileDTO file = FileDTO.builder()
                            .boardId(boardId)
                            .originalName(multipartFile.getOriginalFilename())
                            .physicalName(UUID.randomUUID().toString())
                            .filePath(REAL_PATH)
                            .extension(MultipartFileUtils.extractExtension(multipartFile.getOriginalFilename()))
                            .size(multipartFile.getSize())
                            .build();

                    // Server 저장
                    String filePath = REAL_PATH + file.getPhysicalName() + "." + file.getExtension();
                    File uploadedFile = new File(filePath);
                    FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), uploadedFile);

                    // File DB Add
                    fileMapper.createFile(file);
                }
            }
        }
    }

    /**
     * 게시물의 등록된 파일 DB에서 삭제
     * @param boardId
     */
    public void deleteFiles(Long boardId){
        fileMapper.deleteByBoardId(boardId);
    }

    /**
     * 하나의 파일 삭제
     * @param fileId
     */
    public void deleteFileById(Long fileId){
        fileMapper.deleteById(fileId);
    }

    /**
     * pk로 파일 정보 가져오기
     * @param fileId
     * @return
     */
    public FileDTO findOne(Long fileId){
        return fileMapper.findById(fileId);
    }
}
