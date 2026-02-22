package com.pethealth.utils;

import com.pethealth.handler.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtils {

    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif"
    );

    // 允许的文档类型
    private static final List<String> ALLOWED_DOC_TYPES = Arrays.asList(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    // 最大文件大小(5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传宠物健康报告
     *
     * @param file 上传的文件
     * @param baseDir 基础存储目录
     * @param petId 宠物ID
     * @return 文件存储路径
     */
    public static String uploadHealthReport(MultipartFile file, String baseDir, Long petId) {
        return uploadFile(file, baseDir, "reports", petId, ALLOWED_DOC_TYPES);
    }

    /**
     * 上传宠物照片
     *
     * @param file 上传的文件
     * @param baseDir 基础存储目录
     * @param petId 宠物ID
     * @return 文件存储路径
     */
    public static String uploadPetPhoto(MultipartFile file, String baseDir, Long petId) {
        return uploadFile(file, baseDir, "photos", petId, ALLOWED_IMAGE_TYPES);
    }

    /**
     * 通用文件上传方法
     *
     * @param file 上传的文件
     * @param baseDir 基础存储目录
     * @param subDir 子目录(如reports, photos)
     * @param petId 宠物ID
     * @param allowedTypes 允许的文件类型
     * @return 文件存储路径
     */
    private static String uploadFile(MultipartFile file, String baseDir, String subDir,
                                     Long petId, List<String> allowedTypes) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BusinessException("不允许的文件类型: " + contentType);
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String uniqueFileName = UUID.randomUUID() + fileExtension;

            // 创建存储路径
            String relativePath = StringUtil.joinPath("pets", petId.toString(), subDir, uniqueFileName);
            Path fullPath = Paths.get(baseDir, relativePath);

            // 确保目录存在
            Files.createDirectories(fullPath.getParent());

            // 保存文件
            Files.write(fullPath, file.getBytes());

            return relativePath;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String baseDir, String relativePath) {
        if (StringUtil.isBlank(relativePath)) return false;

        try {
            Path path = Paths.get(baseDir, relativePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }
}
