package com.example.tallybook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 总页数
     */
    private int pages;
    
    /**
     * 当前页码
     */
    private int page;
    
    /**
     * 每页大小
     */
    private int size;
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 创建分页响应
     */
    public static <T> PageResponse<T> of(long total, int page, int size, List<T> list) {
        int pages = (int) Math.ceil((double) total / size);
        return new PageResponse<>(total, pages, page, size, list);
    }
} 