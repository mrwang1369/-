package com.pethealth.controller;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.common.Result;
import com.pethealth.dto.CheckupRecordCreateDTO;
import com.pethealth.dto.CheckupRecordResponseDTO;
import com.pethealth.dto.CheckupRecordUpdateDTO;
import com.pethealth.service.CheckupRecordService;
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
 * 体检记录管理控制器
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@RestController
@RequestMapping("/checkup-records")
@Tag(name = "体检记录管理", description = "体检记录的增删改查及相关查询功能")
@Slf4j
public class CheckupRecordController {

    @Autowired
    private CheckupRecordService checkupRecordService;

    @PostMapping
    @Operation(summary = "创建体检记录", description = "为指定宠物创建体检记录")
    public Result<CheckupRecordResponseDTO> createRecord(
            @Valid @RequestBody CheckupRecordCreateDTO createDTO) {
        CheckupRecordResponseDTO response = checkupRecordService.createRecord(createDTO);
        return Result.<CheckupRecordResponseDTO>success("体检记录创建成功", response);
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "更新体检记录", description = "更新指定ID的体检记录信息")
    public Result<CheckupRecordResponseDTO> updateRecord(
            @Parameter(description = "体检记录ID") @PathVariable Integer recordId,
            @Valid @RequestBody CheckupRecordUpdateDTO updateDTO) {
        CheckupRecordResponseDTO response = checkupRecordService.updateRecord(recordId, updateDTO);
        return Result.<CheckupRecordResponseDTO>success("体检记录更新成功", response);
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "删除体检记录", description = "逻辑删除指定ID的体检记录")
    public Result<Void> deleteRecord(
            @Parameter(description = "体检记录ID") @PathVariable Integer recordId) {
        checkupRecordService.deleteRecord(recordId);
        return Result.<Void>success("体检记录删除成功", null);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "获取宠物体检记录列表", description = "根据宠物ID获取体检记录列表，支持分页")
    public Result<PageResult<CheckupRecordResponseDTO>> getRecordsByPetId(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            @Valid PageRequest pageRequest) {
        PageResult<CheckupRecordResponseDTO> result = checkupRecordService.getRecordsByPetId(petId, pageRequest);
        return Result.success(result);
    }

    @GetMapping("/recent")
    @Operation(summary = "获取近期体检记录", description = "获取指定日期范围内最近的体检记录")
    public Result<List<CheckupRecordResponseDTO>> getRecentCheckups(
            @Parameter(description = "基准日期") @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<CheckupRecordResponseDTO> records = checkupRecordService.getRecentCheckups(queryDate);
        return Result.success(records);
    }

    @GetMapping("/overdue")
    @Operation(summary = "获取过期未体检记录", description = "获取超过指定月份未体检的记录")
    public Result<List<CheckupRecordResponseDTO>> getOverdueCheckups(
            @Parameter(description = "过期月数") @RequestParam(defaultValue = "12") int months) {
        List<CheckupRecordResponseDTO> records = checkupRecordService.getOverdueCheckups(months);
        return Result.success(records);
    }
}