package com.pethealth.mapper;

import com.pethealth.entity.Pet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 存储宠物基本信息，支持多只宠物管理 Mapper 接口
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface PetMapper extends BaseMapper<Pet> {

    /**
     * 根据用户ID删除宠物数据（测试用）
     */
    @Delete("UPDATE pet SET deleted = 1 WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除所有测试数据（仅限测试环境）
     */
    @Delete("UPDATE pet SET deleted = 1 WHERE 1 = 1")
    int deleteAllTestData();
}
