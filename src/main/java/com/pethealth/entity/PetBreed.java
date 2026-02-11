package com.pethealth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 宠物品种字典表
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("pet_breed")
public class PetBreed implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "breed_id", type = IdType.AUTO)
    private Integer breedId;

    /**
     * 宠物类型
     */
    private String species;

    /**
     * 品种名称
     */
    private String breedName;

    private LocalDateTime createTime;
}
