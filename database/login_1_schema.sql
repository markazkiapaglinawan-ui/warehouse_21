CREATE DATABASE IF NOT EXISTS `login_1`;
USE `login_1`;

CREATE TABLE IF NOT EXISTS `users` (
    `idusers` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'STAFF',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`idusers`),
    UNIQUE KEY `uq_users_username` (`username`)
);

SET @role_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'users' AND column_name = 'role'
);
SET @users_role_sql = IF(@role_exists = 0,
    'ALTER TABLE `users` ADD COLUMN `role` VARCHAR(20) NOT NULL DEFAULT ''STAFF''',
    'SELECT 1'
);
PREPARE users_role_stmt FROM @users_role_sql;
EXECUTE users_role_stmt;
DEALLOCATE PREPARE users_role_stmt;

SET @created_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'users' AND column_name = 'created_at'
);
SET @users_created_sql = IF(@created_at_exists = 0,
    'ALTER TABLE `users` ADD COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE users_created_stmt FROM @users_created_sql;
EXECUTE users_created_stmt;
DEALLOCATE PREPARE users_created_stmt;

SET @username_index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'login_1' AND table_name = 'users' AND index_name = 'uq_users_username'
);
SET @users_index_sql = IF(@username_index_exists = 0,
    'CREATE UNIQUE INDEX `uq_users_username` ON `users` (`username`)',
    'SELECT 1'
);
PREPARE users_index_stmt FROM @users_index_sql;
EXECUTE users_index_stmt;
DEALLOCATE PREPARE users_index_stmt;

CREATE TABLE IF NOT EXISTS `items` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `item_code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `category` VARCHAR(100) NULL,
    `quantity` INT NOT NULL DEFAULT 0,
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `image_path` VARCHAR(255) NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_items_item_code` (`item_code`)
);

SET @items_image_path_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'items' AND column_name = 'image_path'
);
SET @items_image_path_sql = IF(@items_image_path_exists = 0,
    'ALTER TABLE `items` ADD COLUMN `image_path` VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE items_image_path_stmt FROM @items_image_path_sql;
EXECUTE items_image_path_stmt;
DEALLOCATE PREPARE items_image_path_stmt;

CREATE TABLE IF NOT EXISTS `stock_requests` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `request_code` VARCHAR(50) NOT NULL,
    `requested_by` VARCHAR(45) NOT NULL,
    `requested_role` VARCHAR(20) NOT NULL,
    `item_id` INT NOT NULL,
    `quantity` INT NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `requested_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `approved_by` VARCHAR(45) NULL,
    `approved_at` TIMESTAMP NULL,
    `posted_by` VARCHAR(45) NULL,
    `posted_at` TIMESTAMP NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_stock_requests_request_code` (`request_code`),
    CONSTRAINT `fk_stock_requests_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`)
);

SET @stock_requested_by_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'requested_by'
);
SET @stock_requested_by_sql = IF(@stock_requested_by_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `requested_by` VARCHAR(45) NOT NULL DEFAULT ''''',
    'SELECT 1'
);
PREPARE stock_requested_by_stmt FROM @stock_requested_by_sql;
EXECUTE stock_requested_by_stmt;
DEALLOCATE PREPARE stock_requested_by_stmt;

SET @stock_requested_role_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'requested_role'
);
SET @stock_requested_role_sql = IF(@stock_requested_role_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `requested_role` VARCHAR(20) NOT NULL DEFAULT ''STAFF''',
    'SELECT 1'
);
PREPARE stock_requested_role_stmt FROM @stock_requested_role_sql;
EXECUTE stock_requested_role_stmt;
DEALLOCATE PREPARE stock_requested_role_stmt;

SET @stock_status_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'status'
);
SET @stock_status_sql = IF(@stock_status_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `status` VARCHAR(20) NOT NULL DEFAULT ''PENDING''',
    'SELECT 1'
);
PREPARE stock_status_stmt FROM @stock_status_sql;
EXECUTE stock_status_stmt;
DEALLOCATE PREPARE stock_status_stmt;

SET @stock_requested_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'requested_at'
);
SET @stock_requested_at_sql = IF(@stock_requested_at_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `requested_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE stock_requested_at_stmt FROM @stock_requested_at_sql;
EXECUTE stock_requested_at_stmt;
DEALLOCATE PREPARE stock_requested_at_stmt;

SET @stock_approved_by_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'approved_by'
);
SET @stock_approved_by_sql = IF(@stock_approved_by_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `approved_by` VARCHAR(45) NULL',
    'SELECT 1'
);
PREPARE stock_approved_by_stmt FROM @stock_approved_by_sql;
EXECUTE stock_approved_by_stmt;
DEALLOCATE PREPARE stock_approved_by_stmt;

SET @stock_approved_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'approved_at'
);
SET @stock_approved_at_sql = IF(@stock_approved_at_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `approved_at` TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE stock_approved_at_stmt FROM @stock_approved_at_sql;
EXECUTE stock_approved_at_stmt;
DEALLOCATE PREPARE stock_approved_at_stmt;

SET @stock_posted_by_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'posted_by'
);
SET @stock_posted_by_sql = IF(@stock_posted_by_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `posted_by` VARCHAR(45) NULL',
    'SELECT 1'
);
PREPARE stock_posted_by_stmt FROM @stock_posted_by_sql;
EXECUTE stock_posted_by_stmt;
DEALLOCATE PREPARE stock_posted_by_stmt;

SET @stock_posted_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND column_name = 'posted_at'
);
SET @stock_posted_at_sql = IF(@stock_posted_at_exists = 0,
    'ALTER TABLE `stock_requests` ADD COLUMN `posted_at` TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE stock_posted_at_stmt FROM @stock_posted_at_sql;
EXECUTE stock_posted_at_stmt;
DEALLOCATE PREPARE stock_posted_at_stmt;

SET @stock_request_index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'login_1' AND table_name = 'stock_requests' AND index_name = 'uq_stock_requests_request_code'
);
SET @stock_request_index_sql = IF(@stock_request_index_exists = 0,
    'CREATE UNIQUE INDEX `uq_stock_requests_request_code` ON `stock_requests` (`request_code`)',
    'SELECT 1'
);
PREPARE stock_request_index_stmt FROM @stock_request_index_sql;
EXECUTE stock_request_index_stmt;
DEALLOCATE PREPARE stock_request_index_stmt;

CREATE TABLE IF NOT EXISTS `orders` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `order_code` VARCHAR(50) NOT NULL,
    `customer_name` VARCHAR(100) NOT NULL,
    `customer_username` VARCHAR(45) NULL,
    `item_id` INT NOT NULL,
    `quantity` INT NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `order_date` DATE NOT NULL,
    `paid` TINYINT(1) NOT NULL DEFAULT 0,
    `payment_method` VARCHAR(20) NULL,
    `courier_name` VARCHAR(40) NULL,
    `forwarded_at` TIMESTAMP NULL,
    `cancellation_reason` TEXT NULL,
    `cancelled_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_orders_order_code` (`order_code`),
    CONSTRAINT `fk_orders_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`)
);

SET @orders_customer_username_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'orders' AND column_name = 'customer_username'
);
SET @orders_customer_username_sql = IF(@orders_customer_username_exists = 0,
    'ALTER TABLE `orders` ADD COLUMN `customer_username` VARCHAR(45) NULL',
    'SELECT 1'
);
PREPARE orders_customer_username_stmt FROM @orders_customer_username_sql;
EXECUTE orders_customer_username_stmt;
DEALLOCATE PREPARE orders_customer_username_stmt;

SET @orders_paid_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'orders' AND column_name = 'paid'
);
SET @orders_paid_sql = IF(@orders_paid_exists = 0,
    'ALTER TABLE `orders` ADD COLUMN `paid` TINYINT(1) NOT NULL DEFAULT 0',
    'SELECT 1'
);
PREPARE orders_paid_stmt FROM @orders_paid_sql;
EXECUTE orders_paid_stmt;
DEALLOCATE PREPARE orders_paid_stmt;

SET @orders_payment_method_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'orders' AND column_name = 'payment_method'
);
SET @orders_payment_method_sql = IF(@orders_payment_method_exists = 0,
    'ALTER TABLE `orders` ADD COLUMN `payment_method` VARCHAR(20) NULL',
    'SELECT 1'
);
PREPARE orders_payment_method_stmt FROM @orders_payment_method_sql;
EXECUTE orders_payment_method_stmt;
DEALLOCATE PREPARE orders_payment_method_stmt;

SET @orders_courier_name_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'orders' AND column_name = 'courier_name'
);
SET @orders_courier_name_sql = IF(@orders_courier_name_exists = 0,
    'ALTER TABLE `orders` ADD COLUMN `courier_name` VARCHAR(40) NULL',
    'SELECT 1'
);
PREPARE orders_courier_name_stmt FROM @orders_courier_name_sql;
EXECUTE orders_courier_name_stmt;
DEALLOCATE PREPARE orders_courier_name_stmt;

SET @orders_forwarded_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'orders' AND column_name = 'forwarded_at'
);
SET @orders_forwarded_at_sql = IF(@orders_forwarded_at_exists = 0,
    'ALTER TABLE `orders` ADD COLUMN `forwarded_at` TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE orders_forwarded_at_stmt FROM @orders_forwarded_at_sql;
EXECUTE orders_forwarded_at_stmt;
DEALLOCATE PREPARE orders_forwarded_at_stmt;

SET @orders_cancellation_reason_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'orders' AND column_name = 'cancellation_reason'
);
SET @orders_cancellation_reason_sql = IF(@orders_cancellation_reason_exists = 0,
    'ALTER TABLE `orders` ADD COLUMN `cancellation_reason` TEXT NULL',
    'SELECT 1'
);
PREPARE orders_cancellation_reason_stmt FROM @orders_cancellation_reason_sql;
EXECUTE orders_cancellation_reason_stmt;
DEALLOCATE PREPARE orders_cancellation_reason_stmt;

SET @orders_cancelled_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'orders' AND column_name = 'cancelled_at'
);
SET @orders_cancelled_at_sql = IF(@orders_cancelled_at_exists = 0,
    'ALTER TABLE `orders` ADD COLUMN `cancelled_at` TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE orders_cancelled_at_stmt FROM @orders_cancelled_at_sql;
EXECUTE orders_cancelled_at_stmt;
DEALLOCATE PREPARE orders_cancelled_at_stmt;

CREATE TABLE IF NOT EXISTS `customer_service_messages` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `customer_username` VARCHAR(45) NOT NULL,
    `customer_name` VARCHAR(100) NOT NULL,
    `subject` VARCHAR(80) NOT NULL,
    `order_code` VARCHAR(50) NULL,
    `message` TEXT NOT NULL,
    `reply` TEXT NULL,
    `replied_by` VARCHAR(100) NULL,
    `replied_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

SET @customer_service_reply_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'customer_service_messages' AND column_name = 'reply'
);
SET @customer_service_reply_sql = IF(@customer_service_reply_exists = 0,
    'ALTER TABLE `customer_service_messages` ADD COLUMN `reply` TEXT NULL',
    'SELECT 1'
);
PREPARE customer_service_reply_stmt FROM @customer_service_reply_sql;
EXECUTE customer_service_reply_stmt;
DEALLOCATE PREPARE customer_service_reply_stmt;

SET @customer_service_replied_by_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'customer_service_messages' AND column_name = 'replied_by'
);
SET @customer_service_replied_by_sql = IF(@customer_service_replied_by_exists = 0,
    'ALTER TABLE `customer_service_messages` ADD COLUMN `replied_by` VARCHAR(100) NULL',
    'SELECT 1'
);
PREPARE customer_service_replied_by_stmt FROM @customer_service_replied_by_sql;
EXECUTE customer_service_replied_by_stmt;
DEALLOCATE PREPARE customer_service_replied_by_stmt;

SET @customer_service_replied_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'login_1' AND table_name = 'customer_service_messages' AND column_name = 'replied_at'
);
SET @customer_service_replied_at_sql = IF(@customer_service_replied_at_exists = 0,
    'ALTER TABLE `customer_service_messages` ADD COLUMN `replied_at` TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE customer_service_replied_at_stmt FROM @customer_service_replied_at_sql;
EXECUTE customer_service_replied_at_stmt;
DEALLOCATE PREPARE customer_service_replied_at_stmt;

INSERT INTO `users` (`username`, `password`, `role`)
SELECT 'admin', '1234', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM `users` WHERE `username` = 'admin'
);

INSERT INTO `users` (`username`, `password`, `role`)
SELECT 'eboy', '1234', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM `users` WHERE `username` = 'eboy'
);

INSERT INTO `users` (`username`, `password`, `role`)
SELECT 'nathan', '1234', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM `users` WHERE `username` = 'nathan'
);

INSERT INTO `users` (`username`, `password`, `role`)
SELECT 'receiver', '1234', 'RECEIVER'
WHERE NOT EXISTS (
    SELECT 1 FROM `users` WHERE `username` = 'receiver'
);

INSERT INTO `users` (`username`, `password`, `role`)
SELECT 'starzy', '1234', 'CUSTOMER'
WHERE NOT EXISTS (
    SELECT 1 FROM `users` WHERE `username` = 'starzy'
);

UPDATE `users`
SET `role` = 'CUSTOMER'
WHERE `username` = 'starzy';

DELETE FROM `users`
WHERE `username` = 'starzy1';
