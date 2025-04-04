package com.example.tallybook.mapper;

import com.example.tallybook.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    /**
     * 获取分类列表
     * @param userId 用户ID, 为null则只获取系统默认分类
     * @return 分类列表
     */
    @Select("<script>" +
            "SELECT * FROM category WHERE 1=1 " +
            "ORDER BY sort_order ASC" +
            "</script>")
    List<Category> findByTypeAndUserId(@Param("userId") Integer userId);
    
    /**
     * 根据ID获取分类
     * @param categoryId 分类ID
     * @return 分类信息
     */
    @Select("SELECT * FROM category WHERE category_id = #{categoryId}")
    Category findById(@Param("categoryId") Integer categoryId);

    /**
     * 根据分类编码获取分类
     * @param categoryCode 分类编码
     * @return 分类信息
     */
    @Select("SELECT * FROM category WHERE category_code = #{categoryCode}")
    Category findByCategoryCode(@Param("categoryCode") String categoryCode);
} 