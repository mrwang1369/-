package com.pethealth.controller;

import com.pethealth.common.Result;
import com.pethealth.dto.PetCreateRequestDTO;
import com.pethealth.dto.PetListQueryDTO;
import com.pethealth.dto.PetListResponseDTO;
import com.pethealth.dto.PetResponseDTO;
import com.pethealth.dto.PetUpdateRequestDTO;
import com.pethealth.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 存储宠物基本信息，支持多只宠物管理 前端控制器
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Slf4j
@RestController
@RequestMapping("/pets")
@Tag(name = "宠物档案管理", description = "宠物档案相关接口")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    @Operation(summary = "获取宠物列表", description = "根据用户ID获取宠物列表，支持分页和条件查询")
    public Result<PetListResponseDTO> getPets(
            @Parameter(description = "宠物种类") @RequestParam(required = false) String species,
            @Parameter(description = "宠物品种") @RequestParam(required = false) String breed,
            @Parameter(description = "宠物姓名关键字") @RequestParam(required = false) String nameKeyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("获取宠物列表: userId={}, species={}, breed={}, nameKeyword={}\n", 
                userId, species, breed, nameKeyword);

        PetListQueryDTO queryDTO = new PetListQueryDTO();
        queryDTO.setSpecies(species);
        queryDTO.setBreed(breed);
        queryDTO.setNameKeyword(nameKeyword);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);

        PetListResponseDTO result = petService.getPetsByUserId(userId, queryDTO);
        return Result.success("获取成功", result);
    }

    @PostMapping
    @Operation(summary = "创建宠物档案", description = "为当前用户创建新的宠物档案")
    public Result<PetResponseDTO> createPet(
            @Parameter(description = "宠物创建请求") @Valid @RequestBody PetCreateRequestDTO createRequest,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("创建宠物档案: userId={}, petName={}", userId, createRequest.getName());

        PetResponseDTO result = petService.createPet(userId, createRequest);
        return Result.success("创建成功", result);
    }

    @GetMapping("/{petId}")
    @Operation(summary = "获取宠物详情", description = "根据宠物ID获取宠物详细信息")
    public Result<PetResponseDTO> getPetDetail(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("获取宠物详情: userId={}, petId={}", userId, petId);

        PetResponseDTO result = petService.getPetDetail(userId, petId);
        return Result.success("获取成功", result);
    }

    @PutMapping("/{petId}")
    @Operation(summary = "更新宠物信息", description = "更新指定宠物的基本信息")
    public Result<PetResponseDTO> updatePet(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            @Parameter(description = "宠物更新请求") @Valid @RequestBody PetUpdateRequestDTO updateRequest,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("更新宠物信息: userId={}, petId={}, petName={}", userId, petId, updateRequest.getName());

        PetResponseDTO result = petService.updatePet(userId, petId, updateRequest);
        return Result.success("更新成功", result);
    }

    @DeleteMapping("/{petId}")
    @Operation(summary = "删除宠物档案", description = "逻辑删除指定的宠物档案")
    public Result<Void> deletePet(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("删除宠物档案: userId={}, petId={}", userId, petId);

        petService.deletePet(userId, petId);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{petId}/avatar")
    @Operation(summary = "上传宠物头像", description = "为指定宠物上传头像图片")
    public Result<String> uploadPetAvatar(
            @Parameter(description = "宠物ID") @PathVariable Integer petId,
            @Parameter(description = "头像URL") @RequestParam String avatarUrl,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("currentUserId");
        log.info("上传宠物头像: userId={}, petId={}, avatarUrl={}", userId, petId, avatarUrl);

        // 验证宠物归属权
        if (!petService.checkPetOwnership(userId, petId)) {
            return Result.error("无权操作该宠物");
        }

        // TODO: 实现文件上传逻辑
        // 这里暂时直接更新头像URL，实际项目中需要实现文件上传功能
        
        PetUpdateRequestDTO updateRequest = new PetUpdateRequestDTO();
        updateRequest.setAvatarUrl(avatarUrl);
        
        PetResponseDTO result = petService.updatePet(userId, petId, updateRequest);
        return Result.success("头像上传成功", result.getAvatarUrl());
    }
}