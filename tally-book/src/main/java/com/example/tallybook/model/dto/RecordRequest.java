package com.example.tallybook.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 记账记录请求DTO
 */
@Data
public class RecordRequest {
    /**
     * 记录ID (更新时必填)
     */
    private Long recordId;
    
    /**
     * 分类编码
     */
    @NotNull(message = "分类不能为空")
    private String category;
    
    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 备注
     */
    @Size(max = 100, message = "备注最多100个字符")
    private String remark;
    
    /**
     * 记账时间, 格式: yyyy-MM-dd HH:mm:ss
     */
    @NotNull(message = "记账时间不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "记账时间格式错误")
    private String recordTime;
} 