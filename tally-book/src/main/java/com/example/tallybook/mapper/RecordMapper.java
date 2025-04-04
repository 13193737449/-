package com.example.tallybook.mapper;

import com.example.tallybook.model.Record;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RecordMapper {
    
    /**
     * 添加记账记录
     * @param record 记账记录
     * @return 影响行数
     */
    @Insert("INSERT INTO record(user_id, amount, category, description, record_time, year, month, day, created_at, updated_at, is_deleted) " +
            "VALUES(#{userId}, #{amount}, #{category}, #{remark}, #{recordTime}, YEAR(#{recordTime}), MONTH(#{recordTime}), DAY(#{recordTime}), NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insert(Record record);
    
    /**
     * 更新记账记录
     * @param record 记账记录
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE record SET updated_at = NOW() " +
            "<if test='category != null'>, category = #{category} </if>" +
            "<if test='amount != null'>, amount = #{amount} </if>" +
            "<if test='remark != null'>, description = #{remark} </if>" +
            "<if test='recordTime != null'>, record_time = #{recordTime}, " +
            "year = YEAR(#{recordTime}), month = MONTH(#{recordTime}), day = DAY(#{recordTime}) </if>" +
            "WHERE record_id = #{recordId} AND user_id = #{userId}" +
            "</script>")
    int update(Record record);
    
    /**
     * 软删除记账记录（标记为已删除）
     * @param recordId 记录ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE record SET is_deleted = 1, updated_at = NOW() WHERE record_id = #{recordId} AND user_id = #{userId}")
    int softDelete(@Param("recordId") Long recordId, @Param("userId") Integer userId);
    
    /**
     * 物理删除记账记录（仅在特殊情况下使用）
     * @param recordId 记录ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM record WHERE record_id = #{recordId} AND user_id = #{userId}")
    int hardDelete(@Param("recordId") Long recordId, @Param("userId") Integer userId);
    
    /**
     * 查询记账记录列表
     * @param userId 用户ID
     * @param category 分类
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param offset 偏移量
     * @param limit 每页条数
     * @return 记账记录列表
     */
    @Select("<script>" +
            "SELECT record_id, user_id, amount, category, description as remark, record_time, " +
            "year, month, day, created_at as createTime, updated_at as updateTime, is_deleted as isDeleted " +
            "FROM record WHERE user_id = #{userId} AND is_deleted = 0 " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='startDate != null'> AND record_time &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND record_time &lt;= #{endDate} </if>" +
            "ORDER BY record_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Record> findByCondition(
            @Param("userId") Integer userId,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
    
    /**
     * 查询记账记录总数
     * @param userId 用户ID
     * @param category 分类
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总记录数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM record WHERE user_id = #{userId} AND is_deleted = 0 " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='startDate != null'> AND record_time &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND record_time &lt;= #{endDate} </if>" +
            "</script>")
    int countByCondition(
            @Param("userId") Integer userId,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * 根据ID查询记账记录
     * @param recordId 记录ID
     * @param userId 用户ID
     * @return 记账记录
     */
    @Select("SELECT record_id, user_id, amount, category, description as remark, record_time, " +
            "year, month, day, created_at as createTime, updated_at as updateTime, is_deleted as isDeleted " +
            "FROM record WHERE record_id = #{recordId} AND user_id = #{userId} AND is_deleted = 0")
    Record findById(@Param("recordId") Long recordId, @Param("userId") Integer userId);
} 