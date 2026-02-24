package com.pethealth.service;

import com.pethealth.dto.PetCreateRequestDTO;
import com.pethealth.dto.PetListQueryDTO;
import com.pethealth.dto.PetListResponseDTO;
import com.pethealth.dto.PetResponseDTO;
import com.pethealth.dto.PetUpdateRequestDTO;
import com.pethealth.entity.Pet;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 存储宠物基本信息，支持多只宠物管理 服务类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
public interface PetService extends IService<Pet> {

    /**
     * 根据用户ID查询宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    PetListResponseDTO getPetsByUserId(Long userId, PetListQueryDTO queryDTO);

    /**
     * 创建宠物档案
     * @param userId 用户ID
     * @param createRequest 创建请求
     * @return 宠物信息
     */
    PetResponseDTO createPet(Long userId, PetCreateRequestDTO createRequest);

    /**
     * 获取宠物详细信息
     * @param userId 用户ID
     * @param petId 宠物ID
     * @return 宠物信息
     */
    PetResponseDTO getPetDetail(Long userId, Integer petId);

    /**
     * 更新宠物信息
     * @param userId 用户ID
     * @param petId 宠物ID
     * @param updateRequest 更新请求
     * @return 宠物信息
     */
    PetResponseDTO updatePet(Long userId, Integer petId, PetUpdateRequestDTO updateRequest);

    /**
     * 删除宠物档案
     * @param userId 用户ID
     * @param petId 宠物ID
     */
    void deletePet(Long userId, Integer petId);

    /**
     * 检查宠物归属权
     * @param userId 用户ID
     * @param petId 宠物ID
     * @return 是否拥有权限
     */
    boolean checkPetOwnership(Long userId, Integer petId);
}
