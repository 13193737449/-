package com.example.tallybook.model.dto;

import lombok.Data;

/**
 * 记账记录列表请求DTO
 */
@Data
public class RecordListRequest {
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer size = 10;
    
    /**
     * 分类编码，用于按分类筛选
     */
    private String category;
    
    /**
     * 开始日期
     */
    private String startDate;
    
    /**
     * 结束日期
     */
    private String endDate;
} 