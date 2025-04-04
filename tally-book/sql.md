/*
 Navicat Premium Data Transfer

 Source Server         : tally-book
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : dbconn.sealosbja.site:37594
 Source Schema         : expense_tracker

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 01/04/2025 16:52:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for budget
-- ----------------------------
DROP TABLE IF EXISTS `budget`;
CREATE TABLE `budget`  (
  `budget_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `year` int NOT NULL,
  `month` int NOT NULL,
  `budget_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`budget_id`) USING BTREE,
  UNIQUE INDEX `idx_user_year_month`(`user_id` ASC, `year` ASC, `month` ASC) USING BTREE,
  CONSTRAINT `fk_budget_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of budget
-- ----------------------------
INSERT INTO `budget` VALUES (1, 1, 2025, 4, 5000.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `sort_order` int NOT NULL DEFAULT 0,
  `is_system` tinyint NOT NULL DEFAULT 1,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `idx_category_code`(`category_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 'food', '餐饮', '🍔', 10, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category` VALUES (2, 'transport', '交通', '🚌', 20, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category` VALUES (3, 'shopping', '购物', '🛍️', 30, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category` VALUES (4, 'entertainment', '娱乐', '🎮', 40, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category` VALUES (5, 'housing', '住房', '🏠', 50, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category` VALUES (6, 'medical', '医疗', '💊', 60, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category` VALUES (7, 'education', '教育', '📚', 70, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category` VALUES (8, 'other', '其他', '📦', 999, 1, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');

-- ----------------------------
-- Table structure for category_budget
-- ----------------------------
DROP TABLE IF EXISTS `category_budget`;
CREATE TABLE `category_budget`  (
  `category_budget_id` int NOT NULL AUTO_INCREMENT,
  `budget_id` int NOT NULL,
  `category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `budget_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_budget_id`) USING BTREE,
  UNIQUE INDEX `idx_budget_category`(`budget_id` ASC, `category` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  CONSTRAINT `fk_category_budget_budget` FOREIGN KEY (`budget_id`) REFERENCES `budget` (`budget_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category_budget
-- ----------------------------
INSERT INTO `category_budget` VALUES (1, 1, 'food', 1500.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category_budget` VALUES (2, 1, 'transport', 500.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category_budget` VALUES (3, 1, 'shopping', 1000.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category_budget` VALUES (4, 1, 'entertainment', 500.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category_budget` VALUES (5, 1, 'housing', 1000.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category_budget` VALUES (6, 1, 'medical', 300.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category_budget` VALUES (7, 1, 'education', 200.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');
INSERT INTO `category_budget` VALUES (8, 1, 'other', 0.00, '2025-04-01 03:39:38', '2025-04-01 03:39:38');

-- ----------------------------
-- Table structure for record
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record`  (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `record_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `year` int NOT NULL,
  `month` int NOT NULL,
  `day` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_record_time`(`record_time` ASC) USING BTREE,
  INDEX `idx_year_month`(`year` ASC, `month` ASC) USING BTREE,
  CONSTRAINT `fk_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of record
-- ----------------------------
INSERT INTO `record` VALUES (1, 1, 35.50, 'food', '午餐', '2025-04-01 12:30:00', 2025, 4, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38', 0);
INSERT INTO `record` VALUES (2, 1, 15.00, 'transport', '公交车', '2025-04-01 08:45:00', 2025, 4, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38', 0);
INSERT INTO `record` VALUES (3, 1, 120.80, 'shopping', '超市购物', '2025-03-31 19:20:00', 2025, 3, 31, '2025-04-01 03:39:38', '2025-04-01 03:39:38', 0);
INSERT INTO `record` VALUES (4, 1, 49.90, 'entertainment', '电影票', '2025-03-31 20:00:00', 2025, 3, 31, '2025-04-01 03:39:38', '2025-04-01 03:39:38', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$x6xANz9GSuDW1bQx0QIyFu8QR.Lp1B4gtR5RDvE/0NLKYZGNaQ4R.', '管理员', '13800000000', NULL, 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38', NULL);

-- ----------------------------
-- Table structure for user_setting
-- ----------------------------
DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting`  (
  `setting_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `theme` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'light',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CNY',
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'zh-CN',
  `notification` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`setting_id`) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_setting_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_setting
-- ----------------------------
INSERT INTO `user_setting` VALUES (1, 1, 'dark', 'CNY', 'zh-CN', 1, '2025-04-01 03:39:38', '2025-04-01 03:39:38');

-- ----------------------------
-- View structure for category_expense_summary
-- ----------------------------
DROP VIEW IF EXISTS `category_expense_summary`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `category_expense_summary` AS select `u`.`user_id` AS `user_id`,`r`.`year` AS `year`,`r`.`month` AS `month`,`r`.`category` AS `category`,`c`.`category_name` AS `category_name`,sum(`r`.`amount`) AS `category_expense`,count(`r`.`record_id`) AS `record_count` from ((`user` `u` join `record` `r` on(((`u`.`user_id` = `r`.`user_id`) and (`r`.`is_deleted` = 0)))) join `category` `c` on((`r`.`category` = `c`.`category_code`))) group by `u`.`user_id`,`r`.`year`,`r`.`month`,`r`.`category`,`c`.`category_name`;

-- ----------------------------
-- View structure for monthly_expense_summary
-- ----------------------------
DROP VIEW IF EXISTS `monthly_expense_summary`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `monthly_expense_summary` AS select `u`.`user_id` AS `user_id`,`u`.`username` AS `username`,`r`.`year` AS `year`,`r`.`month` AS `month`,sum(`r`.`amount`) AS `total_expense`,count(`r`.`record_id`) AS `record_count`,min(`r`.`record_time`) AS `first_record_time`,max(`r`.`record_time`) AS `last_record_time` from (`user` `u` left join `record` `r` on(((`u`.`user_id` = `r`.`user_id`) and (`r`.`is_deleted` = 0)))) group by `u`.`user_id`,`u`.`username`,`r`.`year`,`r`.`month`;

-- ----------------------------
-- Procedure structure for CreateUser
-- ----------------------------
DROP PROCEDURE IF EXISTS `CreateUser`;
delimiter ;;
CREATE PROCEDURE `CreateUser`(IN p_username VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_nickname VARCHAR(50),
    IN p_phone VARCHAR(20))
BEGIN
    DECLARE hashed_password VARCHAR(255);
    -- 这里使用SHA-256进行简单示例，实际应用中应该使用更安全的加密方式如BCrypt
    SET hashed_password = SHA2(p_password, 256);
    
    INSERT INTO `user` (`username`, `password`, `nickname`, `phone`) 
    VALUES (p_username, hashed_password, p_nickname, p_phone);
    
    -- 为新用户创建默认设置
    INSERT INTO `user_setting` (`user_id`, `theme`, `currency`, `language`) 
    VALUES (LAST_INSERT_ID(), 'light', 'CNY', 'zh-CN');
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table record
-- ----------------------------
DROP TRIGGER IF EXISTS `before_record_insert`;
delimiter ;;
CREATE TRIGGER `before_record_insert` BEFORE INSERT ON `record` FOR EACH ROW BEGIN
    IF NEW.year IS NULL THEN
        SET NEW.year = YEAR(NEW.record_time);
    END IF;
    
    IF NEW.month IS NULL THEN
        SET NEW.month = MONTH(NEW.record_time);
    END IF;
    
    IF NEW.day IS NULL THEN
        SET NEW.day = DAY(NEW.record_time);
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table record
-- ----------------------------
DROP TRIGGER IF EXISTS `before_record_update`;
delimiter ;;
CREATE TRIGGER `before_record_update` BEFORE UPDATE ON `record` FOR EACH ROW BEGIN
    IF NEW.record_time != OLD.record_time THEN
        SET NEW.year = YEAR(NEW.record_time);
        SET NEW.month = MONTH(NEW.record_time);
        SET NEW.day = DAY(NEW.record_time);
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
