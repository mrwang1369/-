package com.pethealth.test.util;

import com.pethealth.dto.*;
import com.pethealth.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 测试数据工厂类 - 提供标准化的测试数据创建方法
 */
public class TestDataFactory {
    
    // ==================== User 相关测试数据 ====================
    
    public static User createUser() {
        User user = new User();
        user.setUserId(1);
        user.setPhone("13800138000");
        user.setPassword("$2a$10$exampleHashedPassword");
        user.setNickname("测试用户");
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setDeleted((byte) 0);
        user.setCreateTime(LocalDateTime.now());
        return user;
    }
    
    public static LoginRequestDTO createLoginRequest() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setLoginType("phone");
        request.setPhone("13800138000");
        request.setPassword("123456");
        return request;
    }
    
    public static RegisterRequestDTO createRegisterRequest() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setPhone("13800138001");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setNickname("新注册用户");
        return request;
    }
    
    // ==================== Pet 相关测试数据 ====================
    
    public static Pet createPet() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setUserId(1);
        pet.setName("测试狗狗");
        pet.setSpecies("狗");
        pet.setBreed("金毛寻回犬");
        pet.setBirthDate(LocalDate.now().minusYears(2));
        pet.setGender("公");
        pet.setWeight(new BigDecimal("25.5"));
        pet.setAllergyHistory("无过敏史");
        pet.setNeuteredStatus(true);
        pet.setDeleted((byte) 0);
        pet.setCreateTime(LocalDateTime.now());
        return pet;
    }
    
    public static PetCreateRequestDTO createPetCreateRequest() {
        PetCreateRequestDTO request = new PetCreateRequestDTO();
        request.setName("测试狗狗");
        request.setSpecies("狗");
        request.setBreed("金毛寻回犬");
        request.setBirthDate(LocalDate.now().minusYears(2));
        request.setGender("公");
        request.setWeight(new BigDecimal("25.5"));
        request.setAllergyHistory("无过敏史");
        request.setNeuteredStatus(true);
        return request;
    }
    
    public static PetUpdateRequestDTO createPetUpdateRequest() {
        PetUpdateRequestDTO request = new PetUpdateRequestDTO();
        request.setName("更新后的狗狗");
        request.setSpecies("狗");
        request.setBreed("拉布拉多");
        request.setBirthDate(LocalDate.now().minusYears(3));
        request.setGender("母");
        request.setWeight(new BigDecimal("28.0"));
        request.setAllergyHistory("对某些食物过敏");
        request.setNeuteredStatus(false);
        return request;
    }
    
    // ==================== 健康记录相关测试数据 ====================
    
    public static VaccinationRecord createVaccinationRecord() {
        VaccinationRecord record = new VaccinationRecord();
        record.setVaccinationId(1);
        record.setPetId(1);
        record.setVaccineName("狂犬疫苗");
        record.setVaccinationDate(LocalDate.now().minusMonths(6));
        record.setNextDueDate(LocalDate.now().plusMonths(6));
        record.setVetInfo("宠物医院");
        record.setNotes("按时接种");
        record.setDeleted((byte) 0);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }
    
    public static VaccinationRecordCreateDTO createVaccinationRecordCreateDTO() {
        VaccinationRecordCreateDTO dto = new VaccinationRecordCreateDTO();
        dto.setPetId(1);
        dto.setVaccineName("狂犬疫苗");
        dto.setVaccinationDate(LocalDate.now().minusMonths(6));
        dto.setNextDueDate(LocalDate.now().plusMonths(6));
        dto.setNotes("按时接种");
        return dto;
    }
    
    public static DewormingRecord createDewormingRecord() {
        DewormingRecord record = new DewormingRecord();
        record.setDewormingId(1);
        record.setPetId(1);
        record.setDewormingType("体内驱虫");
        record.setDrugName("拜耳内虫逃");
        record.setDate(LocalDate.now().minusMonths(3));
        record.setNextDate(LocalDate.now().plusMonths(3));
        record.setNotes("按医嘱服用");
        record.setDeleted((byte) 0);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }
    
    public static MedicalRecord createMedicalRecord() {
        MedicalRecord record = new MedicalRecord();
        record.setMedicalId(1);
        record.setPetId(1);
        record.setHospital("宠物医院");
        record.setDiagnosis("轻微感冒");
        record.setMedicationList("感冒药");
        record.setTreatmentDate(LocalDate.now().minusDays(5));
        record.setNotes("恢复良好");
        record.setDeleted((byte) 0);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }
    
    public static CheckupRecord createCheckupRecord() {
        CheckupRecord record = new CheckupRecord();
        record.setCheckupId(1);
        record.setPetId(1);
        record.setCheckupDate(LocalDate.now().minusMonths(1));
        record.setInstitution("宠物医院");
        record.setResultSummary("体检结果正常");
        record.setDeleted((byte) 0);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }
    
    // ==================== 其他辅助方法 ====================
    
    /**
     * 创建指定数量的测试宠物列表
     */
    public static Pet[] createPetArray(int count) {
        Pet[] pets = new Pet[count];
        for (int i = 0; i < count; i++) {
            Pet pet = createPet();
            pet.setPetId(i + 1);
            pet.setName("测试宠物" + (i + 1));
            pets[i] = pet;
        }
        return pets;
    }
    
    /**
     * 创建测试用的时间戳
     */
    public static LocalDateTime createTestTime() {
        return LocalDateTime.of(2026, 1, 1, 12, 0, 0);
    }
    
    /**
     * 创建有效的手机号码
     */
    public static String createValidPhone() {
        return "138" + String.format("%08d", (int)(Math.random() * 100000000));
    }
    
    /**
     * 创建有效的密码
     */
    public static String createValidPassword() {
        return "Password123!";
    }
}