-- ============================================================
-- 在线秒杀系统 - 数据库初始化脚本
-- 数据库: seckill_db
-- 字符集: utf8mb4 / utf8mb4_unicode_ci
-- 密码说明: 示例用户密码统一为 BCrypt 加密的 "123456"
--   $2a$10$7JB720yubVSZvUI0rEqK/.VqGOVTHYKIiBZ0XQ4KQp0eB6QvYcDcW
-- ============================================================

DROP DATABASE IF EXISTS seckill_db;
CREATE DATABASE seckill_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE seckill_db;

-- ------------------------------------------------------------
-- 1. 系统用户表
-- ------------------------------------------------------------
CREATE TABLE sys_user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(64) NOT NULL COMMENT '登录账号',
  password VARCHAR(255) NOT NULL COMMENT 'BCrypt 密码',
  real_name VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
  role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色码 USER/ADMIN',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ------------------------------------------------------------
-- 2. 秒杀商品表
-- ------------------------------------------------------------
CREATE TABLE product (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  name VARCHAR(128) NOT NULL COMMENT '商品名称',
  description TEXT COMMENT '商品描述',
  image_url VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
  original_price DECIMAL(10,2) NOT NULL COMMENT '原价',
  seckill_price DECIMAL(10,2) NOT NULL COMMENT '秒杀价',
  stock INT NOT NULL DEFAULT 0 COMMENT '剩余库存',
  activity_id BIGINT UNSIGNED DEFAULT NULL COMMENT '关联秒杀活动ID',
  status VARCHAR(10) NOT NULL DEFAULT 'ON' COMMENT 'ON 上架 / OFF 下架',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_activity (activity_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀商品表';

-- ------------------------------------------------------------
-- 3. 秒杀活动表
-- ------------------------------------------------------------
CREATE TABLE seckill_activity (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  title VARCHAR(128) NOT NULL COMMENT '活动标题',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '关联商品ID',
  seckill_price DECIMAL(10,2) NOT NULL COMMENT '活动秒杀价',
  activity_stock INT NOT NULL DEFAULT 0 COMMENT '活动库存',
  start_at DATETIME NOT NULL COMMENT '开始时间',
  end_at DATETIME NOT NULL COMMENT '结束时间',
  status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED' COMMENT 'NOT_STARTED/ONGOING/ENDED',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_product (product_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀活动表';

-- ------------------------------------------------------------
-- 4. 订单表
-- ------------------------------------------------------------
CREATE TABLE orders (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  order_no VARCHAR(32) NOT NULL COMMENT '订单编号（业务唯一）',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '下单用户ID',
  activity_id BIGINT UNSIGNED NOT NULL COMMENT '秒杀活动ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  product_name VARCHAR(128) NOT NULL COMMENT '下单时商品名称快照',
  quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT'
    COMMENT 'PENDING_PAYMENT 待支付 / PAID 已支付 / COMPLETED 已完成 / CANCELLED 已取消',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_user (user_id),
  KEY idx_activity (activity_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀订单表';

-- ------------------------------------------------------------
-- 5. 订单明细表
-- ------------------------------------------------------------
CREATE TABLE order_item (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  product_name VARCHAR(128) NOT NULL COMMENT '商品名称快照',
  price DECIMAL(10,2) NOT NULL COMMENT '成交单价',
  quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ------------------------------------------------------------
-- 6. 库存变动流水表
-- ------------------------------------------------------------
CREATE TABLE stock_log (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  activity_id BIGINT UNSIGNED DEFAULT NULL COMMENT '活动ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  change_type VARCHAR(10) NOT NULL COMMENT 'DEDUCT 扣减 / RETURN 回补',
  change_count INT NOT NULL COMMENT '变动数量',
  remain_stock INT NOT NULL COMMENT '变动后剩余库存',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_product (product_id),
  KEY idx_activity (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存变动流水表';

-- ------------------------------------------------------------
-- 种子数据：管理员 / 演示用户（密码均为 123456）
-- ------------------------------------------------------------
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOVTHYKIiBZ0XQ4KQp0eB6QvYcDcW', '平台管理员', 'ADMIN', 1),
('buyer',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOVTHYKIiBZ0XQ4KQp0eB6QvYcDcW', '演示买家',   'USER',  1);

-- 商品
INSERT INTO product (name, description, original_price, seckill_price, stock, status) VALUES
('限时秒杀·国产橙 5 斤装', '当季鲜橙，皮薄汁多，产地直发，秒杀价限量供应。', 39.90, 19.90, 100, 'ON'),
('限时秒杀·精选苹果 4 斤装', '脆甜红富士，果径 80mm 以上，礼盒装。',         49.90, 25.90,  80, 'ON');

-- 进行中活动：当前时间前后各 2 小时
INSERT INTO seckill_activity (title, product_id, seckill_price, activity_stock, start_at, end_at, status) VALUES
('鲜橙限时秒杀专场', 1, 19.90, 100, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 2 HOUR), 'ONGOING'),
('苹果周末秒杀专场', 2, 25.90,  80, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 HOUR), 'ENDED');

-- 回写商品活动关联
UPDATE product p SET p.activity_id = (SELECT a.id FROM seckill_activity a WHERE a.product_id = p.id LIMIT 1);
