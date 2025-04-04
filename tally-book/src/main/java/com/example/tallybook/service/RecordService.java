package com.example.tallybook.service;

import com.example.tallybook.model.dto.RecordRequest;

import java.util.Map;

public interface RecordService {
    
    /**
     * Add a new expense record
     * @param request record request
     * @param userId current user ID
     * @return created record
     */
    Map<String, Object> addRecord(RecordRequest request, Integer userId);
    
    /**
     * Get record list with pagination
     * @param userId current user ID
     * @param year filter by year
     * @param month filter by month
     * @param category filter by category
     * @param pageNum page number
     * @param pageSize page size
     * @return record list and pagination info
     */
    Map<String, Object> getRecordList(Integer userId, Integer year, Integer month, 
                                     String category, Integer pageNum, Integer pageSize);
    
    /**
     * Update an existing record
     * @param recordId record ID
     * @param request record request
     * @param userId current user ID
     */
    void updateRecord(Integer recordId, RecordRequest request, Integer userId);
    
    /**
     * Delete a record
     * @param recordId record ID
     * @param userId current user ID
     */
    void deleteRecord(Integer recordId, Integer userId);
} 