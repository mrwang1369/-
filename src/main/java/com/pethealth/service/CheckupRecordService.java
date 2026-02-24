package com.pethealth.service;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.CheckupRecordCreateDTO;
import com.pethealth.dto.CheckupRecordResponseDTO;
import com.pethealth.dto.CheckupRecordUpdateDTO;
import com.pethealth.entity.CheckupRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 记录体检信息，支持图片上传 服务类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface CheckupRecordService extends IService<CheckupRecord> {

    /**
     * 创建体检记录
     * @param createDTO 创建请求DTO
     * @return 体检记录响应DTO
     */
    CheckupRecordResponseDTO createRecord(CheckupRecordCreateDTO createDTO);

    /**
     * 更新体检记录
     * @param recordId 记录ID
     * @param updateDTO 更新请求DTO
     * @return 体检记录响应DTO
     */
    CheckupRecordResponseDTO updateRecord(Integer recordId, CheckupRecordUpdateDTO updateDTO);

    /**
     * 删除体检记录（逻辑删除）
     * @param recordId 记录ID
     */
    void deleteRecord(Integer recordId);

    /**
     * 根据宠物ID获取体检记录列表（分页）
     * @param petId 宠物ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<CheckupRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest);

    /**
     * 获取近期体检记录
     * @param date 基准日期
     * @return 体检记录列表
     */
    List<CheckupRecordResponseDTO> getRecentCheckups(LocalDate date);

    /**
     * 获取过期未体检记录
     * @param months 月份阈值（超过几个月未体检视为过期）
     * @return 体检记录列表
     */
    List<CheckupRecordResponseDTO> getOverdueCheckups(int months);
}