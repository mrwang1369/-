package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康时间线响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "健康时间线响应")
public class HealthTimelineDTO {

    @Schema(description = "时间线日期", example = "2024-12-01")
    private LocalDate timelineDate;

    @Schema(description = "该日期的健康事件列表")
    private List<HealthEvent> events;

    @Schema(description = "该日期是否为今天", example = "false")
    private Boolean isToday;

    @Schema(description = "该日期是否有重要事件", example = "true")
    private Boolean hasImportantEvents;

    /**
     * 健康事件内部类
     */
    @Data
    @Schema(description = "健康事件")
    public static class HealthEvent {
        
        @Schema(description = "事件类型", example = "疫苗", allowableValues = {"疫苗", "驱虫", "体检", "病历", "体重记录"})
        private String eventType;

        @Schema(description = "事件标题", example = "狂犬疫苗接种")
        private String title;

        @Schema(description = "事件描述", example = "在爱心宠物医院接种狂犬疫苗")
        private String description;

        @Schema(description = "相关记录ID", example = "1")
        private Integer recordId;

        @Schema(description = "事件图片URL", example = "https://example.com/event.jpg")
        private String imageUrl;

        @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
        private LocalDateTime createTime;

        @Schema(description = "是否为重要事件", example = "true")
        private Boolean isImportant;

        @Schema(description = "事件状态", example = "已完成", allowableValues = {"已完成", "待完成", "已过期"})
        private String status;
    }
}
