package com.pethealth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethealth.dto.PetCreateRequestDTO;
import com.pethealth.dto.PetListQueryDTO;
import com.pethealth.dto.PetListResponseDTO;
import com.pethealth.dto.PetResponseDTO;
import com.pethealth.dto.PetUpdateRequestDTO;
import com.pethealth.entity.Pet;
import com.pethealth.handler.BusinessException;
import com.pethealth.handler.ResourceNotFoundException;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.PetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 存储宠物基本信息，支持多只宠物管理 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    @Override
    public PetListResponseDTO getPetsByUserId(Long userId, PetListQueryDTO queryDTO) {
        // 构建查询条件
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("deleted", 0)
                   .orderByDesc("create_time");

        // 添加查询条件
        if (StringUtils.hasText(queryDTO.getSpecies())) {
            queryWrapper.like("species", queryDTO.getSpecies());
        }
        if (StringUtils.hasText(queryDTO.getBreed())) {
            queryWrapper.like("breed", queryDTO.getBreed());
        }
        if (StringUtils.hasText(queryDTO.getNameKeyword())) {
            queryWrapper.like("name", queryDTO.getNameKeyword());
        }

        // 分页查询
        IPage<Pet> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Pet> resultPage = page(page, queryWrapper);

        // 转换为DTO
        List<PetResponseDTO> petDTOs = resultPage.getRecords().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PetListResponseDTO(
                petDTOs,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }

    @Override
    @Transactional
    public PetResponseDTO createPet(Long userId, PetCreateRequestDTO createRequest) {
        // 检查同名宠物是否已存在
        QueryWrapper<Pet> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("user_id", userId)
                   .eq("name", createRequest.getName())
                   .eq("deleted", 0);
        
        if (count(checkWrapper) > 0) {
            throw new BusinessException("该用户下已存在同名宠物");
        }

        // 创建宠物实体
        Pet pet = new Pet();
        BeanUtils.copyProperties(createRequest, pet);
        pet.setUserId(userId.intValue());
        pet.setCreateTime(LocalDateTime.now());
        pet.setDeleted((byte) 0);

        // 保存到数据库
        if (!save(pet)) {
            throw new BusinessException("创建宠物档案失败");
        }

        return convertToResponseDTO(pet);
    }

    @Override
    public PetResponseDTO getPetDetail(Long userId, Integer petId) {
        // 检查宠物是否存在且属于该用户
        Pet pet = getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }
        
        if (!pet.getUserId().equals(userId.intValue())) {
            throw new BusinessException("无权访问该宠物信息");
        }

        return convertToResponseDTO(pet);
    }

    @Override
    @Transactional
    public PetResponseDTO updatePet(Long userId, Integer petId, PetUpdateRequestDTO updateRequest) {
        // 检查宠物归属权
        if (!checkPetOwnership(userId, petId)) {
            throw new BusinessException("无权修改该宠物信息");
        }

        // 获取原宠物信息
        Pet pet = getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new ResourceNotFoundException("宠物不存在");
        }

        // 检查是否重名（排除自己）
        if (!pet.getName().equals(updateRequest.getName())) {
            QueryWrapper<Pet> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("user_id", userId)
                       .eq("name", updateRequest.getName())
                       .eq("deleted", 0)
                       .ne("pet_id", petId);
            
            if (count(checkWrapper) > 0) {
                throw new BusinessException("该用户下已存在同名宠物");
            }
        }

        // 更新宠物信息
        BeanUtils.copyProperties(updateRequest, pet);
        pet.setUpdateTime(LocalDateTime.now());

        if (!updateById(pet)) {
            throw new BusinessException("更新宠物信息失败");
        }

        return convertToResponseDTO(pet);
    }

    @Override
    @Transactional
    public void deletePet(Long userId, Integer petId) {
        // 检查宠物归属权
        if (!checkPetOwnership(userId, petId)) {
            throw new BusinessException("无权删除该宠物");
        }

        // 逻辑删除
        Pet pet = new Pet();
        pet.setPetId(petId);
        pet.setDeleted((byte) 1);
        pet.setUpdateTime(LocalDateTime.now());

        if (!updateById(pet)) {
            throw new BusinessException("删除宠物失败");
        }
    }

    @Override
    public boolean checkPetOwnership(Long userId, Integer petId) {
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId)
                   .eq("user_id", userId)
                   .eq("deleted", 0);
        
        return count(queryWrapper) > 0;
    }

    /**
     * 将Pet实体转换为PetResponseDTO
     */
    private PetResponseDTO convertToResponseDTO(Pet pet) {
        PetResponseDTO dto = new PetResponseDTO();
        BeanUtils.copyProperties(pet, dto);
        
        // 计算宠物年龄
        if (pet.getBirthDate() != null) {
            dto.setAge(calculateAge(pet.getBirthDate()));
        }
        
        return dto;
    }

    /**
     * 计算宠物年龄
     */
    private String calculateAge(LocalDate birthDate) {
        Period period = Period.between(birthDate, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();
        
        if (years > 0) {
            if (months > 0) {
                return years + "岁" + months + "个月";
            } else {
                return years + "岁";
            }
        } else {
            return months + "个月";
        }
    }
}