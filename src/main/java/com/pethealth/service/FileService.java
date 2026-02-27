package com.pethealth.service;

import com.pethealth.dto.FileInfoResponseDTO;
import com.pethealth.dto.FileUploadRequestDTO;
import com.pethealth.dto.FileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传服务接口
 *
 * @author pethealth
 * @since 2026-02-27
 */
public interface FileService {

    /**
     * 通用文件上传
     *
     * @param file 文件
     * @param requestDTO 上传请求参数
     * @param userId 用户ID
     * @return 文件上传响应
     */
    FileUploadResponseDTO uploadFile(MultipartFile file, FileUploadRequestDTO requestDTO, Integer userId);

    /**
     * 宠物头像上传
     *
     * @param file 头像文件
     * @param petId 宠物ID
     * @param userId 用户ID
     * @return 文件上传响应
     */
    FileUploadResponseDTO uploadPetAvatar(MultipartFile file, Long petId, Integer userId);

    /**
     * 健康记录附件上传
     *
     * @param file 附件文件
     * @param moduleType 模块类型
     * @param recordId 记录ID
     * @param userId 用户ID
     * @return 文件上传响应
     */
    FileUploadResponseDTO uploadHealthRecordAttachment(MultipartFile file, String moduleType, Long recordId, Integer userId);

    /**
     * 根据文件ID获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfoResponseDTO getFileInfo(Long fileId);

    /**
     * 根据业务ID获取文件列表
     *
     * @param moduleType 模块类型
     * @param businessId 业务ID
     * @return 文件列表
     */
    List<FileInfoResponseDTO> getFileListByBusiness(String moduleType, Long businessId);

    /**
     * 根据用户ID获取文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileInfoResponseDTO> getFileListByUser(Integer userId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId, Integer userId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @param userId 用户ID
     * @return 删除成功的文件数量
     */
    int batchDeleteFiles(List<Long> fileIds, Integer userId);
}