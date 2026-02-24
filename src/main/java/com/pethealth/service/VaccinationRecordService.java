package com.pethealth.service;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.VaccinationRecordCreateDTO;
import com.pethealth.dto.VaccinationRecordResponseDTO;
import com.pethealth.dto.VaccinationRecordUpdateDTO;
import com.pethealth.entity.VaccinationRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 记录疫苗接种信息，用于健康计划和提醒 服务类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface VaccinationRecordService extends IService<VaccinationRecord> {

    /**
     * 创建疫苗记录
     * @param createDTO 创建请求DTO
     * @return 疫苗记录响应DTO
     */
    VaccinationRecordResponseDTO createRecord(VaccinationRecordCreateDTO createDTO);

    /**
     * 更新疫苗记录
     * @param recordId 记录ID
     * @param updateDTO 更新请求DTO
     * @return 疫苗记录响应DTO
     */
    VaccinationRecordResponseDTO updateRecord(Integer recordId, VaccinationRecordUpdateDTO updateDTO);

    /**
     * 删除疫苗记录（逻辑删除）
     * @param recordId 记录ID
     */
    void deleteRecord(Integer recordId);

    /**
     * 根据宠物ID获取疫苗记录列表（分页）
     * @param petId 宠物ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<VaccinationRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest);

    /**
     * 获取即将到期的疫苗接种记录
     * @param date 基准日期
     * @return 疫苗记录列表
     */
    List<VaccinationRecordResponseDTO> getUpcomingVaccinations(LocalDate date);

    /**
     * 获取已过期的疫苗接种记录
     * @param date 基准日期
     * @return 疫苗记录列表
     */
    List<VaccinationRecordResponseDTO> getExpiredVaccinations(LocalDate date);
}