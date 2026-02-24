package com.pethealth.service;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.dto.DewormingRecordCreateDTO;
import com.pethealth.dto.DewormingRecordResponseDTO;
import com.pethealth.dto.DewormingRecordUpdateDTO;
import com.pethealth.entity.DewormingRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 记录驱虫信息，支持周期提醒 服务类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface DewormingRecordService extends IService<DewormingRecord> {

    /**
     * 创建驱虫记录
     * @param createDTO 创建请求DTO
     * @return 驱虫记录响应DTO
     */
    DewormingRecordResponseDTO createRecord(DewormingRecordCreateDTO createDTO);

    /**
     * 更新驱虫记录
     * @param recordId 记录ID
     * @param updateDTO 更新请求DTO
     * @return 驱虫记录响应DTO
     */
    DewormingRecordResponseDTO updateRecord(Integer recordId, DewormingRecordUpdateDTO updateDTO);

    /**
     * 删除驱虫记录（逻辑删除）
     * @param recordId 记录ID
     */
    void deleteRecord(Integer recordId);

    /**
     * 根据宠物ID获取驱虫记录列表（分页）
     * @param petId 宠物ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<DewormingRecordResponseDTO> getRecordsByPetId(Integer petId, PageRequest pageRequest);

    /**
     * 获取即将到期的驱虫记录
     * @param date 基准日期
     * @return 驱虫记录列表
     */
    List<DewormingRecordResponseDTO> getUpcomingDewormings(LocalDate date);

    /**
     * 获取已过期的驱虫记录
     * @param date 基准日期
     * @return 驱虫记录列表
     */
    List<DewormingRecordResponseDTO> getExpiredDewormings(LocalDate date);

    /**
     * 计算下次驱虫日期（基于当前记录和周期）
     * @param lastDewormingDate 上次驱虫日期
     * @param cycleDays 驱虫周期（天数）
     * @return 下次驱虫日期
     */
    LocalDate calculateNextDewormingDate(LocalDate lastDewormingDate, int cycleDays);
}