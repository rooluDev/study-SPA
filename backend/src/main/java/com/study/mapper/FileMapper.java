package com.study.mapper;

import com.study.dto.FileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    /**
     * boardId로 files 찾기
     * @param boardId
     * @return
     */
    List<FileDTO> findByBoardId(Long boardId);

    /**
     * File Create
     * @param file
     */
    void createFile(FileDTO file);

    /**
     * board의 파일 삭제
     * @param boardId
     */
    void deleteByBoardId(Long boardId);

    /**
     * pk로 파일 찾기
     * @param fileId
     * @return
     */
    FileDTO findById(Long fileId);

    /**
     * pk로 파일 삭제
     * @param fileId
     */
    void deleteById(Long fileId);
}
