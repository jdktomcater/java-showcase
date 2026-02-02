/*
 * Nacos MySQL Init SQL
 * Version: Nacos 2.x
 * Database: nacos_config
 */

CREATE DATABASE IF NOT EXISTS nacos_config DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE nacos_config;

/******************************************
 * 用户 & 权限（可选，但强烈推荐）
 ******************************************/
CREATE USER IF NOT EXISTS 'nacos'@'%' IDENTIFIED WITH mysql_native_password BY 'Nacos@123';

GRANT ALL PRIVILEGES ON nacos_config.* TO 'nacos'@'%';
FLUSH PRIVILEGES;

/******************************************
 * Nacos Core Tables
 ******************************************/

CREATE TABLE `config_info` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `data_id` varchar(255) NOT NULL,
   `group_id` varchar(255) DEFAULT NULL,
   `content` longtext NOT NULL,
   `md5` varchar(32) DEFAULT NULL,
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `src_user` text,
   `src_ip` varchar(50) DEFAULT NULL,
   `app_name` varchar(128) DEFAULT NULL,
   `tenant_id` varchar(128) DEFAULT '',
   `c_desc` varchar(256) DEFAULT NULL,
   `c_use` varchar(64) DEFAULT NULL,
   `effect` varchar(64) DEFAULT NULL,
   `type` varchar(64) DEFAULT NULL,
   `c_schema` text,
   `encrypted_data_key` text NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `config_info_aggr` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `data_id` varchar(255) NOT NULL,
    `group_id` varchar(255) NOT NULL,
    `datum_id` varchar(255) NOT NULL,
    `content` longtext NOT NULL,
    `gmt_modified` datetime NOT NULL,
    `app_name` varchar(128) DEFAULT NULL,
    `tenant_id` varchar(128) DEFAULT '',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `config_info_beta` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `data_id` varchar(255) NOT NULL,
    `group_id` varchar(128) NOT NULL,
    `app_name` varchar(128) DEFAULT NULL,
    `content` longtext NOT NULL,
    `beta_ips` varchar(1024) DEFAULT NULL,
    `md5` varchar(32) DEFAULT NULL,
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `src_user` text,
    `src_ip` varchar(50) DEFAULT NULL,
    `tenant_id` varchar(128) DEFAULT '',
    `encrypted_data_key` text NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `config_info_tag` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `data_id` varchar(255) NOT NULL,
   `group_id` varchar(128) NOT NULL,
   `tenant_id` varchar(128) DEFAULT '',
   `tag_id` varchar(128) NOT NULL,
   `app_name` varchar(128) DEFAULT NULL,
   `content` longtext NOT NULL,
   `md5` varchar(32) DEFAULT NULL,
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `src_user` text,
   `src_ip` varchar(50) DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tenant_info` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `kp` varchar(128) NOT NULL,
   `tenant_id` varchar(128) DEFAULT '',
   `tenant_name` varchar(128) DEFAULT '',
   `tenant_desc` varchar(256) DEFAULT NULL,
   `create_source` varchar(32) DEFAULT NULL,
   `gmt_create` bigint NOT NULL,
   `gmt_modified` bigint NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/******************************************
 * 服务发现相关
 ******************************************/

CREATE TABLE `instances` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `service_name` varchar(255) NOT NULL,
    `ip` varchar(50) NOT NULL,
    `port` int NOT NULL,
    `cluster_name` varchar(255) DEFAULT NULL,
    `weight` double DEFAULT NULL,
    `enabled` tinyint DEFAULT NULL,
    `healthy` tinyint DEFAULT NULL,
    `metadata` varchar(2048) DEFAULT NULL,
    `gmt_modified` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_service_ip_port` (`service_name`,`ip`,`port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE DATABASE IF NOT EXISTS order_db DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS pay DEFAULT CHARSET utf8mb4;
CREATE USER IF NOT EXISTS 'pay'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON order_db.* TO 'pay'@'%';
GRANT ALL PRIVILEGES ON pay.* TO 'pay'@'%';

