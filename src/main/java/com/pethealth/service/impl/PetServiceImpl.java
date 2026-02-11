package com.pethealth.service.impl;

import com.pethealth.entity.Pet;
import com.pethealth.mapper.PetMapper;
import com.pethealth.service.PetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
