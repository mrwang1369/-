package com.pethealth.controller;

import com.pethealth.common.PageRequest;
import com.pethealth.common.PageResult;
import com.pethealth.common.Result;
import com.pethealth.dto.DewormingRecordCreateDTO;
import com.pethealth.dto.DewormingRecordResponseDTO;
import com.pethealth.dto.DewormingRecordUpdateDTO;
import com.pethealth.service.DewormingRecordService;
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
 * 驱虫记录管理控制器
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@RestController
@RequestMapping("/api/deworming-records")
@Tag(name = "驱虫记录管理", description = "驱虫记录的增删改查及相关提醒功能")
@Slf4j
public class DewormingRecordController {

    @Autowired
    private DewormingRecordService dewormingRecordService;

    @PostMapping
    @Operation(summary = "创建驱虫记录", description = "为指定宠物创建驱虫记录")
    public Result<DewormingRecordResponseDTO> createRecord(
            @Valid @RequestBody DewormingRecordCreateDTO createDTO) {
        DewormingRecordResponseDTO response = dewormingRecordService.createRecord(createDTO);
        return Result.<DewormingRecordResponseDTO>success("驱虫记录创建成功", response);
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "更新驱虫记录", description = "更新指定ID的驱虫记录信息")
    public Result<DewormingRecordResponseDTO> updateRecord(
            @Parameter(description = "驱虫记录ID") @PathVariable Integer recordId,
            @Valid @RequestBody DewormingRecordUpdateDTO updateDTO) {
        DewormingRecordResponseDTO response = dewormingRecordService.updateRecord(recordId, updateDTO);
        return Result.<DewormingRecordResponseDTO>success("驱虫记录更新成功", response);
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "删除驱虫记录", description = "逻辑删除指定ID的驱虫记录")
    public Result<Void> deleteRecord(
            @Parameter(description = "驱虫记录ID") @PathVariable Integer recordId) {
        dewormingRecordService.deleteRecord(recordId);
        return Result.<Void>success("驱虫记录删除成功", null);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "获取宠物驱虫记录列表", description = "根据宠物ID获取驱虫记录列表，支持分页")
    public Result<PageResult<DewormingRecordResponseDTO>> getRecordsByPetId(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            @Valid PageRequest pageRequest) {
        PageResult<DewormingRecordResponseDTO> result = dewormingRecordService.getRecordsByPetId(petId, pageRequest);
        return Result.success(result);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "获取即将驱虫提醒", description = "获取指定日期前需要驱虫的记录")
    public Result<List<DewormingRecordResponseDTO>> getUpcomingDeworming(
            @Parameter(description = "截止日期") @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now().plusDays(30);
        List<DewormingRecordResponseDTO> records = dewormingRecordService.getUpcomingDewormings(queryDate);
        return Result.success(records);
    }

    @GetMapping("/overdue")
    @Operation(summary = "获取过期未驱虫记录", description = "获取已过期未驱虫的记录")
    public Result<List<DewormingRecordResponseDTO>> getOverdueDeworming(
            @Parameter(description = "参考日期") @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<DewormingRecordResponseDTO> records = dewormingRecordService.getExpiredDewormings(queryDate);
        return Result.success(records);
    }
}