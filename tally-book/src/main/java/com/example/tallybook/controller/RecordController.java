package com.example.tallybook.controller;

import com.example.tallybook.mapper.CategoryMapper;
import com.example.tallybook.mapper.RecordMapper;
import com.example.tallybook.model.Category;
import com.example.tallybook.model.Record;
import com.example.tallybook.model.dto.*;
import com.example.tallybook.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记账记录控制器
 */
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
@Slf4j
public class RecordController {
    
    private final RecordMapper recordMapper;
    private final CategoryMapper categoryMapper;
    private final JwtUtil jwtUtil;
    
    /**
     * 添加记账记录
     * @param requestMap 请求参数
     * @param httpRequest HTTP请求
     * @return 添加结果
     */
    @PostMapping("/add")
    public ApiResponse<?> addRecord(@Valid @RequestBody Map<String, Object> requestMap, HttpServletRequest httpRequest) {
        try {
            // 从JWT中获取用户ID
            String token = getTokenFromRequest(httpRequest);
            Integer userId = getUserIdFromToken(token);
            
            // 如果前端提供了userId，则使用前端提供的值覆盖JWT中的值
            if (requestMap.containsKey("user_id") && requestMap.get("user_id") != null) {
                userId = Integer.valueOf(requestMap.get("user_id").toString());
            }
            
            // 获取分类编码
            String categoryCode = (String) requestMap.get("category");
            if (categoryCode == null || categoryCode.isEmpty()) {
                return ApiResponse.error(400, "分类不能为空");
            }
            
            // 检查分类是否存在
            Category category = categoryMapper.findByCategoryCode(categoryCode);
            if (category == null) {
                return ApiResponse.error(400, "分类不存在");
            }
            
            // 创建记账记录
            Record record = new Record();
            record.setUserId(userId);
            record.setCategory(categoryCode); // 使用分类编码
            
            // 设置金额
            if (requestMap.containsKey("amount") && requestMap.get("amount") != null) {
                record.setAmount(new java.math.BigDecimal(requestMap.get("amount").toString()));
            } else {
                return ApiResponse.error(400, "金额不能为空");
            }
            
            // 设置备注
            if (requestMap.containsKey("description")) {
                record.setRemark((String) requestMap.get("description"));
            }
            
            // 解析记账时间
            if (requestMap.containsKey("record_time") && requestMap.get("record_time") != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                record.setRecordTime(LocalDateTime.parse((String) requestMap.get("record_time"), formatter));
            } else {
                // 默认为当前时间
                record.setRecordTime(LocalDateTime.now());
            }
            
            // 根据记账时间设置年、月、日
            if (record.getRecordTime() != null) {
                record.setYear(record.getRecordTime().getYear());
                record.setMonth(record.getRecordTime().getMonthValue());
                record.setDay(record.getRecordTime().getDayOfMonth());
            }
            
            // 保存记账记录
            int result = recordMapper.insert(record);
            if (result <= 0) {
                return ApiResponse.error(500, "添加失败，请稍后再试");
            }
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("record_id", record.getRecordId());
            
            return ApiResponse.success("添加成功", data);
        } catch (Exception e) {
            log.error("添加记账记录异常", e);
            return ApiResponse.error(500, "添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改记账记录
     * @param requestMap 请求参数
     * @param httpRequest HTTP请求
     * @return 修改结果
     */
    @PutMapping("/update")
    public ApiResponse<?> updateRecord(@Valid @RequestBody Map<String, Object> requestMap, HttpServletRequest httpRequest) {
        try {
            if (!requestMap.containsKey("record_id") || requestMap.get("record_id") == null) {
                return ApiResponse.error(400, "记录ID不能为空");
            }
            
            Long recordId = Long.valueOf(requestMap.get("record_id").toString());
            
            // 从JWT中获取用户ID
            String token = getTokenFromRequest(httpRequest);
            Integer userId = getUserIdFromToken(token);
            
            // 如果前端提供了userId，则使用前端提供的值覆盖JWT中的值
            if (requestMap.containsKey("user_id") && requestMap.get("user_id") != null) {
                userId = Integer.valueOf(requestMap.get("user_id").toString());
            }
            
            // 检查记录是否存在
            Record existingRecord = recordMapper.findById(recordId, userId);
            if (existingRecord == null) {
                return ApiResponse.error(404, "记录不存在");
            }
            
            // 获取分类编码并检查是否存在
            String categoryCode = (String) requestMap.get("category");
            if (categoryCode != null && !categoryCode.isEmpty()) {
                Category category = categoryMapper.findByCategoryCode(categoryCode);
                if (category == null) {
                    return ApiResponse.error(400, "分类不存在");
                }
            } else {
                categoryCode = existingRecord.getCategory(); // 使用原有分类
            }
            
            // 更新记账记录
            Record record = new Record();
            record.setRecordId(recordId);
            record.setUserId(userId);
            record.setCategory(categoryCode);
            
            // 设置金额
            if (requestMap.containsKey("amount") && requestMap.get("amount") != null) {
                record.setAmount(new java.math.BigDecimal(requestMap.get("amount").toString()));
            } else {
                record.setAmount(existingRecord.getAmount()); // 使用原有金额
            }
            
            // 设置备注
            if (requestMap.containsKey("description")) {
                record.setRemark((String) requestMap.get("description"));
            } else {
                record.setRemark(existingRecord.getRemark()); // 使用原有备注
            }
            
            // 解析记账时间
            if (requestMap.containsKey("record_time") && requestMap.get("record_time") != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                record.setRecordTime(LocalDateTime.parse((String) requestMap.get("record_time"), formatter));
            } else {
                record.setRecordTime(existingRecord.getRecordTime()); // 使用原有时间
            }
            
            // 根据记账时间设置年、月、日
            if (record.getRecordTime() != null) {
                record.setYear(record.getRecordTime().getYear());
                record.setMonth(record.getRecordTime().getMonthValue());
                record.setDay(record.getRecordTime().getDayOfMonth());
            }
            
            // 保存更新
            int result = recordMapper.update(record);
            if (result <= 0) {
                return ApiResponse.error(500, "修改失败，请稍后再试");
            }
            
            return ApiResponse.success("修改成功");
        } catch (Exception e) {
            log.error("修改记账记录异常", e);
            return ApiResponse.error(500, "修改失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除记账记录（软删除）
     * @param recordId 记录ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/delete/{recordId}")
    public ApiResponse<?> deleteRecord(@PathVariable Long recordId, HttpServletRequest httpRequest) {
        try {
            // 从JWT中获取用户ID
            String token = getTokenFromRequest(httpRequest);
            Integer userId = getUserIdFromToken(token);
            
            // 检查记录是否存在
            Record existingRecord = recordMapper.findById(recordId, userId);
            if (existingRecord == null) {
                return ApiResponse.error(404, "记录不存在");
            }
            
            // 软删除记录（标记为已删除）
            int result = recordMapper.softDelete(recordId, userId);
            if (result <= 0) {
                return ApiResponse.error(500, "删除失败，请稍后再试");
            }
            
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除记账记录异常", e);
            return ApiResponse.error(500, "删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取记账记录列表
     * @param request 请求参数
     * @param httpRequest HTTP请求
     * @return 记账记录列表
     */
    @GetMapping("/list")
    public ApiResponse<?> getRecords(RecordListRequest request, HttpServletRequest httpRequest) {
        try {
            // 从JWT中获取用户ID
            String token = getTokenFromRequest(httpRequest);
            Integer userId = getUserIdFromToken(token);
            
            // 分页参数
            int page = request.getPage() != null && request.getPage() > 0 ? request.getPage() : 1;
            int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;
            int offset = (page - 1) * size;
            
            // 日期参数处理
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
                startDate = LocalDate.parse(request.getStartDate()).atStartOfDay();
            }
            if (request.getEndDate() != null && !request.getEndDate().isEmpty()) {
                endDate = LocalDate.parse(request.getEndDate()).atTime(LocalTime.MAX);
            }
            
            // 查询记录
            List<Record> records = recordMapper.findByCondition(
                userId,
                null, // 不再使用分类筛选
                startDate,
                endDate,
                offset,
                size
            );
            
            // 查询总数
            int total = recordMapper.countByCondition(
                userId,
                null, // 不再使用分类筛选
                startDate,
                endDate
            );
            
            // 转换为响应对象
            List<Map<String, Object>> responseList = new ArrayList<>();
            for (Record record : records) {
                Map<String, Object> response = new HashMap<>();
                response.put("record_id", record.getRecordId());
                response.put("user_id", record.getUserId());
                response.put("amount", record.getAmount());
                response.put("category", record.getCategory());
                response.put("description", record.getRemark()); // 前端期望字段名是description
                if (record.getRecordTime() != null) {
                    response.put("record_time", record.getRecordTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else {
                    response.put("record_time", null);
                }
                response.put("year", record.getYear());
                response.put("month", record.getMonth());
                response.put("day", record.getDay());
                if (record.getCreateTime() != null) {
                    response.put("created_at", record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else {
                    // 使用 created_at 字段
                    if (record.getRecordTime() != null) {
                        response.put("created_at", record.getRecordTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else {
                        response.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                }
                
                if (record.getUpdateTime() != null) {
                    response.put("updated_at", record.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else {
                    response.put("updated_at", response.get("created_at"));
                }
                
                response.put("is_deleted", record.getIsDeleted() != null && record.getIsDeleted() ? 1 : 0);
                
                responseList.add(response);
            }
            
            // 直接返回记录列表
            return ApiResponse.success(responseList);
        } catch (Exception e) {
            log.error("获取记账记录列表异常", e);
            return ApiResponse.error(500, "获取记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取记账记录详情
     * @param recordId 记录ID
     * @param httpRequest HTTP请求
     * @return 记账记录详情
     */
    @GetMapping("/detail/{recordId}")
    public ApiResponse<?> getRecordDetail(@PathVariable Long recordId, HttpServletRequest httpRequest) {
        try {
            // 从JWT中获取用户ID
            String token = getTokenFromRequest(httpRequest);
            Integer userId = getUserIdFromToken(token);
            
            // 查询记录
            Record record = recordMapper.findById(recordId, userId);
            if (record == null) {
                return ApiResponse.error(404, "记录不存在");
            }
            
            // 转换为响应对象
            Map<String, Object> response = new HashMap<>();
            response.put("record_id", record.getRecordId());
            response.put("user_id", record.getUserId());
            response.put("amount", record.getAmount());
            response.put("category", record.getCategory());
            response.put("description", record.getRemark());
            if (record.getRecordTime() != null) {
                response.put("record_time", record.getRecordTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                response.put("record_time", null);
            }
            response.put("year", record.getYear());
            response.put("month", record.getMonth());
            response.put("day", record.getDay());
            if (record.getCreateTime() != null) {
                response.put("created_at", record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                // 使用 created_at 字段
                if (record.getRecordTime() != null) {
                    response.put("created_at", record.getRecordTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else {
                    response.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }
            
            if (record.getUpdateTime() != null) {
                response.put("updated_at", record.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                response.put("updated_at", response.get("created_at"));
            }
            
            response.put("is_deleted", record.getIsDeleted() != null && record.getIsDeleted() ? 1 : 0);
            
            // 查询分类信息
            Category category = categoryMapper.findByCategoryCode(record.getCategory());
            if (category != null) {
                response.put("categoryName", category.getCategoryName());
            }
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取记账记录详情异常", e);
            return ApiResponse.error(500, "获取记录详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 从请求中获取token
     * @param request HTTP请求
     * @return token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    private Integer getUserIdFromToken(String token) {
        if (token == null) {
            // TODO: 替换为从Spring Security上下文中获取
            return 1; // 模拟用户ID
        }
        return jwtUtil.extractUserId(token);
    }
} 