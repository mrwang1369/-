package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检记录响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "体检记录响应")
public class CheckupRecordResponseDTO {

    @Schema(description = "体检记录ID", example = "1")
    private Integer checkupId;

    @Schema(description = "宠物ID", example = "1")
    private Integer petId;

    @Schema(description = "体检日期", example = "2024-11-10")
    private LocalDate checkupDate;

    @Schema(description = "体检机构", example = "爱心宠物医院")
    private String institution;

    @Schema(description = "体检结果摘要", example = "身体健康，各项指标正常")
    private String resultSummary;

    @Schema(description = "报告图片URL", example = "https://example.com/checkup_report.jpg")
    private String reportImageUrl;

    @Schema(description = "备注", example = "建议保持当前饮食和运动量")
    private String notes;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "体检距今天数", example = "15")
    private Integer daysAgo;

    @Schema(description = "是否为近期体检", example = "true")
    private Boolean isRecent;

    // 显式添加getter/setter方法
    public Integer getCheckupId() { return checkupId; }
    public void setCheckupId(Integer checkupId) { this.checkupId = checkupId; }
    
    public Integer getPetId() { return petId; }
    public void setPetId(Integer petId) { this.petId = petId; }
    
    public LocalDate getCheckupDate() { return checkupDate; }
    public void setCheckupDate(LocalDate checkupDate) { this.checkupDate = checkupDate; }
    
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    
    public String getResultSummary() { return resultSummary; }
    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
    
    public String getReportImageUrl() { return reportImageUrl; }
    public void setReportImageUrl(String reportImageUrl) { this.reportImageUrl = reportImageUrl; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public Integer getDaysAgo() { return daysAgo; }
    public void setDaysAgo(Integer daysAgo) { this.daysAgo = daysAgo; }
    
    public Boolean getIsRecent() { return isRecent; }
    public void setIsRecent(Boolean isRecent) { this.isRecent = isRecent; }
}