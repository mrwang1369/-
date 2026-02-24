package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建宠物档案请求DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "创建宠物档案请求")
public class PetCreateRequestDTO {

    @NotBlank(message = "宠物姓名不能为空")
    @Length(max = 50, message = "宠物姓名长度不能超过50个字符")
    @Schema(description = "宠物姓名", example = "小白")
    private String name;

    @NotBlank(message = "宠物种类不能为空")
    @Schema(description = "宠物种类", example = "狗", allowableValues = {"猫", "狗", "其他"})
    private String species;

    @Schema(description = "宠物品种", example = "金毛寻回犬")
    @Length(max = 50, message = "宠物品种长度不能超过50个字符")
    private String breed;

    @PastOrPresent(message = "出生日期不能晚于今天")
    @Schema(description = "出生日期", example = "2023-01-01")
    private LocalDate birthDate;

    @Schema(description = "性别", example = "公", allowableValues = {"公", "母"})
    private String gender;

    @Positive(message = "体重必须大于0")
    @Schema(description = "体重(kg)", example = "25.5")
    private BigDecimal weight;

    @Schema(description = "过敏史", example = "无")
    @Length(max = 500, message = "过敏史长度不能超过500个字符")
    private String allergyHistory;

    @Schema(description = "绝育状态", example = "true")
    private Boolean neuteredStatus = false;

    @Schema(description = "宠物头像URL", example = "https://example.com/avatar.jpg")
    @Length(max = 200, message = "头像URL长度不能超过200个字符")
    private String avatarUrl;

    // 显式添加getter/setter方法
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    
    public String getAllergyHistory() { return allergyHistory; }
    public void setAllergyHistory(String allergyHistory) { this.allergyHistory = allergyHistory; }
    
    public Boolean getNeuteredStatus() { return neuteredStatus; }
    public void setNeuteredStatus(Boolean neuteredStatus) { this.neuteredStatus = neuteredStatus; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}