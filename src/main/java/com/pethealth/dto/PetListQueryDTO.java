package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 宠物列表查询参数DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "宠物列表查询参数")
public class PetListQueryDTO {

    @Schema(description = "宠物种类", example = "狗")
    private String species;

    @Schema(description = "宠物品种", example = "金毛寻回犬")
    private String breed;

    @Schema(description = "宠物姓名关键字", example = "小")
    private String nameKeyword;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
