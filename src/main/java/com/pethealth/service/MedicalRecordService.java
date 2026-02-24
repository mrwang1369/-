package com.pethealth.service;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.MedicalRecordCreateDTO;
import com.pethealth.dto.MedicalRecordResponseDTO;
import com.pethealth.dto.MedicalRecordUpdateDTO;
import com.pethealth.entity.MedicalRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 记录病历和用药信息，可设置用药提醒 服务类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface MedicalRecordService extends IService<MedicalRecord> {

    /**
     * 创建病历记录
     * @param createDTO 创建请求DTO
     * @return 病历记录响应DTO
     */
    MedicalRecordResponseDTO createRecord(MedicalRecordCreateDTO createDTO);

    /**
     * 更新病历记录
     * @param recordId 记录ID
     * @param updateDTO 更新请求DTO
     * @return 病历记录响应DTO
     */
    MedicalRecordResponseDTO updateRecord(Integer recordId, MedicalRecordUpdateDTO updateDTO);

    /**
     * 删除病历记录（逻辑删除）
     * @param recordId 记录ID
     */
    void deleteRecord(Integer recordId);

    /**
     * 根据宠物ID获取病历记录列表（分页）
     * @param petId 宠物ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<MedicalRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest);

    /**
     * 获取近期病历记录
     * @param date 基准日期
     * @return 病历记录列表
     */
    List<MedicalRecordResponseDTO> getRecentMedicalRecords(LocalDate date);

    /**
     * 根据诊断结果搜索病历记录
     * @param diagnosisKeyword 诊断关键词
     * @return 病历记录列表
     */
    List<MedicalRecordResponseDTO> searchByDiagnosis(String diagnosisKeyword);

    /**
     * 获取需要用药提醒的病历记录
     * @return 病历记录列表
     */
    List<MedicalRecordResponseDTO> getMedicationReminders();
}