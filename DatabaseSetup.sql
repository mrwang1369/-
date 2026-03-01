-- 宠物健康管理系统数据库初始化脚本
-- 数据库: bishe
-- 创建时间: 2026-02-22

-- 用户表(支持微信一键登录和手机号注册)
CREATE TABLE IF NOT EXISTS `user`(
    `user_id` INT PRIMARY KEY AUTO_INCREMENT,
    `openid` VARCHAR(64) UNIQUE COMMENT '微信唯一标识',
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `password` VARCHAR(100) COMMENT '加密密码(手机号注册时使用)',
    `nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称',
    `avatar_url` VARCHAR(200) COMMENT '头像 URL',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_phone`(`phone`),
    INDEX `idx_openid`(`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储用户基本信息，支持微信和手机号登录';

-- 宠物表(核心档案信息)
CREATE TABLE IF NOT EXISTS `pet`(
    `pet_id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL COMMENT '关联用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '宠物姓名',
    `species` ENUM('猫','狗','其他') NOT NULL COMMENT '宠物类型',
    `breed` VARCHAR(50) COMMENT '品种(可搜索选择)',
    `birth_date` DATE COMMENT '出生日期',
    `gender` ENUM('公','母') COMMENT '性别',
    `weight` DECIMAL(5,2) COMMENT '体重(kg)',
    `allergy_history` TEXT COMMENT '过敏史',
    `neutered_status` BOOLEAN DEFAULT FALSE COMMENT '绝育状态',
    `avatar_url` VARCHAR(200) COMMENT '宠物头像URL',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_user_id`(`user_id`),
    INDEX `idx_species`(`species`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储宠物基本信息，支持多只宠物管理';

-- 疫苗记录表
CREATE TABLE IF NOT EXISTS `vaccination_record`(
    `vaccination_id` INT PRIMARY KEY AUTO_INCREMENT,
    `pet_id` INT NOT NULL COMMENT '关联宠物ID',
    `vaccine_name` VARCHAR(50) NOT NULL COMMENT '疫苗名称',
    `vaccination_date` DATE NOT NULL COMMENT '接种日期',
    `next_due_date` DATE NOT NULL COMMENT '下次接种日期',
    `vet_info` VARCHAR(100) COMMENT '兽医信息',
    `proof_image_url` VARCHAR(200) COMMENT '接种证明照片 URL',
    `notes` TEXT COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE,
    INDEX `idx_pet_id`(`pet_id`),
    INDEX `idx_next_due_date`(`next_due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录疫苗接种信息，用于健康计划和提醒';

-- 驱虫记录表
CREATE TABLE IF NOT EXISTS `deworming_record`(
    `deworming_id` INT PRIMARY KEY AUTO_INCREMENT,
    `pet_id` INT NOT NULL COMMENT '关联宠物ID',
    `deworming_type` ENUM('体内','体外') NOT NULL COMMENT '驱虫类型',
    `drug_name` VARCHAR(50) NOT NULL COMMENT '药物名称',
    `date` DATE NOT NULL COMMENT '驱虫日期',
    `next_date` DATE COMMENT '下次驱虫日期',
    `notes` TEXT COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE,
    INDEX `idx_pet_id`(`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录驱虫信息，支持周期提醒';

-- 体检报告记录表
CREATE TABLE IF NOT EXISTS `checkup_record`(
    `checkup_id` INT PRIMARY KEY AUTO_INCREMENT,
    `pet_id` INT NOT NULL COMMENT '关联宠物ID',
    `checkup_date` DATE NOT NULL COMMENT '体检日期',
    `institution` VARCHAR(100) NOT NULL COMMENT '体检机构',
    `result_summary` TEXT COMMENT '结果摘要',
    `report_image_url` VARCHAR(200) COMMENT '报告照片或PDF URL',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE,
    INDEX `idx_pet_id`(`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录体检信息，支持图片上传';

-- 病历与用药记录表
CREATE TABLE IF NOT EXISTS `medical_record`(
    `medical_id` INT PRIMARY KEY AUTO_INCREMENT,
    `pet_id` INT NOT NULL COMMENT '关联宠物ID',
    `hospital` VARCHAR(100) NOT NULL COMMENT '就诊医院',
    `diagnosis` TEXT NOT NULL COMMENT '诊断结果',
    `medication_list` TEXT COMMENT '用药清单',
    `treatment_date` DATE NOT NULL COMMENT '就诊日期',
    `notes` TEXT COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE,
    INDEX `idx_pet_id`(`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录病历和用药信息，可设置用药提醒';

-- 提醒事项表(自动生成和自定义)
CREATE TABLE IF NOT EXISTS `reminder`(
    `reminder_id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL COMMENT '关联用户ID',
    `pet_id` INT COMMENT '关联宠物ID(可为NULL表示自定义提醒)',
    `reminder_type` VARCHAR(50) NOT NULL COMMENT '类型(如疫苗、驱虫、喂食)',
    `title` VARCHAR(100) NOT NULL COMMENT '提醒标题',
    `due_date` DATETIME NOT NULL COMMENT '到期时间',
    `status` ENUM('待完成','已完成') DEFAULT '待完成' COMMENT '状态',
    `repeat_cycle` VARCHAR(20) COMMENT '重复周期(如每日、每周)',
    `notes` TEXT COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `completed_time` DATETIME COMMENT '完成时间',
    FOREIGN KEY(`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY(`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE SET NULL,
    INDEX `idx_user_id`(`user_id`),
    INDEX `idx_pet_id`(`pet_id`),
    INDEX `idx_due_date`(`due_date`),
    INDEX `idx_status`(`status`),
    INDEX `idx_user_pet_status_due`(`user_id`,`pet_id`,`status`,`due_date`) COMMENT '优化提醒列表查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储提醒事项，支持微信推送';

-- AI症状初筛记录表
CREATE TABLE IF NOT EXISTS `symptom_record`(
    `symptom_id` INT PRIMARY KEY AUTO_INCREMENT,
    `pet_id` INT NOT NULL COMMENT '关联宠物 ID',
    `user_id` INT NOT NULL COMMENT '关联用户ID',
    `symptoms_text` TEXT NOT NULL COMMENT '症状关键词输入',
    `analysis_result` TEXT COMMENT 'AI分析结果(可能病因)',
    `emergency_level` ENUM('建议观察','尽快就医','紧急') COMMENT '紧急程度',
    `suggestions` TEXT COMMENT '处理建议',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE,
    FOREIGN KEY(`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_pet_id`(`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录症状输入和 AI分析，用于就医引导';

-- 周边服务点表(医院、宠物店等)
CREATE TABLE IF NOT EXISTS `service_point`(
    `point_id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '服务点名称',
    `type` ENUM('医院','宠物店','其他') NOT NULL COMMENT '服务类型',
    `address` VARCHAR(200) NOT NULL COMMENT '地址',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `latitude` DECIMAL(10,6) NOT NULL COMMENT '纬度(用于LBS)',
    `longitude` DECIMAL(10,6) NOT NULL COMMENT '经度',
    `rating` DECIMAL(3,2) DEFAULT 0.00 COMMENT '用户评分',
    `business_hours` VARCHAR(100) COMMENT '营业时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_name_address`(`name`,`address`) COMMENT '防止重复添加相同地点的服务点',
    INDEX `idx_location`(`latitude`,`longitude`),
    INDEX `idx_type`(`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储周边服务信息，支持地图集成';

-- 成长事件表(用于时光轴聚合)
CREATE TABLE IF NOT EXISTS `growth_event`(
    `event_id` INT PRIMARY KEY AUTO_INCREMENT,
    `pet_id` INT NOT NULL COMMENT '关联宠物ID',
    `event_type` ENUM('照片','体重记录','疫苗','驱虫','体检','病历') NOT NULL COMMENT '事件类型',
    `event_date` DATETIME NOT NULL COMMENT '事件日期',
    `description` TEXT COMMENT '描述(如疫苗名称)',
    `image_url` VARCHAR(200) COMMENT '相关图片 URL',
    `weight_value` DECIMAL(5,2) COMMENT '体重值(仅体重记录时使用)',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE,
    INDEX `idx_pet_id`(`pet_id`),
    INDEX `idx_event_date`(`event_date`),
    INDEX `idx_pet_date`(`pet_id`,`event_date` DESC) COMMENT '优化时光轴查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聚合多源数据生成成长时光轴';

-- 宠物品种字典表
CREATE TABLE IF NOT EXISTS `pet_breed`(
    `breed_id` INT PRIMARY KEY AUTO_INCREMENT,
    `species` ENUM('猫','狗','其他') NOT NULL COMMENT '宠物类型',
    `breed_name` VARCHAR(50) NOT NULL COMMENT '品种名称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_species`(`species`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物品种字典表';



















-- 插入常见犬种
INSERT INTO `pet_breed`(`species`,`breed_name`) VALUES
('狗','拉布拉多'),('狗','金毛寻回犬'),('狗','德国牧羊犬'),('狗','贵宾犬'),('狗','比熊犬'),
('狗','柯基'),('狗','柴犬'),('狗','哈士奇'),('狗','萨摩耶'),('狗','边境牧羊犬'),
('狗','法国斗牛犬'),('狗','英国斗牛犬'),('狗','博美'),('狗','吉娃娃'),('狗','西施犬'),
('狗','雪纳瑞'),('狗','杜宾'),('狗','罗威纳'),('狗','中华田园犬'),('狗','其他');

-- 插入常见猫种
INSERT INTO `pet_breed`(`species`,`breed_name`) VALUES
('猫','英国短毛猫'),('猫','美国短毛猫'),('猫','布偶猫'),('猫','暹罗猫'),('猫','波斯猫'),
('猫','苏格兰折耳猫'),('猫','俄罗斯蓝猫'),('猫','孟加拉豹猫'),('猫','挪威森林猫'),('猫','缅因猫'),
('猫','埃及猫'),('猫','巴曼猫'),('猫','异国短毛猫'),('猫','狸花猫'),('猫','三花猫'),
('猫','橘猫'),('猫','黑猫'),('猫','白猫'),('猫','其他');

-- 插入测试用户数据
INSERT INTO `user`(`openid`,`phone`,`password`,`nickname`,`avatar_url`) VALUES
('test_openid_001','13800138001','$2a$10$rT7QqL7Xq9Q8qW8qW8qW8uOe','宠物主人张','https://example.com/avatar1.jpg'),
('test_openid_002','13800138002','$2a$10$rT7QqL7Xq9Q8qW8qW8qW8uOe','爱宠达人李','https://example.com/avatar2.jpg'),
('test_openid_003','13800138003','$2a$10$rT7QqL7Xq9Q8qW8qW8qW8uOe','猫咪守护者','https://example.com/avatar3.jpg');

-- 插入测试宠物数据
INSERT INTO `pet`(`user_id`,`name`,`species`,`breed`,`birth_date`,`gender`,`weight`,`neutered_status`,`avatar_url`) VALUES
(1,'豆豆','狗','金毛寻回犬','2023-05-15','公',25.5, TRUE,'https://example.com/pet1.jpg'),
(1,'咪咪','猫','英国短毛猫','2024-01-20','母',4.2,FALSE,'https://example.com/pet2.jpg'),
(2,'旺财','狗','中华田园犬','2022-08-10','公',18.3,TRUE,'https://example.com/pet3.jpg'),
(3,'小白','猫','波斯猫','2023-11-05','母',3.8,TRUE,'https://example.com/pet4.jpg');

-- 插入疫苗记录示例
INSERT INTO `vaccination_record`(`pet_id`,`vaccine_name`,`vaccination_date`,`next_due_date`,`vet_info`) VALUES
(1,'狂犬疫苗','2024-12-01','2025-12-01','张医生-爱心宠物医院'),
(1,'六联疫苗','2024-11-15','2025-11-15','李医生-动物保健中心'),
(2,'猫三联','2024-10-20','2025-10-20','王医生-猫咪专科医院');

-- 插入驱虫记录示例
INSERT INTO `deworming_record`(`pet_id`,`deworming_type`,`drug_name`,`date`,`next_date`) VALUES
(1,'体内','拜宠清','2024-12-01','2025-01-01'),
(1,'体外','福来恩','2024-12-01','2025-01-01'),
(2,'体内','海乐妙','2024-11-20','2024-12-20');

-- 插入体检记录示例
INSERT INTO `checkup_record`(`pet_id`,`checkup_date`,`institution`,`result_summary`) VALUES
(1,'2024-11-10','爱心宠物医院','身体健康，各项指标正常，建议保持当前饮食和运动量'),
(2,'2024-10-15','猫咪专科医院','体重偏轻，建议增加营养摄入');

-- 插入病历记录示例
INSERT INTO `medical_record`(`pet_id`,`hospital`,`diagnosis`,`medication_list`,`treatment_date`) VALUES
(1,'爱心宠物医院','轻微皮肤病','消炎药膏，每日涂抹两次','2024-09-05'),
(2,'猫咪专科医院','消化不良','益生菌，每日一次','2024-10-08');

-- 插入提醒事项示例
INSERT INTO `reminder`(`user_id`,`pet_id`,`reminder_type`,`title`,`due_date`,`repeat_cycle`) VALUES
(1,1,'疫苗','豆豆的狂犬疫苗到期','2025-12-01 09:00:00','每年'),
(1,1,'驱虫','豆豆的体内驱虫','2025-01-01 09:00:00','每月'),
(1,2,'疫苗','咪咪的猫三联','2025-10-20 09:00:00','每年'),
(1,NULL,'自定义','购买宠物粮食','2024-12-25 10:00:00',NULL);

-- 插入服务点示例数据
INSERT INTO `service_point`(`name`,`type`,`address`,`phone`,`latitude`,`longitude`,`rating`,`business_hours`) VALUES
('爱心宠物医院','医院','北京市朝阳区建国路100号','010-12345678',39.9042,116.4074,4.8,'09:00-21:00'),
('猫咪专科医院','医院','北京市海淀区中关村大街1号','010-87654321',39.9834,116.3164,4.9,'08:30-20:30'),
('宠物乐园商店','宠物店','北京市西城区西单北大街130号','010-56781234',39.9139,116.3917,4.5,'10:00-22:00');

-- 插入成长事件示例
INSERT INTO `growth_event`(`pet_id`,`event_type`,`event_date`,`description`,`image_url`,`weight_value`) VALUES
(1,'体重记录','2024-12-01 10:00:00','定期体重测量',NULL,25.5),
(1,'疫苗','2024-12-01 09:00:00','狂犬疫苗接种','https://example.com/vaccine1.jpg', NULL),
(2,'照片','2024-11-15 15:30:00','日常玩耍照片','https://example.com/photo1.jpg',NULL);

-- 文件信息表(用于管理上传的文件)
CREATE TABLE IF NOT EXISTS `file_info`(
    `file_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `original_name` VARCHAR(200) NOT NULL COMMENT '原始文件名',
    `stored_name` VARCHAR(200) NOT NULL COMMENT '存储文件名(UUID)',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型(MIME类型)',
    `file_extension` VARCHAR(20) COMMENT '文件扩展名',
    `module_type` VARCHAR(50) NOT NULL COMMENT '模块类型(pet_avatar,medical_record,vaccination等)',
    `business_id` BIGINT COMMENT '关联业务ID(如pet_id,record_id等)',
    `uploader_id` INT NOT NULL COMMENT '上传用户ID',
    `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    FOREIGN KEY(`uploader_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_uploader_id`(`uploader_id`),
    INDEX `idx_module_business`(`module_type`,`business_id`),
    INDEX `idx_upload_time`(`upload_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表，管理所有上传文件的元信息';

-- 数据验证查询
-- 查看插入的数据
SELECT '用户数量:' as info, COUNT(*) as count FROM `user` 
UNION ALL SELECT '宠物数量:', COUNT(*) FROM `pet` 
UNION ALL SELECT '品种数量:', COUNT(*) FROM `pet_breed` 
UNION ALL SELECT '疫苗记录:', COUNT(*) FROM `vaccination_record` 
UNION ALL SELECT '提醒事项:', COUNT(*) FROM `reminder`;

-- 查看测试用户和宠物关联信息
SELECT u.nickname as 用户, p.name as 宠物, p.species as 类型, p.breed as 品种 
FROM `user` u LEFT JOIN `pet` p ON u.user_id = p.user_id 
WHERE u.deleted = 0 AND p.deleted = 0;

COMMIT;