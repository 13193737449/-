package com.example.tallybook.mapper;

import com.example.tallybook.model.UserSetting;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserSettingMapper {
    
    @Select("SELECT * FROM user_setting WHERE user_id = #{userId}")
    UserSetting findByUserId(Integer userId);
    
    @Insert("INSERT INTO user_setting (user_id, theme, currency, language, notification, created_at, updated_at) " +
            "VALUES (#{userId}, #{theme}, #{currency}, #{language}, #{notification}, NOW(), NOW())")
    int insert(UserSetting userSetting);
    
    @Update("UPDATE user_setting SET theme = #{theme}, currency = #{currency}, language = #{language}, " +
            "notification = #{notification}, updated_at = NOW() WHERE user_id = #{userId}")
    int update(UserSetting userSetting);
} 