package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethealth.dto.FileInfoResponseDTO;
import com.pethealth.dto.FileUploadRequestDTO;
import com.pethealth.dto.FileUploadResponseDTO;
import com.pethealth.entity.FileInfo;
import com.pethealth.enums.FileModuleTypeEnum;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.FileUploadException;
import com.pethealth.mapper.FileInfoMapper;
import com.pethealth.service.FileService;
import com.pethealth.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件上传服务实现类
 *
 * @author pethealth
 * @since 2026-02-27
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Value("${file.upload.path:#{systemProperties['user.home']}/pethealth/uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}") // 10MB
    private Long maxFileSize;

    @Value("${file.upload.allowed-types:image/jpeg,image/png,image/gif,application/pdf}")
    private String allowedTypes;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponseDTO uploadFile(MultipartFile file, FileUploadRequestDTO requestDTO, Integer userId) {
        log.info("开始上传文件: userId={}, moduleType={}, businessId={}", userId, requestDTO.getModuleType(), requestDTO.getBusinessId());

        // 参数验证
        validateFile(file);
        validateModuleType(requestDTO.getModuleType());

        try {
            // 生成存储文件名
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtils.getFileExtension(originalFilename);
            String storedName = UUID.randomUUID().toString() + "." + extension;

            // 构建存储路径
            String modulePath = FileUtils.getModulePath(requestDTO.getModuleType());
            String fullPath = uploadPath + File.separator + modulePath;
            File directory = new File(fullPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = fullPath + File.separator + storedName;
            File destFile = new File(filePath);

            // 保存文件
            file.transferTo(destFile);

            // 保存文件信息到数据库
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginalName(originalFilename);
            fileInfo.setStoredName(storedName);
            fileInfo.setFilePath(filePath);
            fileInfo.setFileSize(file.getSize());
            fileInfo.setFileType(file.getContentType());
            fileInfo.setFileExtension(extension);
            fileInfo.setModuleType(requestDTO.getModuleType());
            fileInfo.setBusinessId(requestDTO.getBusinessId());
            fileInfo.setUploaderId(userId);
            fileInfo.setUploadTime(LocalDateTime.now());

            fileInfoMapper.insert(fileInfo);

            // 构建响应
            FileUploadResponseDTO response = new FileUploadResponseDTO();
            response.setFileId(fileInfo.getFileId());
            response.setOriginalName(originalFilename);
            response.setFileUrl(buildFileUrl(modulePath, storedName));
            response.setFileSize(file.getSize());
            response.setFileType(file.getContentType());
            response.setFileExtension(extension);
            response.setModuleType(requestDTO.getModuleType());
            response.setBusinessId(requestDTO.getBusinessId());
            response.setUploadTime(LocalDateTime.now());

            log.info("文件上传成功: fileId={}, fileName={}", fileInfo.getFileId(), originalFilename);
            return response;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileUploadResponseDTO uploadPetAvatar(MultipartFile file, Long petId, Integer userId) {
        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.PET_AVATAR.getCode());
        requestDTO.setBusinessId(petId);
        requestDTO.setDescription("宠物头像");
        return uploadFile(file, requestDTO, userId);
    }

    @Override
    public FileUploadResponseDTO uploadHealthRecordAttachment(MultipartFile file, String moduleType, Long recordId, Integer userId) {
        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(moduleType);
        requestDTO.setBusinessId(recordId);
        requestDTO.setDescription("健康记录附件");
        return uploadFile(file, requestDTO, userId);
    }

    @Override
    public FileInfoResponseDTO getFileInfo(Long fileId) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null || fileInfo.getDeleted() == 1) {
            throw new BusinessException("文件不存在");
        }

        return convertToFileInfoResponse(fileInfo);
    }

    @Override
    public List<FileInfoResponseDTO> getFileListByBusiness(String moduleType, Long businessId) {
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getModuleType, moduleType)
                .eq(FileInfo::getBusinessId, businessId)
                .eq(FileInfo::getDeleted, 0)
                .orderByDesc(FileInfo::getUploadTime);

        List<FileInfo> fileList = fileInfoMapper.selectList(queryWrapper);
        return fileList.stream()
                .map(this::convertToFileInfoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileInfoResponseDTO> getFileListByUser(Integer userId) {
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getUploaderId, userId)
                .eq(FileInfo::getDeleted, 0)
                .orderByDesc(FileInfo::getUploadTime);

        List<FileInfo> fileList = fileInfoMapper.selectList(queryWrapper);
        return fileList.stream()
                .map(this::convertToFileInfoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId, Integer userId) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        // 验证权限
        if (!fileInfo.getUploaderId().equals(userId)) {
            throw new BusinessException("无权限删除该文件");
        }

        // 逻辑删除
        fileInfo.setDeleted(1);
        int result = fileInfoMapper.updateById(fileInfo);

        // 删除物理文件
        if (result > 0) {
            try {
                File file = new File(fileInfo.getFilePath());
                if (file.exists()) {
                    file.delete();
                    log.info("物理文件删除成功: {}", fileInfo.getFilePath());
                }
            } catch (Exception e) {
                log.warn("物理文件删除失败: {}", fileInfo.getFilePath(), e);
            }
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteFiles(List<Long> fileIds, Integer userId) {
        int successCount = 0;
        for (Long fileId : fileIds) {
            try {
                if (deleteFile(fileId, userId)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("批量删除文件失败: fileId={}", fileId, e);
            }
        }
        return successCount;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        // 基本验证
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("FILE_EMPTY", "文件不能为空");
        }

        // 文件大小验证
        if (file.getSize() > maxFileSize) {
            throw new FileUploadException("FILE_TOO_LARGE", 
                "文件大小超出限制: " + FileUtils.formatFileSize(maxFileSize) + ", 当前文件大小: " + FileUtils.formatFileSize(file.getSize()));
        }

        // 文件类型验证
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new FileUploadException("UNSUPPORTED_FILE_TYPE", 
                "不支持的文件类型: " + contentType + ", 支持的类型: " + allowedTypes);
        }

        // 文件名安全验证
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !FileUtils.isFileNameSafe(originalFilename)) {
            throw new FileUploadException("INVALID_FILENAME", "文件名包含非法字符");
        }

        // 文件内容安全检查（简单检查）
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new FileUploadException("EMPTY_FILE_CONTENT", "文件内容为空");
            }
            
            // 检查文件头（magic number）
            validateFileHeader(bytes, contentType);
        } catch (Exception e) {
            throw new FileUploadException("FILE_VALIDATION_FAILED", "文件验证失败: " + e.getMessage());
        }
    }

    /**
     * 验证模块类型
     */
    private void validateModuleType(String moduleType) {
        if (FileModuleTypeEnum.getByCode(moduleType) == null) {
            throw new FileUploadException("INVALID_MODULE_TYPE", "不支持的模块类型: " + moduleType);
        }
    }

    /**
     * 验证文件头（Magic Number）
     */
    private void validateFileHeader(byte[] fileBytes, String contentType) {
        if (fileBytes.length < 8) {
            return; // 文件太小，跳过验证
        }

        // 读取文件前几个字节
        byte[] header = new byte[Math.min(8, fileBytes.length)];
        System.arraycopy(fileBytes, 0, header, 0, header.length);

        // 根据Content-Type验证文件头
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
                // JPEG文件头: FF D8 FF
                if (!(header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF)) {
                    throw new FileUploadException("INVALID_JPEG_HEADER", "无效的JPEG文件头");
                }
                break;
            case "image/png":
                // PNG文件头: 89 50 4E 47 0D 0A 1A 0A
                byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
                if (!java.util.Arrays.equals(java.util.Arrays.copyOf(header, 8), pngHeader)) {
                    throw new FileUploadException("INVALID_PNG_HEADER", "无效的PNG文件头");
                }
                break;
            case "image/gif":
                // GIF文件头: 47 49 46 38
                if (!(header[0] == 0x47 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x38)) {
                    throw new FileUploadException("INVALID_GIF_HEADER", "无效的GIF文件头");
                }
                break;
            case "application/pdf":
                // PDF文件头: 25 50 44 46
                if (!(header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46)) {
                    throw new FileUploadException("INVALID_PDF_HEADER", "无效的PDF文件头");
                }
                break;
        }
    }

    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String modulePath, String storedName) {
        return String.format("http://localhost:%s%s/files/%s/%s", serverPort, contextPath, modulePath, storedName);
    }

    /**
     * 转换为响应DTO
     */
    private FileInfoResponseDTO convertToFileInfoResponse(FileInfo fileInfo) {
        FileInfoResponseDTO response = new FileInfoResponseDTO();
        response.setFileId(fileInfo.getFileId());
        response.setOriginalName(fileInfo.getOriginalName());
        response.setFileUrl(buildFileUrl(FileUtils.getModulePath(fileInfo.getModuleType()), fileInfo.getStoredName()));
        response.setFileSize(fileInfo.getFileSize());
        response.setFileSizeDisplay(FileUtils.formatFileSize(fileInfo.getFileSize()));
        response.setFileType(fileInfo.getFileType());
        response.setFileExtension(fileInfo.getFileExtension());
        response.setModuleType(fileInfo.getModuleType());
        response.setModuleTypeDisplay(FileModuleTypeEnum.getByCode(fileInfo.getModuleType()).getDisplayName());
        response.setBusinessId(fileInfo.getBusinessId());
        response.setUploaderId(fileInfo.getUploaderId());
        response.setUploadTime(fileInfo.getUploadTime());
        return response;
    }
}