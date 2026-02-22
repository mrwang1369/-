package com.pethealth.service.impl;

import com.pethealth.entity.VaccinationRecord;
import com.pethealth.mapper.VaccinationRecordMapper;
import com.pethealth.service.VaccinationRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 记录疫苗接种信息，用于健康计划和提醒 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class VaccinationRecordServiceImpl extends ServiceImpl<VaccinationRecordMapper, VaccinationRecord> implements VaccinationRecordService {

}
