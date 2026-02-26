-- 测试数据初始化脚本

-- 插入测试用户数据
INSERT INTO `user` (`user_id`, `phone`, `password`, `nickname`, `avatar_url`, `openid`) VALUES
(1, '13800138001', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUVWX', '测试用户1', 'https://example.com/avatar1.jpg', 'openid_test_user_1'),
(2, '13800138002', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUVWX', '测试用户2', 'https://example.com/avatar2.jpg', 'openid_test_user_2');

-- 插入测试宠物数据
INSERT INTO `pet` (`pet_id`, `user_id`, `name`, `species`, `breed`, `birth_date`, `gender`, `weight`, `allergy_history`, `neutered_status`, `avatar_url`) VALUES
(1, 1, '小白', '狗', '金毛寻回犬', '2022-01-01', '公', 25.5, '无过敏史', TRUE, 'https://example.com/pet1.jpg'),
(2, 1, '小花', '猫', '英短', '2021-05-15', '母', 4.2, '对某些猫粮过敏', FALSE, 'https://example.com/pet2.jpg'),
(3, 2, '旺财', '狗', '拉布拉多', '2023-03-10', '公', 30.0, '无', TRUE, 'https://example.com/pet3.jpg');

-- 插入测试提醒数据
INSERT INTO `reminder` (`reminder_id`, `user_id`, `pet_id`, `reminder_type`, `title`, `due_date`, `status`, `repeat_cycle`, `notes`) VALUES
(1, 1, 1, '喂食', '每日喂食', '2026-02-26 08:00:00', '待完成', '每日', '按时喂食优质狗粮'),
(2, 1, 1, '散步', '每日遛狗', '2026-02-26 18:00:00', '待完成', '每日', '至少遛狗1小时'),
(3, 1, 2, '疫苗', '狂犬疫苗接种', '2026-03-15 10:00:00', '待完成', '每年', '记得带上疫苗本'),
(4, 2, 3, '体检', '年度体检', '2026-04-01 09:00:00', '待完成', '每年', '提前预约宠物医院');

-- 插入测试疫苗记录数据
INSERT INTO `vaccination_record` (`vaccination_id`, `pet_id`, `vaccine_name`, `vaccination_date`, `next_due_date`, `vet_info`, `notes`) VALUES
(1, 1, '狂犬疫苗', '2025-09-01', '2026-09-01', '爱心宠物医院 张医生', '注射剂量正常，无不良反应'),
(2, 1, '六联疫苗', '2025-10-15', '2026-10-15', '爱心宠物医院 张医生', '按计划完成第三针'),
(3, 2, '猫三联疫苗', '2025-11-20', '2026-11-20', '猫咪专科医院 李医生', '幼猫疫苗接种完成');

-- 插入测试驱虫记录数据
INSERT INTO `deworming_record` (`deworming_id`, `pet_id`, `deworming_type`, `drug_name`, `date`, `next_date`, `notes`) VALUES
(1, 1, '体内', '拜宠清', '2026-01-15', '2026-04-15', '按体重给药，无呕吐腹泻'),
(2, 1, '体外', '福来恩', '2026-02-01', '2026-03-01', '滴剂使用正常'),
(3, 2, '体内', '海乐妙', '2026-01-20', '2026-04-20', '猫咪服用顺利');

-- 插入测试体检记录数据
INSERT INTO `checkup_record` (`checkup_id`, `pet_id`, `checkup_date`, `institution`, `result_summary`, `report_image_url`) VALUES
(1, 1, '2026-01-10', '爱心宠物医院', '身体健康，各项指标正常，建议继续保持现有饮食和运动习惯', 'https://example.com/report1.jpg'),
(2, 2, '2025-12-20', '猫咪专科医院', '体检结果良好，体重增长正常，牙齿健康状况佳', 'https://example.com/report2.jpg');

-- 插入测试病历记录数据
INSERT INTO `medical_record` (`medical_id`, `pet_id`, `hospital`, `diagnosis`, `medication_list`, `treatment_date`, `notes`) VALUES
(1, 1, '爱心宠物医院', '轻微皮肤过敏', '氯雷他定片，每日一次，连续服用一周', '2026-02-05', '避免接触过敏原，定期复查'),
(2, 2, '猫咪专科医院', '上呼吸道感染', '阿莫西林克拉维酸钾，每日两次', '2026-01-25', '注意保暖，多喝水');

-- 插入测试成长事件数据
INSERT INTO `growth_event` (`event_id`, `pet_id`, `event_type`, `event_date`, `description`, `image_url`, `weight_value`) VALUES
(1, 1, '照片', '2026-01-01 10:00:00', '新年快乐！小白又长大了一岁', 'https://example.com/newyear2026.jpg', NULL),
(2, 1, '体重记录', '2026-01-15 09:00:00', '月度体重测量', NULL, 25.5),
(3, 1, '疫苗', '2026-01-20 14:00:00', '狂犬疫苗接种', NULL, NULL),
(4, 2, '照片', '2025-12-25 15:00:00', '圣诞节拍照留念', 'https://example.com/christmas2025.jpg', NULL);

-- 插入测试症状记录数据
INSERT INTO `symptom_record` (`symptom_id`, `pet_id`, `user_id`, `symptoms_text`, `analysis_result`, `emergency_level`, `suggestions`) VALUES
(1, 1, 1, '食欲不振，精神萎靡', '可能是消化不良或轻微肠胃炎', '建议观察', '先禁食12小时，观察症状变化，如有加重及时就医'),
(2, 2, 1, '频繁抓挠耳朵', '疑似耳螨感染', '尽快就医', '建议立即前往宠物医院进行专业检查和治疗');

-- 插入测试服务点数据
INSERT INTO `service_point` (`point_id`, `name`, `type`, `address`, `phone`, `latitude`, `longitude`, `rating`, `business_hours`) VALUES
(1, '爱心宠物医院', '医院', '北京市朝阳区建国路88号', '010-12345678', 39.9042, 116.4074, 4.8, '周一至周日 8:00-20:00'),
(2, '宠物之家商店', '宠物店', '北京市海淀区中关村大街1号', '010-87654321', 39.9163, 116.3972, 4.5, '周一至周日 9:00-21:00'),
(3, '猫咪专科医院', '医院', '北京市西城区金融街丙1号', '010-11111111', 39.9095, 116.3742, 4.9, '周二至周日 9:00-18:00');

-- 插入测试宠物品种数据
INSERT INTO `pet_breed` (`breed_id`, `species`, `breed_name`) VALUES
(1, '狗', '金毛寻回犬'),
(2, '狗', '拉布拉多'),
(3, '狗', '哈士奇'),
(4, '猫', '英国短毛猫'),
(5, '猫', '美国短毛猫'),
(6, '猫', '布偶猫');