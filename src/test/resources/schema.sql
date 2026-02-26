-- 测试数据库表结构定义
-- 使用H2数据库兼容的MySQL模式

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `openid` VARCHAR(64) UNIQUE,
    `phone` VARCHAR(20) UNIQUE,
    `password` VARCHAR(100),
    `nickname` VARCHAR(50) NOT NULL,
    `avatar_url` VARCHAR(200),
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 宠物表
CREATE TABLE IF NOT EXISTS `pet` (
    `pet_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `species` ENUM('猫', '狗', '其他') NOT NULL,
    `breed` VARCHAR(50),
    `birth_date` DATE,
    `gender` ENUM('公', '母'),
    `weight` DECIMAL(5,2),
    `allergy_history` TEXT,
    `neutered_status` BOOLEAN DEFAULT FALSE,
    `avatar_url` VARCHAR(200),
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
);

-- 提醒表
CREATE TABLE IF NOT EXISTS `reminder` (
    `reminder_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `pet_id` INT,
    `reminder_type` VARCHAR(50) NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `due_date` DATETIME NOT NULL,
    `status` ENUM('待完成', '已完成') DEFAULT '待完成',
    `repeat_cycle` VARCHAR(20),
    `notes` TEXT,
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `completed_time` DATETIME,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE SET NULL
);

-- 疫苗记录表
CREATE TABLE IF NOT EXISTS `vaccination_record` (
    `vaccination_id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `vaccine_name` VARCHAR(50) NOT NULL,
    `vaccination_date` DATE NOT NULL,
    `next_due_date` DATE NOT NULL,
    `vet_info` VARCHAR(100),
    `proof_image_url` VARCHAR(200),
    `notes` TEXT,
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE
);

-- 驱虫记录表
CREATE TABLE IF NOT EXISTS `deworming_record` (
    `deworming_id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `deworming_type` ENUM('体内', '体外') NOT NULL,
    `drug_name` VARCHAR(50) NOT NULL,
    `date` DATE NOT NULL,
    `next_date` DATE,
    `notes` TEXT,
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE
);

-- 体检记录表
CREATE TABLE IF NOT EXISTS `checkup_record` (
    `checkup_id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `checkup_date` DATE NOT NULL,
    `institution` VARCHAR(100) NOT NULL,
    `result_summary` TEXT,
    `report_image_url` VARCHAR(200),
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE
);

-- 病历记录表
CREATE TABLE IF NOT EXISTS `medical_record` (
    `medical_id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `hospital` VARCHAR(100) NOT NULL,
    `diagnosis` TEXT NOT NULL,
    `medication_list` TEXT,
    `treatment_date` DATE NOT NULL,
    `notes` TEXT,
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE
);

-- 成长事件表
CREATE TABLE IF NOT EXISTS `growth_event` (
    `event_id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `event_type` ENUM('照片', '体重记录', '疫苗', '驱虫', '体检', '病历') NOT NULL,
    `event_date` DATETIME NOT NULL,
    `description` TEXT,
    `image_url` VARCHAR(200),
    `weight_value` DECIMAL(5,2),
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE
);

-- 症状记录表
CREATE TABLE IF NOT EXISTS `symptom_record` (
    `symptom_id` INT AUTO_INCREMENT PRIMARY KEY,
    `pet_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `symptoms_text` TEXT NOT NULL,
    `analysis_result` TEXT,
    `emergency_level` ENUM('建议观察', '尽快就医', '紧急'),
    `suggestions` TEXT,
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`pet_id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
);

-- 服务点表
CREATE TABLE IF NOT EXISTS `service_point` (
    `point_id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `type` ENUM('医院', '宠物店', '其他') NOT NULL,
    `address` VARCHAR(200) NOT NULL,
    `phone` VARCHAR(20),
    `latitude` DECIMAL(10,6) NOT NULL,
    `longitude` DECIMAL(10,6) NOT NULL,
    `rating` DECIMAL(3,2) DEFAULT 0.00,
    `business_hours` VARCHAR(100),
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_name_address` (`name`, `address`)
);

-- 宠物品种表
CREATE TABLE IF NOT EXISTS `pet_breed` (
    `breed_id` INT AUTO_INCREMENT PRIMARY KEY,
    `species` ENUM('猫', '狗', '其他') NOT NULL,
    `breed_name` VARCHAR(50) NOT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建必要的索引
CREATE INDEX IF NOT EXISTS `idx_user_phone` ON `user`(`phone`);
CREATE INDEX IF NOT EXISTS `idx_user_openid` ON `user`(`openid`);
CREATE INDEX IF NOT EXISTS `idx_pet_user_id` ON `pet`(`user_id`);
CREATE INDEX IF NOT EXISTS `idx_reminder_user_id` ON `reminder`(`user_id`);
CREATE INDEX IF NOT EXISTS `idx_reminder_pet_id` ON `reminder`(`pet_id`);
CREATE INDEX IF NOT EXISTS `idx_vaccination_pet_id` ON `vaccination_record`(`pet_id`);
CREATE INDEX IF NOT EXISTS `idx_deworming_pet_id` ON `deworming_record`(`pet_id`);