package com.study.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * pagination 관련 api controller
 */
@RequestMapping("/api")
@RestController
public class PaginationController {
    @Value("#{pagination['pagination.pageSize']}")
    private int pageSize;

    /**
     * pageSize Get 요청
     * @return
     */
    @GetMapping("/pageSize")
    public ResponseEntity<Integer> getPageSize(){
        return new ResponseEntity<>(pageSize, HttpStatus.OK);
    }
}
