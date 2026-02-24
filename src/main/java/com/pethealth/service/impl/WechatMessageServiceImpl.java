package com.pethealth.service.impl;

import com.alibaba.fastjson.JSON;
import com.pethealth.dto.ReminderResponseDTO;
import com.pethealth.entity.User;
import com.pethealth.handler.BusinessException;
import com.pethealth.mapper.UserMapper;
import com.pethealth.service.WechatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信消息推送服务实现类
 *
 * @author Mr wang
 * @since 2026-02-24
 */
@Service
@Slf4j
public class WechatMessageServiceImpl implements WechatMessageService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wechat.appid:}")
    private String appId;

    @Value("${wechat.secret:}")
    private String appSecret;

    @Value("${wechat.template.upcoming-reminder:}")
    private String upcomingReminderTemplateId;

    @Value("${wechat.template.overdue-reminder:}")
    private String overdueReminderTemplateId;

    @Value("${wechat.template.completion-confirmation:}")
    private String completionConfirmationTemplateId;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public boolean sendReminderNotification(Integer userId, ReminderResponseDTO reminder, String templateId) {
        try {
            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null || user.getOpenid() == null) {
                log.warn("用户{}没有绑定微信openid，无法发送消息", userId);
                return false;
            }

            // 构造消息内容
            Map<String, Object> messageData = buildMessageData(reminder, templateId);

            // 发送微信模板消息
            boolean result = sendTemplateMessage(user.getOpenid(), templateId, messageData);
            
            if (result) {
                log.info("向用户{}发送微信提醒成功: reminderId={}", userId, reminder.getReminderId());
            } else {
                log.warn("向用户{}发送微信提醒失败: reminderId={}", userId, reminder.getReminderId());
            }
            
            return result;
        } catch (Exception e) {
            log.error("发送微信提醒时发生错误: userId={}, reminderId={}", userId, reminder.getReminderId(), e);
            return false;
        }
    }

    @Override
    public boolean sendUpcomingReminder(Integer userId, ReminderResponseDTO reminder) {
        return sendReminderNotification(userId, reminder, upcomingReminderTemplateId);
    }

    @Override
    public boolean sendOverdueReminder(Integer userId, ReminderResponseDTO reminder) {
        return sendReminderNotification(userId, reminder, overdueReminderTemplateId);
    }

    @Override
    public boolean sendCompletionConfirmation(Integer userId, ReminderResponseDTO reminder) {
        return sendReminderNotification(userId, reminder, completionConfirmationTemplateId);
    }

    /**
     * 构造微信模板消息数据
     */
    private Map<String, Object> buildMessageData(ReminderResponseDTO reminder, String templateId) {
        Map<String, Object> data = new HashMap<>();
        
        // 根据不同的模板ID构造不同的消息内容
        if (templateId.equals(upcomingReminderTemplateId)) {
            // 即将到期提醒模板
            data.put("thing1", Map.of("value", reminder.getTitle())); // 提醒标题
            data.put("time2", Map.of("value", reminder.getDueDate().format(DATE_FORMATTER))); // 到期时间
            data.put("thing3", Map.of("value", reminder.getReminderType())); // 提醒类型
            data.put("thing4", Map.of("value", reminder.getNotes() != null ? reminder.getNotes() : "请及时处理")); // 备注
        } else if (templateId.equals(overdueReminderTemplateId)) {
            // 逾期提醒模板
            data.put("thing1", Map.of("value", reminder.getTitle())); // 提醒标题
            data.put("time2", Map.of("value", reminder.getDueDate().format(DATE_FORMATTER))); // 逾期时间
            data.put("thing3", Map.of("value", reminder.getReminderType())); // 提醒类型
            data.put("thing4", Map.of("value", "已逾期，请尽快处理")); // 提示信息
        } else if (templateId.equals(completionConfirmationTemplateId)) {
            // 完成确认模板
            data.put("thing1", Map.of("value", reminder.getTitle())); // 提醒标题
            data.put("time2", Map.of("value", reminder.getCompletedTime() != null ? 
                    reminder.getCompletedTime().format(DATE_FORMATTER) : "")); // 完成时间
            data.put("thing3", Map.of("value", "已完成")); // 状态
            data.put("thing4", Map.of("value", "感谢您的及时处理")); // 感谢语
        }
        
        return data;
    }

    /**
     * 发送微信模板消息
     */
    private boolean sendTemplateMessage(String openid, String templateId, Map<String, Object> data) {
        try {
            // 获取access_token（这里简化处理，实际项目中需要缓存token）
            String accessToken = getAccessToken();
            if (accessToken == null) {
                throw new BusinessException("获取微信access_token失败");
            }

            // 构造请求URL
            String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken);

            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("touser", openid);
            requestBody.put("template_id", templateId);
            requestBody.put("data", data);

            // 发送POST请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(url, request, String.class);
            
            // 解析响应
            Map<String, Object> responseMap = JSON.parseObject(response, Map.class);
            Integer errcode = (Integer) responseMap.get("errcode");
            
            return errcode != null && errcode == 0;
        } catch (Exception e) {
            log.error("发送微信模板消息失败", e);
            return false;
        }
    }

    /**
     * 获取微信access_token
     */
    private String getAccessToken() {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, appSecret
            );

            String response = restTemplate.getForObject(url, String.class);
            Map<String, Object> responseMap = JSON.parseObject(response, Map.class);
            
            Integer errcode = (Integer) responseMap.get("errcode");
            if (errcode != null && errcode != 0) {
                log.error("获取access_token失败: errcode={}, errmsg={}", 
                        errcode, responseMap.get("errmsg"));
                return null;
            }
            
            return (String) responseMap.get("access_token");
        } catch (Exception e) {
            log.error("获取微信access_token时发生错误", e);
            return null;
        }
    }
}