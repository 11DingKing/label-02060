-- WebRTC Database Schema

CREATE DATABASE IF NOT EXISTS webrtc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE webrtc_db;

-- 房间表
CREATE TABLE IF NOT EXISTS `room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `room_id` VARCHAR(64) NOT NULL COMMENT '房间ID',
    `room_name` VARCHAR(128) NOT NULL COMMENT '房间名称',
    `max_users` INT NOT NULL DEFAULT 10 COMMENT '最大用户数',
    `current_users` INT NOT NULL DEFAULT 0 COMMENT '当前用户数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-关闭, 1-开启',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_id` (`room_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 房间用户表
CREATE TABLE IF NOT EXISTS `room_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `room_id` VARCHAR(64) NOT NULL COMMENT '房间ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(64) NOT NULL COMMENT '用户昵称',
    `video_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '视频开关: 0-关, 1-开',
    `audio_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '音频开关: 0-关, 1-开',
    `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_user` (`room_id`, `user_id`),
    KEY `idx_room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间用户表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    `user_name` VARCHAR(64) DEFAULT NULL COMMENT '用户名',
    `action` VARCHAR(64) NOT NULL COMMENT '操作类型',
    `target` VARCHAR(128) DEFAULT NULL COMMENT '操作目标',
    `detail` TEXT DEFAULT NULL COMMENT '操作详情',
    `ip` VARCHAR(64) DEFAULT NULL COMMENT 'IP地址',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_action` (`action`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
