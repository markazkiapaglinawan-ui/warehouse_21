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
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_items_item_code` (`item_code`)
);

CREATE TABLE IF NOT EXISTS `orders` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `order_code` VARCHAR(50) NOT NULL,
    `customer_name` VARCHAR(100) NOT NULL,
    `item_id` INT NOT NULL,
    `quantity` INT NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `order_date` DATE NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_orders_order_code` (`order_code`),
    CONSTRAINT `fk_orders_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`)
);

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
SELECT 'starzy', '1234', 'RECEIVER'
WHERE NOT EXISTS (
    SELECT 1 FROM `users` WHERE `username` = 'starzy'
);
