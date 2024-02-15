package com.study.condition;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 웹페이지에서 Board 검색 조건
 */
@Getter
@Setter
public class SearchCondition {
    private String startDate;
    private String endDate;
    private int categoryId;
    private String searchText;

    public SearchCondition() {
        startDate = getOneYearAgoTime();
        endDate = getCurrentTime();
        categoryId = -1;
        searchText = "";
    }

    /**
     * 현재시간 받기
     * @return
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 1년전 시간 받기
     * @return
     */
    private String getOneYearAgoTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }
}
