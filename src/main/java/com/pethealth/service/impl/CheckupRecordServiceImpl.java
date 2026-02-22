package com.pethealth.service.impl;

import com.pethealth.entity.CheckupRecord;
import com.pethealth.mapper.CheckupRecordMapper;
import com.pethealth.service.CheckupRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 记录体检信息，支持图片上传 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class CheckupRecordServiceImpl extends ServiceImpl<CheckupRecordMapper, CheckupRecord> implements CheckupRecordService {

}
