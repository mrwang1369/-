package com.pethealth.controller;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.common.Result;
import com.pethealth.dto.VaccinationRecordCreateDTO;
import com.pethealth.dto.VaccinationRecordResponseDTO;
import com.pethealth.dto.VaccinationRecordUpdateDTO;
import com.pethealth.service.VaccinationRecordService;
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
 * 疫苗记录管理控制器
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@RestController
@RequestMapping("/vaccination-records")
@Tag(name = "疫苗记录管理", description = "疫苗接种记录的增删改查及相关提醒功能")
@Slf4j
public class VaccinationRecordController {

    @Autowired
    private VaccinationRecordService vaccinationRecordService;

    @PostMapping
    @Operation(summary = "创建疫苗记录", description = "为指定宠物创建疫苗接种记录")
    public Result<VaccinationRecordResponseDTO> createRecord(
            @Valid @RequestBody VaccinationRecordCreateDTO createDTO) {
        VaccinationRecordResponseDTO response = vaccinationRecordService.createRecord(createDTO);
        return Result.<VaccinationRecordResponseDTO>success("疫苗记录创建成功", response);
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "更新疫苗记录", description = "更新指定ID的疫苗记录信息")
    public Result<VaccinationRecordResponseDTO> updateRecord(
            @Parameter(description = "疫苗记录ID") @PathVariable Integer recordId,
            @Valid @RequestBody VaccinationRecordUpdateDTO updateDTO) {
        VaccinationRecordResponseDTO response = vaccinationRecordService.updateRecord(recordId, updateDTO);
        return Result.<VaccinationRecordResponseDTO>success("疫苗记录更新成功", response);
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "删除疫苗记录", description = "逻辑删除指定ID的疫苗记录")
    public Result<Void> deleteRecord(
            @Parameter(description = "疫苗记录ID") @PathVariable Integer recordId) {
        vaccinationRecordService.deleteRecord(recordId);
        return Result.<Void>success("疫苗记录删除成功", null);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "获取宠物疫苗记录列表", description = "根据宠物ID获取疫苗记录列表，支持分页")
    public Result<PageResult<VaccinationRecordResponseDTO>> getRecordsByPetId(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            @Valid PageRequest pageRequest) {
        PageResult<VaccinationRecordResponseDTO> result = vaccinationRecordService.getRecordsByPetId(petId, pageRequest);
        return Result.success(result);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "获取即将到期疫苗", description = "获取指定日期前需要接种的疫苗记录")
    public Result<List<VaccinationRecordResponseDTO>> getUpcomingVaccinations(
            @Parameter(description = "截止日期") @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now().plusDays(30);
        List<VaccinationRecordResponseDTO> records = vaccinationRecordService.getUpcomingVaccinations(queryDate);
        return Result.success(records);
    }

    @GetMapping("/expired")
    @Operation(summary = "获取过期疫苗", description = "获取已过期未接种的疫苗记录")
    public Result<List<VaccinationRecordResponseDTO>> getExpiredVaccinations(
            @Parameter(description = "参考日期") @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<VaccinationRecordResponseDTO> records = vaccinationRecordService.getExpiredVaccinations(queryDate);
        return Result.success(records);
    }
}