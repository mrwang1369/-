package com.pethealth.controller;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.common.Result;
import com.pethealth.dto.MedicalRecordCreateDTO;
import com.pethealth.dto.MedicalRecordResponseDTO;
import com.pethealth.dto.MedicalRecordUpdateDTO;
import com.pethealth.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 病历记录管理控制器
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "病历记录管理", description = "病历记录的增删改查及相关查询功能")
@Slf4j
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping
    @Operation(summary = "创建病历记录", description = "为指定宠物创建病历记录")
    public Result<MedicalRecordResponseDTO> createRecord(
            @Valid @RequestBody MedicalRecordCreateDTO createDTO) {
        MedicalRecordResponseDTO response = medicalRecordService.createRecord(createDTO);
        return Result.<MedicalRecordResponseDTO>success("病历记录创建成功", response);
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "更新病历记录", description = "更新指定ID的病历记录信息")
    public Result<MedicalRecordResponseDTO> updateRecord(
            @Parameter(description = "病历记录ID") @PathVariable Integer recordId,
            @Valid @RequestBody MedicalRecordUpdateDTO updateDTO) {
        MedicalRecordResponseDTO response = medicalRecordService.updateRecord(recordId, updateDTO);
        return Result.<MedicalRecordResponseDTO>success("病历记录更新成功", response);
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "删除病历记录", description = "逻辑删除指定ID的病历记录")
    public Result<Void> deleteRecord(
            @Parameter(description = "病历记录ID") @PathVariable Integer recordId) {
        medicalRecordService.deleteRecord(recordId);
        return Result.<Void>success("病历记录删除成功", null);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "获取宠物病历记录列表", description = "根据宠物ID获取病历记录列表，支持分页")
    public Result<PageResult<MedicalRecordResponseDTO>> getRecordsByPetId(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            @Valid PageRequest pageRequest) {
        PageResult<MedicalRecordResponseDTO> result = medicalRecordService.getRecordsByPetId(petId, pageRequest);
        return Result.success(result);
    }

    @GetMapping("/recent")
    @Operation(summary = "获取近期病历记录", description = "获取指定日期范围内最近的病历记录")
    public Result<List<MedicalRecordResponseDTO>> getRecentMedicalRecords(
            @Parameter(description = "基准日期") @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<MedicalRecordResponseDTO> records = medicalRecordService.getRecentMedicalRecords(queryDate);
        return Result.success(records);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索病历记录", description = "根据诊断关键词搜索病历记录")
    public Result<List<MedicalRecordResponseDTO>> searchByDiagnosis(
            @Parameter(description = "诊断关键词") @RequestParam String keyword) {
        List<MedicalRecordResponseDTO> records = medicalRecordService.searchByDiagnosis(keyword);
        return Result.success(records);
    }

    @GetMapping("/medication-reminders")
    @Operation(summary = "获取用药提醒", description = "获取需要用药提醒的病历记录")
    public Result<List<MedicalRecordResponseDTO>> getMedicationReminders() {
        List<MedicalRecordResponseDTO> records = medicalRecordService.getMedicationReminders();
        return Result.success(records);
    }
}