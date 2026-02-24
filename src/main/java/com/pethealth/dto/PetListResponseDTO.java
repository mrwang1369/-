package com.pethealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 宠物列表响应DTO
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Data
@Schema(description = "宠物列表响应")
public class PetListResponseDTO {

    @Schema(description = "宠物列表数据")
    private List<PetResponseDTO> pets;

    @Schema(description = "总记录数", example = "15")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize;

    @Schema(description = "总页数", example = "2")
    private Integer totalPages;

    public PetListResponseDTO() {}

    public PetListResponseDTO(List<PetResponseDTO> pets, Long total, Integer pageNum, Integer pageSize) {
        this.pets = pets;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    // 显式添加getter/setter方法
    public List<PetResponseDTO> getPets() { return pets; }
    public void setPets(List<PetResponseDTO> pets) { this.pets = pets; }
    
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
}