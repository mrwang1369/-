package com.pethealth.service;

import com.pethealth.dto.FileInfoResponseDTO;
import com.pethealth.dto.FileUploadRequestDTO;
import com.pethealth.dto.FileUploadResponseDTO;
import com.pethealth.enums.FileModuleTypeEnum;
import com.pethealth.handler.FileUploadException;
import com.pethealth.service.impl.FileServiceImpl;
import com.pethealth.test.BaseFileTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 文件服务测试类
 *
 * @author pethealth
 * @since 2026-02-27
 */
class FileServiceTest extends BaseFileTest {

    @Autowired
    private FileService fileService;

    @Test
    void testUploadFile_Success() {
        // Arrange - 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.PET_AVATAR.getCode());
        requestDTO.setBusinessId(1L);
        requestDTO.setDescription("测试头像");

        Integer userId = 1;

        // Act - 执行上传操作
        FileUploadResponseDTO response = fileService.uploadFile(mockFile, requestDTO, userId);

        // Assert - 验证结果
        assertNotNull(response, "上传响应不应为null");
        assertNotNull(response.getFileId(), "文件ID不应为null");
        assertEquals("test.jpg", response.getOriginalName(), "原始文件名应正确");
        assertEquals("image/jpeg", response.getFileType(), "文件类型应正确");
        assertEquals("jpg", response.getFileExtension(), "文件扩展名应正确");
        assertEquals(FileModuleTypeEnum.PET_AVATAR.getCode(), response.getModuleType(), "模块类型应正确");
        assertTrue(response.getFileUrl().contains("/files/"), "文件URL应包含files路径");
        assertTrue(response.getFileSize() > 0, "文件大小应大于0");
    }

    @Test
    void testUploadFile_InvalidFileType() {
        // Arrange - 准备不支持的文件类型
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "executable content".getBytes()
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.PET_AVATAR.getCode());
        requestDTO.setBusinessId(1L);

        Integer userId = 1;

        // Act & Assert - 验证抛出异常
        FileUploadException exception = assertThrows(FileUploadException.class, () -> {
            fileService.uploadFile(mockFile, requestDTO, userId);
        }, "应该抛出文件类型不支持异常");

        assertEquals("UNSUPPORTED_FILE_TYPE", exception.getErrorCode(), "异常错误码应正确");
        assertTrue(exception.getMessage().contains("不支持的文件类型"), "异常消息应包含文件类型错误信息");
    }

    @Test
    void testUploadFile_EmptyFile() {
        // Arrange - 准备空文件
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0] // 空内容
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.GENERAL.getCode());
        requestDTO.setBusinessId(1L);

        Integer userId = 1;

        // Act & Assert - 验证抛出异常
        FileUploadException exception = assertThrows(FileUploadException.class, () -> {
            fileService.uploadFile(mockFile, requestDTO, userId);
        }, "应该抛出空文件异常");

        assertEquals("EMPTY_FILE_CONTENT", exception.getErrorCode(), "异常错误码应正确");
    }

    @Test
    void testUploadFile_ExceedsSizeLimit() {
        // Arrange - 准备超大文件（模拟15MB）
        byte[] largeContent = new byte[15 * 1024 * 1024]; // 15MB
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                largeContent
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.MEDICAL_RECORD.getCode());
        requestDTO.setBusinessId(1L);

        Integer userId = 1;

        // Act & Assert - 验证抛出异常
        FileUploadException exception = assertThrows(FileUploadException.class, () -> {
            fileService.uploadFile(mockFile, requestDTO, userId);
        }, "应该抛出文件过大异常");

        assertEquals("FILE_TOO_LARGE", exception.getErrorCode(), "异常错误码应正确");
        assertTrue(exception.getMessage().contains("文件大小超出限制"), "异常消息应包含大小限制信息");
    }

    @Test
    void testUploadPetAvatar_Success() {
        // Arrange - 准备宠物头像文件
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "pet_avatar.png",
                "image/png",
                "png image content".getBytes()
        );

        Long petId = 1L;
        Integer userId = 1;

        // Act - 执行宠物头像上传
        FileUploadResponseDTO response = fileService.uploadPetAvatar(mockFile, petId, userId);

        // Assert - 验证结果
        assertNotNull(response, "宠物头像上传响应不应为null");
        assertEquals(FileModuleTypeEnum.PET_AVATAR.getCode(), response.getModuleType(), "模块类型应为宠物头像");
        assertEquals(petId, response.getBusinessId(), "业务ID应为宠物ID");
        assertTrue(response.getFileUrl().contains("avatars"), "文件应存储在avatars目录下");
    }

    @Test
    void testUploadHealthRecordAttachment_Success() {
        // Arrange - 准备健康记录附件
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "medical_report.pdf",
                "application/pdf",
                "pdf content".getBytes()
        );

        String moduleType = FileModuleTypeEnum.MEDICAL_RECORD.getCode();
        Long recordId = 100L;
        Integer userId = 1;

        // Act - 执行健康记录附件上传
        FileUploadResponseDTO response = fileService.uploadHealthRecordAttachment(mockFile, moduleType, recordId, userId);

        // Assert - 验证结果
        assertNotNull(response, "健康记录附件上传响应不应为null");
        assertEquals(moduleType, response.getModuleType(), "模块类型应正确");
        assertEquals(recordId, response.getBusinessId(), "业务ID应正确");
        assertTrue(response.getFileUrl().contains("medical_records"), "文件应存储在medical_records目录下");
    }

    @Test
    void testGetFileInfo_Success() {
        // Arrange - 先上传一个文件
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_document.pdf",
                "application/pdf",
                "document content".getBytes()
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.CHECKUP_RECORD.getCode());
        requestDTO.setBusinessId(50L);

        Integer userId = 1;
        
        // 先上传文件获取fileId
        FileUploadResponseDTO uploadResponse = fileService.uploadFile(mockFile, requestDTO, userId);
        Long fileId = uploadResponse.getFileId();

        // Act - 获取文件信息
        FileInfoResponseDTO fileInfo = fileService.getFileInfo(fileId);

        // Assert - 验证文件信息
        assertNotNull(fileInfo, "文件信息不应为null");
        assertEquals(fileId, fileInfo.getFileId(), "文件ID应匹配");
        assertEquals("test_document.pdf", fileInfo.getOriginalName(), "原始文件名应正确");
        assertEquals("application/pdf", fileInfo.getFileType(), "文件类型应正确");
        assertEquals(FileModuleTypeEnum.CHECKUP_RECORD.getDisplayName(), fileInfo.getModuleTypeDisplay(), "模块类型显示名称应正确");
        assertNotNull(fileInfo.getFileSizeDisplay(), "文件大小显示不应为null");
    }

    @Test
    void testGetFileInfo_NotFound() {
        // Act & Assert - 验证不存在的文件
        assertThrows(RuntimeException.class, () -> {
            fileService.getFileInfo(99999L); // 不存在的文件ID
        }, "应该抛出文件不存在异常");
    }

    @Test
    void testGetFileListByBusiness_Success() {
        // Arrange - 上传多个同一业务的文件
        Integer userId = 1;
        Long businessId = 200L;
        String moduleType = FileModuleTypeEnum.VACCINATION_RECORD.getCode();

        // 上传第一个文件
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "vaccine1.jpg",
                "image/jpeg",
                "vaccine photo 1".getBytes()
        );
        FileUploadRequestDTO request1 = new FileUploadRequestDTO();
        request1.setModuleType(moduleType);
        request1.setBusinessId(businessId);
        fileService.uploadFile(file1, request1, userId);

        // 上传第二个文件
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "vaccine2.jpg",
                "image/jpeg",
                "vaccine photo 2".getBytes()
        );
        FileUploadRequestDTO request2 = new FileUploadRequestDTO();
        request2.setModuleType(moduleType);
        request2.setBusinessId(businessId);
        fileService.uploadFile(file2, request2, userId);

        // Act - 获取业务相关文件列表
        List<FileInfoResponseDTO> fileList = fileService.getFileListByBusiness(moduleType, businessId);

        // Assert - 验证文件列表
        assertNotNull(fileList, "文件列表不应为null");
        assertEquals(2, fileList.size(), "应该返回2个文件");
        assertTrue(fileList.stream().allMatch(file -> 
            file.getModuleType().equals(moduleType) && 
            file.getBusinessId().equals(businessId)), 
            "所有文件都应该属于指定的模块和业务");
    }

    @Test
    void testGetFileListByUser_Success() {
        // Arrange - 上传多个用户文件
        Integer userId = 2; // 使用不同的用户ID

        // 上传文件1
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "user_file1.png",
                "image/png",
                "user content 1".getBytes()
        );
        FileUploadRequestDTO request1 = new FileUploadRequestDTO();
        request1.setModuleType(FileModuleTypeEnum.GENERAL.getCode());
        request1.setBusinessId(null);
        fileService.uploadFile(file1, request1, userId);

        // 上传文件2
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "user_file2.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "user content 2".getBytes()
        );
        FileUploadRequestDTO request2 = new FileUploadRequestDTO();
        request2.setModuleType(FileModuleTypeEnum.GENERAL.getCode());
        request2.setBusinessId(null);
        fileService.uploadFile(file2, request2, userId);

        // Act - 获取用户文件列表
        List<FileInfoResponseDTO> fileList = fileService.getFileListByUser(userId);

        // Assert - 验证用户文件列表
        assertNotNull(fileList, "用户文件列表不应为null");
        assertTrue(fileList.size() >= 2, "用户应该至少有2个文件");
        assertTrue(fileList.stream().allMatch(file -> 
            file.getUploaderId().equals(userId)), 
            "所有文件都应该属于指定用户");
    }

    @Test
    void testDeleteFile_Success() {
        // Arrange - 先上传文件
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "delete_test.pdf",
                "application/pdf",
                "delete content".getBytes()
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.GENERAL.getCode());
        requestDTO.setBusinessId(null);

        Integer userId = 1;
        FileUploadResponseDTO uploadResponse = fileService.uploadFile(mockFile, requestDTO, userId);
        Long fileId = uploadResponse.getFileId();

        // Act - 删除文件
        boolean result = fileService.deleteFile(fileId, userId);

        // Assert - 验证删除结果
        assertTrue(result, "文件删除应该成功");

        // 验证文件确实已被删除
        assertThrows(RuntimeException.class, () -> {
            fileService.getFileInfo(fileId);
        }, "删除后的文件应该无法获取");
    }

    @Test
    void testDeleteFile_Unauthorized() {
        // Arrange - 上传文件（用户1）
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "unauthorized_test.jpg",
                "image/jpeg",
                "unauthorized content".getBytes()
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.GENERAL.getCode());
        requestDTO.setBusinessId(null);

        Integer uploaderId = 1;
        Integer otherUserId = 2;
        
        FileUploadResponseDTO uploadResponse = fileService.uploadFile(mockFile, requestDTO, uploaderId);
        Long fileId = uploadResponse.getFileId();

        // Act & Assert - 其他用户尝试删除
        assertThrows(RuntimeException.class, () -> {
            fileService.deleteFile(fileId, otherUserId);
        }, "非上传用户应该无法删除文件");
    }

    @Test
    void testBatchDeleteFiles_Success() {
        // Arrange - 上传多个文件
        Integer userId = 1;
        
        // 上传文件1
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "batch1.pdf",
                "application/pdf",
                "batch content 1".getBytes()
        );
        FileUploadResponseDTO response1 = fileService.uploadFile(
                file1, 
                createRequestDTO(FileModuleTypeEnum.GENERAL.getCode(), null), 
                userId
        );

        // 上传文件2
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "batch2.png",
                "image/png",
                "batch content 2".getBytes()
        );
        FileUploadResponseDTO response2 = fileService.uploadFile(
                file2, 
                createRequestDTO(FileModuleTypeEnum.GENERAL.getCode(), null), 
                userId
        );

        // 准备删除列表
        java.util.List<Long> fileIds = java.util.Arrays.asList(response1.getFileId(), response2.getFileId());

        // Act - 批量删除
        int deletedCount = fileService.batchDeleteFiles(fileIds, userId);

        // Assert - 验证删除结果
        assertEquals(2, deletedCount, "应该成功删除2个文件");

        // 验证文件确实已被删除
        assertThrows(RuntimeException.class, () -> {
            fileService.getFileInfo(response1.getFileId());
        }, "第一个文件应该已被删除");

        assertThrows(RuntimeException.class, () -> {
            fileService.getFileInfo(response2.getFileId());
        }, "第二个文件应该已被删除");
    }

    /**
     * 创建文件上传请求DTO的辅助方法
     */
    private FileUploadRequestDTO createRequestDTO(String moduleType, Long businessId) {
        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(moduleType);
        requestDTO.setBusinessId(businessId);
        return requestDTO;
    }
}