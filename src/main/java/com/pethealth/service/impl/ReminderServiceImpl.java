package com.pethealth.service.impl;

import com.pethealth.entity.Reminder;
import com.pethealth.mapper.ReminderMapper;
import com.pethealth.service.ReminderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储提醒事项，支持微信推送 服务实现类
 * </p>
 *
 * @author Mr wang
 * @since 2026-02-11
 */
@Service
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder> implements ReminderService {

}
