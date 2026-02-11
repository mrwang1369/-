package com.pethealth.service.impl;

import com.pethealth.entity.ServicePoint;
import com.pethealth.mapper.ServicePointMapper;
import com.pethealth.service.ServicePointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储周边服务信息，支持地图集成 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class ServicePointServiceImpl extends ServiceImpl<ServicePointMapper, ServicePoint> implements ServicePointService {

}
