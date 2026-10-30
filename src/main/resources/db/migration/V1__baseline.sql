-- V1: schema inicial do SpringEcom

CREATE TABLE `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `active` bit(1) NOT NULL,
    `email` varchar(255) DEFAULT NULL,
    `external_id` binary(16) NOT NULL,
    `password` varchar(255) DEFAULT NULL,
    `role` enum('ADMIN','SELLER','USER') DEFAULT 'USER',
    `username` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_external_id` (`external_id`),
    UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `product` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `active` bit(1) NOT NULL,
    `brand` varchar(255) DEFAULT NULL,
    `category` varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `price` decimal(38,2) DEFAULT NULL,
    `product_available` bit(1) NOT NULL,
    `release_date` date DEFAULT NULL,
    `stock_quantity` int NOT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_product_user_id` (`user_id`),
    CONSTRAINT `fk_product_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tb_order` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `custom_name` varchar(255) DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `order_date` date DEFAULT NULL,
    `order_id` varchar(255) DEFAULT NULL,
    `status` varchar(255) DEFAULT NULL,
    `user_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tb_order_order_id` (`order_id`),
    KEY `idx_tb_order_user_id` (`user_id`),
    CONSTRAINT `fk_tb_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `cart` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cart_user_id` (`user_id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `cart_item` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `quantity` int NOT NULL,
    `cart_id` bigint NOT NULL,
    `product_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_cart_item_cart_id` (`cart_id`),
    KEY `idx_cart_item_product_id` (`product_id`),
    CONSTRAINT `fk_cart_item_cart` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
    CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order_item` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `quantity` int NOT NULL,
    `total_price` decimal(38,2) DEFAULT NULL,
    `order_id` bigint DEFAULT NULL,
    `product_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_order_item_order_id` (`order_id`),
    KEY `idx_order_item_product_id` (`product_id`),
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `tb_order` (`id`),
    CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `product_image` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `image_data` longblob,
    `image_name` varchar(255) DEFAULT NULL,
    `image_type` varchar(255) DEFAULT NULL,
    `product_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_product_image_product_id` (`product_id`),
    CONSTRAINT `fk_product_image_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;