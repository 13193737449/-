package com.example.tallybook.controller;

import com.example.tallybook.mapper.CategoryMapper;
import com.example.tallybook.model.Category;
import com.example.tallybook.model.dto.ApiResponse;
import com.example.tallybook.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    
    private final CategoryMapper categoryMapper;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取分类列表
     * @return 分类列表
     */
    @GetMapping("/list")
    public ApiResponse<?> getCategories() {
        try {
            // TODO: 从当前登录用户中获取userId
            Integer userId = 1; // 假设当前用户ID为1
            
            List<Category> categories = categoryMapper.findByTypeAndUserId(userId);
            return ApiResponse.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ApiResponse.error(500, "获取分类列表失败: " + e.getMessage());
        }
    }
} 