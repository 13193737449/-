package com.example.tallybook.model.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSettingsRequest {
    
    @Pattern(regexp = "^(light|dark)$", message = "主题只能是light或dark")
    private String theme;
    
    private String currency;
    
    private String language;
    
    private Boolean notification;
} 