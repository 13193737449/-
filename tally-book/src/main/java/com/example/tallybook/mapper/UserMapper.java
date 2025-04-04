package com.example.tallybook.mapper;

import com.example.tallybook.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE username = #{username} AND status = 1")
    User findByUsername(String username);
    
    @Select("SELECT * FROM user WHERE phone = #{phone} AND status = 1")
    User findByPhone(String phone);
    
    @Select("SELECT * FROM user WHERE user_id = #{userId} AND status = 1")
    User findById(Integer userId);
    
    @Insert("INSERT INTO user (username, password, nickname, phone, status, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{phone}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);
    
    @Update("UPDATE user SET password = #{password}, updated_at = NOW() WHERE phone = #{phone}")
    int updatePassword(@Param("phone") String phone, @Param("password") String password);
    
    @Update("UPDATE user SET last_login_time = NOW() WHERE user_id = #{userId}")
    int updateLastLoginTime(Integer userId);
    
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int countByUsername(String username);
    
    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone}")
    int countByPhone(String phone);
} 