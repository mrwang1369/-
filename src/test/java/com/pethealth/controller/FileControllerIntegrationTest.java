package com.pethealth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.dto.FileUploadRequestDTO;
import com.pethealth.enums.FileModuleTypeEnum;
import com.pethealth.test.BaseFileTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 文件控制器集成测试类
 *
 * @author pethealth
 * @since 2026-02-27
 */
@AutoConfigureMockMvc
class FileControllerIntegrationTest extends BaseFileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUploadFile_Success() throws Exception {
        // Arrange - 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "integration_test.jpg",
                "image/jpeg",
                "integration test content".getBytes()
        );

        FileUploadRequestDTO requestDTO = new FileUploadRequestDTO();
        requestDTO.setModuleType(FileModuleTypeEnum.PET_AVATAR.getCode());
        requestDTO.setBusinessId(1L);
        requestDTO.setDescription("集成测试头像");

        // Act & Assert - 执行上传并验证响应
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload")
                .file(file)
                .param("moduleType", requestDTO.getModuleType())
                .param("businessId", requestDTO.getBusinessId().toString())
                .param("description", requestDTO.getDescription())
                .header("Authorization", "Bearer test-token")) // 模拟认证头
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.originalName").value("integration_test.jpg"))
                .andExpect(jsonPath("$.data.moduleType").value(FileModuleTypeEnum.PET_AVATAR.getCode()))
                .andExpect(jsonPath("$.data.fileUrl").exists());
    }

    @Test
    void testUploadFile_MissingFile() throws Exception {
        // Act & Assert - 测试缺少文件的情况
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload")
                .param("moduleType", FileModuleTypeEnum.GENERAL.getCode())
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadFile_Unauthorized() throws Exception {
        // Arrange - 准备文件但不提供认证信息
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "unauthorized_test.png",
                "image/png",
                "unauthorized content".getBytes()
        );

        // Act & Assert - 验证未认证访问被拒绝
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload")
                .file(file)
                .param("moduleType", FileModuleTypeEnum.GENERAL.getCode()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("用户未登录"));
    }

    @Test
    void testUploadPetAvatar_Success() throws Exception {
        // Arrange - 准备宠物头像文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "pet_avatar_integration.png",
                "image/png",
                "pet avatar integration test".getBytes()
        );

        Long petId = 10L;

        // Act & Assert - 上传宠物头像
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload-avatar/{petId}", petId)
                .file(file)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.moduleType").value(FileModuleTypeEnum.PET_AVATAR.getCode()))
                .andExpect(jsonPath("$.data.businessId").value(petId));
    }

    @Test
    void testUploadHealthRecordAttachment_Success() throws Exception {
        // Arrange - 准备健康记录附件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "health_record_integration.pdf",
                "application/pdf",
                "health record integration test".getBytes()
        );

        String moduleType = FileModuleTypeEnum.MEDICAL_RECORD.getCode();
        Long recordId = 200L;

        // Act & Assert - 上传健康记录附件
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload-health-record/{moduleType}/{recordId}", 
                        moduleType, recordId)
                .file(file)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.moduleType").value(moduleType))
                .andExpect(jsonPath("$.data.businessId").value(recordId));
    }

    @Test
    void testGetFileInfo_Success() throws Exception {
        // 注意：由于这是集成测试，需要先上传文件才能测试获取文件信息
        // 这里主要测试接口可达性和基本响应结构
        
        Long fileId = 1L; // 假设存在的文件ID

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/{fileId}", fileId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFileListByBusiness_Success() throws Exception {
        // Arrange
        String moduleType = FileModuleTypeEnum.VACCINATION_RECORD.getCode();
        Long businessId = 100L;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/list/business/{moduleType}/{businessId}", 
                        moduleType, businessId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetFileListByUser_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/list/user")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        // Arrange
        Long fileId = 1L;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/files/{fileId}", fileId)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testBatchDeleteFiles_Success() throws Exception {
        // Arrange
        java.util.List<Long> fileIds = java.util.Arrays.asList(1L, 2L, 3L);
        String jsonContent = objectMapper.writeValueAsString(fileIds);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/files/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testFileDownload_EndpointExists() throws Exception {
        // 测试文件下载端点是否存在
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/download/{moduleType}/{fileName}", 
                        "avatars", "test.jpg"))
                .andExpect(status().isOk()); // 静态资源处理器会处理这个请求
    }

    @Test
    void testInvalidModuleType_UploadShouldFail() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid_module.jpg",
                "image/jpeg",
                "invalid module test".getBytes()
        );

        // Act & Assert - 使用无效的模块类型
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload")
                .file(file)
                .param("moduleType", "invalid_module_type")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnsupportedFileType_UploadShouldFail() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "unsupported.exe",
                "application/octet-stream",
                "executable content".getBytes()
        );

        // Act & Assert - 上传不支持的文件类型
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload")
                .file(file)
                .param("moduleType", FileModuleTypeEnum.GENERAL.getCode())
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isBadRequest());
    }
}