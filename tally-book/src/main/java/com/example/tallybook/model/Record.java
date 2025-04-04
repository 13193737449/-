package com.example.tallybook.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 记账记录模型
 */
@Data
public class Record {
    /**
     * 记录ID
     */
    private Long recordId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 分类编码
     */
    private String category;
    
    /**
     * 金额
     */
    private BigDecimal amount;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 记账时间
     */
    private LocalDateTime recordTime;
    
    /**
     * 年份
     */
    private Integer year;
    
    /**
     * 月份
     */
    private Integer month;
    
    /**
     * 日期
     */
    private Integer day;
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 