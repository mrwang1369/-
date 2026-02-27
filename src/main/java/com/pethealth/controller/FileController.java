package com.pethealth.controller;

import com.pethealth.common.Result;
import com.pethealth.dto.FileInfoResponseDTO;
import com.pethealth.dto.FileUploadRequestDTO;
import com.pethealth.dto.FileUploadResponseDTO;
import com.pethealth.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传Controller
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Slf4j
@RestController
@RequestMapping("/files")
@Tag(name = "文件管理", description = "文件上传、下载、管理相关接口")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 通用文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "通用文件上传", description = "上传各种类型的文件")
    public Result<FileUploadResponseDTO> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @ModelAttribute FileUploadRequestDTO requestDTO,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("currentUserId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }

        log.info("用户{}上传文件: moduleType={}, businessId={}", userId, requestDTO.getModuleType(), requestDTO.getBusinessId());
        FileUploadResponseDTO response = fileService.uploadFile(file, requestDTO, userId);
        return Result.success(response);
    }

    /**
     * 宠物头像上传
     */
    @PostMapping("/upload-avatar/{petId}")
    @Operation(summary = "宠物头像上传", description = "上传宠物头像")
    public Result<FileUploadResponseDTO> uploadPetAvatar(
            @Parameter(description = "宠物ID") @PathVariable Long petId,
            @Parameter(description = "上传的头像文件") @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("currentUserId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }

        log.info("用户{}上传宠物{}头像", userId, petId);
        FileUploadResponseDTO response = fileService.uploadPetAvatar(file, petId, userId);
        return Result.success(response);
    }

    /**
     * 健康记录附件上传
     */
    @PostMapping("/upload-health-record/{moduleType}/{recordId}")
    @Operation(summary = "健康记录附件上传", description = "上传健康记录相关附件")
    public Result<FileUploadResponseDTO> uploadHealthRecordAttachment(
            @Parameter(description = "模块类型") @PathVariable String moduleType,
            @Parameter(description = "记录ID") @PathVariable Long recordId,
            @Parameter(description = "上传的附件文件") @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("currentUserId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }

        log.info("用户{}上传健康记录附件: moduleType={}, recordId={}", userId, moduleType, recordId);
        FileUploadResponseDTO response = fileService.uploadHealthRecordAttachment(file, moduleType, recordId, userId);
        return Result.success(response);
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/{fileId}")
    @Operation(summary = "获取文件信息", description = "根据文件ID获取文件详细信息")
    public Result<FileInfoResponseDTO> getFileInfo(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {

        FileInfoResponseDTO fileInfo = fileService.getFileInfo(fileId);
        return Result.success(fileInfo);
    }

    /**
     * 根据业务ID获取文件列表
     */
    @GetMapping("/list/business/{moduleType}/{businessId}")
    @Operation(summary = "获取业务相关文件列表", description = "根据模块类型和业务ID获取相关文件列表")
    public Result<List<FileInfoResponseDTO>> getFileListByBusiness(
            @Parameter(description = "模块类型") @PathVariable String moduleType,
            @Parameter(description = "业务ID") @PathVariable Long businessId) {

        List<FileInfoResponseDTO> fileList = fileService.getFileListByBusiness(moduleType, businessId);
        return Result.success(fileList);
    }

    /**
     * 获取用户文件列表
     */
    @GetMapping("/list/user")
    @Operation(summary = "获取用户文件列表", description = "获取当前用户上传的所有文件")
    public Result<List<FileInfoResponseDTO>> getFileListByUser(HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("currentUserId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }

        List<FileInfoResponseDTO> fileList = fileService.getFileListByUser(userId);
        return Result.success(fileList);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    @Operation(summary = "删除文件", description = "删除指定文件")
    public Result<Boolean> deleteFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("currentUserId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }

        boolean result = fileService.deleteFile(fileId, userId);
        return Result.success(result);
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    public Result<Integer> batchDeleteFiles(
            @Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("currentUserId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }

        int deletedCount = fileService.batchDeleteFiles(fileIds, userId);
        return Result.success(deletedCount);
    }

    /**
     * 文件访问接口（静态资源映射）
     * 注意：此接口由WebMvcConfig配置静态资源处理器
     */
    @GetMapping("/download/{moduleType}/{fileName}")
    @Operation(summary = "文件下载", description = "下载指定文件")
    public void downloadFile(
            @Parameter(description = "模块类型") @PathVariable String moduleType,
            @Parameter(description = "文件名") @PathVariable String fileName) {
        // 此方法主要用于Swagger文档展示，实际文件访问由静态资源处理器处理
    }
}