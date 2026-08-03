DROP SCHEMA IF EXISTS `obidos` ;
CREATE SCHEMA IF NOT EXISTS `obidos` DEFAULT CHARACTER SET utf8 ;
USE `obidos` ;
/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.4.5-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: obidos
-- ------------------------------------------------------
-- Server version	11.4.5-MariaDB-ubu2404

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `auditlog`
--

DROP TABLE IF EXISTS `auditlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` int(10) unsigned NOT NULL COMMENT 'All actions performed in Obidos are audited.',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT 'The id of the user performing the action',
  `object_id` bigint(20) unsigned DEFAULT NULL COMMENT 'the id of the object being acted upon, typically another user id',
  `recipient_id` bigint(20) unsigned DEFAULT NULL,
  `new_item_id` bigint(20) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `username` varchar(64) DEFAULT NULL COMMENT 'The username of the user performing the action',
  `object_name` varchar(64) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL COMMENT 'When the column_id does not refer to a different table, this holds the new value\n',
  `fullname` varchar(64) DEFAULT NULL COMMENT 'Full name of user performing the action.\n',
  `recipient_username` varchar(64) DEFAULT NULL COMMENT 'Username of recipient of shared item/container.\n',
  `recipient_fullname` varchar(64) DEFAULT NULL COMMENT 'Full name of recipient of shared item/container.\n',
  PRIMARY KEY (`id`),
  KEY `action_index` (`action`),
  KEY `user_id_index` (`user_id`),
  KEY `object_id_index` (`object_id`),
  KEY `username_index` (`username`),
  KEY `created_at_index` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditlog`
--

LOCK TABLES `auditlog` WRITE;
/*!40000 ALTER TABLE `auditlog` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c3p0Test`
--

DROP TABLE IF EXISTS `c3p0Test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `c3p0Test` (
  `a` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c3p0Test`
--

LOCK TABLES `c3p0Test` WRITE;
/*!40000 ALTER TABLE `c3p0Test` DISABLE KEYS */;
/*!40000 ALTER TABLE `c3p0Test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `capabilities`
--

DROP TABLE IF EXISTS `capabilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `capabilities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `capabilities` bigint(20) unsigned NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  KEY `fk_capabilities_1_idx` (`user_id`),
  CONSTRAINT `fk_capabilities_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Defines the capabilities an administrator has.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `capabilities`
--

LOCK TABLES `capabilities` WRITE;
/*!40000 ALTER TABLE `capabilities` DISABLE KEYS */;
INSERT INTO `capabilities` VALUES
(1,0,6143,'2025-12-28 22:15:45','2025-12-28 22:15:45',1);
/*!40000 ALTER TABLE `capabilities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `complexity_requirements`
--

DROP TABLE IF EXISTS `complexity_requirements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `complexity_requirements` (
  `id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'The owner of this complexity configuration',
  `name` varchar(64) NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `type` tinyint(3) unsigned NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `minimum_length` int(10) unsigned NOT NULL,
  `minimum_uppercase` int(10) unsigned NOT NULL DEFAULT 0,
  `minimum_lowercase` int(10) unsigned NOT NULL DEFAULT 0,
  `minimum_special` int(10) unsigned NOT NULL DEFAULT 0,
  `minimum_numbers` int(10) unsigned NOT NULL DEFAULT 0,
  `minimum_entropy` int(10) unsigned NOT NULL DEFAULT 0,
  `max_age_in_days` int(10) unsigned NOT NULL DEFAULT 0,
  `min_age_in_seconds_before_reset` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'To prevent the user from changing their password too frequently (to prevent someone from immediately changing the password after the user had reset it)',
  `extra_checks_enabled` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT 'Should the password be checked against services like haveIBeenPwned, dictionaries, etc.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `complexity_requirements_combo_index` (`name`,`type`),
  KEY `fk_complexity_requirements_1_idx` (`user_id`),
  CONSTRAINT `fk_complexity_requirements_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complexity_requirements`
--

LOCK TABLES `complexity_requirements` WRITE;
/*!40000 ALTER TABLE `complexity_requirements` DISABLE KEYS */;
INSERT INTO `complexity_requirements` VALUES
(1,0,'default',1,1,'2025-12-28 22:15:56','2025-12-28 22:15:56',14,0,0,0,0,18,0,0,30),
(2,0,'default',1,2,'2025-12-28 22:15:56','2025-12-28 22:15:56',18,0,0,0,0,28,0,0,30);
/*!40000 ALTER TABLE `complexity_requirements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_assignments`
--

DROP TABLE IF EXISTS `container_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `container_assignments` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `container_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `count` int(11) NOT NULL DEFAULT 1,
  `version` int(10) unsigned NOT NULL,
  `view_flag` tinyint(1) NOT NULL,
  `modify_flag` tinyint(1) NOT NULL,
  `share_flag` tinyint(1) NOT NULL,
  `update_permitted` tinyint(1) NOT NULL DEFAULT 0,
  `ownership_control` tinyint(1) NOT NULL DEFAULT 0,
  `add_permitted` tinyint(1) NOT NULL DEFAULT 0,
  `shared_explicitly` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ca_multi_index` (`user_id`,`container_id`),
  KEY `fk_container_assignment_1_idx` (`container_id`) USING BTREE,
  KEY `fk_container_assignment_2_idx` (`user_id`),
  KEY `fk_container_assignment_3_idx` (`count`),
  CONSTRAINT `fk_container_assignment_1` FOREIGN KEY (`container_id`) REFERENCES `containers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_container_assignment_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Specifies which users are permitted to view/modify/share the container.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_assignments`
--

LOCK TABLES `container_assignments` WRITE;
/*!40000 ALTER TABLE `container_assignments` DISABLE KEYS */;
INSERT INTO `container_assignments` VALUES
(1,1596075829932644373,0,1,1,1,0,0,1,1,1,1,'2025-12-28 22:15:48','2025-12-28 22:15:48');
/*!40000 ALTER TABLE `container_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_group_assignments`
--

DROP TABLE IF EXISTS `container_group_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `container_group_assignments` (
  `id` bigint(20) unsigned NOT NULL,
  `container_id` bigint(20) unsigned NOT NULL,
  `group_id` bigint(20) unsigned NOT NULL,
  `update_permitted` tinyint(1) NOT NULL,
  `ownership_control` tinyint(1) NOT NULL,
  `add_permitted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_idx_cga_1` (`container_id`,`group_id`),
  KEY `fk_container_group_assignments_1_idx` (`container_id`),
  KEY `fk_container_group_assignments_2_idx` (`group_id`),
  CONSTRAINT `fk_container_group_assignments_1` FOREIGN KEY (`container_id`) REFERENCES `containers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_container_group_assignments_2` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_group_assignments`
--

LOCK TABLES `container_group_assignments` WRITE;
/*!40000 ALTER TABLE `container_group_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_group_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `containers`
--

DROP TABLE IF EXISTS `containers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `containers` (
  `id` bigint(20) unsigned NOT NULL,
  `user_Id` bigint(20) unsigned NOT NULL,
  `parent_id` bigint(20) unsigned DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  `maximum_security_classification` int(10) unsigned NOT NULL DEFAULT 42000 COMMENT 'items above this classification may not be placed in this container. Users security clearance must meet this level to create container of this level.',
  `is_private` tinyint(1) NOT NULL DEFAULT 0,
  `shared` tinyint(1) NOT NULL DEFAULT 0,
  `name` varchar(64) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_name_per_user` (`user_Id`,`name`),
  KEY `fk_container_1_idx` (`user_Id`),
  CONSTRAINT `fk_container_1` FOREIGN KEY (`user_Id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Users can place credentials and other containers within containers.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `containers`
--

LOCK TABLES `containers` WRITE;
/*!40000 ALTER TABLE `containers` DISABLE KEYS */;
INSERT INTO `containers` VALUES
(2000,0,NULL,1,42000,0,0,'Notebook','2025-12-28 22:15:56','2025-12-28 22:15:56'),
(1596075829932644373,0,NULL,1,42000,1,0,'Private','2025-12-28 22:15:48','2025-12-28 22:15:48');
/*!40000 ALTER TABLE `containers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `documents` (
  `id` bigint(20) unsigned NOT NULL,
  `guid` varchar(64) DEFAULT NULL,
  `filename` blob DEFAULT NULL,
  `decryption_key` blob DEFAULT NULL,
  `public_key` blob DEFAULT NULL,
  `file_length` blob DEFAULT NULL,
  `compressed_file_length` blob DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documents`
--

LOCK TABLES `documents` WRITE;
/*!40000 ALTER TABLE `documents` DISABLE KEYS */;
/*!40000 ALTER TABLE `documents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_members` (
  `id` bigint(20) unsigned NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `group_Id` bigint(20) unsigned NOT NULL,
  `user_Id` bigint(20) unsigned NOT NULL,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index4` (`group_Id`,`user_Id`),
  KEY `GroupMembers_FKIndex1` (`user_Id`),
  KEY `GroupMembers_FKIndex2` (`group_Id`),
  CONSTRAINT `fk_group_gm` FOREIGN KEY (`group_Id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_gm` FOREIGN KEY (`user_Id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci PACK_KEYS=0;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_members`
--

LOCK TABLES `group_members` WRITE;
/*!40000 ALTER TABLE `group_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `groups` (
  `id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `global` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Group was created by Group Administrator and is visible to everyone',
  `visible_to_members_only` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Group was created by Group Administrator but only members can see it',
  `minimum_security_clearance` int(10) unsigned NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `name` varchar(128) NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `combo` (`user_id`,`name`),
  KEY `fk_groups_owner_idx` (`user_id`),
  KEY `global_group_index` (`global`),
  KEY `member_visible_index` (`visible_to_members_only`),
  CONSTRAINT `fk_groups_owner` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci PACK_KEYS=0;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_assignments`
--

DROP TABLE IF EXISTS `item_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_assignments` (
  `id` bigint(20) unsigned NOT NULL COMMENT '	',
  `user_id` bigint(20) unsigned NOT NULL,
  `item_id` bigint(20) unsigned NOT NULL,
  `update_permitted` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'This user has been granted access to update the item',
  `ownership_control` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'This user has been granted access to take ownership of the item',
  `shared_explicitly` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'An item can be shared with a user explicitly or with a user via a group. If an item was shared with a user explicitly, this flag is set to true.',
  `count` int(11) NOT NULL DEFAULT 1 COMMENT 'Each time the item is shared with this user, the count is incremented. It can be shared multiple times if the user is a member of multiple groups and the item is shared via group. When the count goes to zero, the assignment can be deleted.',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_item_assignments_uniq` (`user_id`,`item_id`),
  KEY `fk_item_assignments_1_idx` (`item_id`),
  KEY `fk_item_assignments_2_idx` (`user_id`),
  KEY `fk_item_assignments_3_idx` (`count`),
  CONSTRAINT `fk_item_assignments_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_item_assignments_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='This table ties a user to an item. It defines what operations a user can perform on that item.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_assignments`
--

LOCK TABLES `item_assignments` WRITE;
/*!40000 ALTER TABLE `item_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `item_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_groups`
--

DROP TABLE IF EXISTS `item_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_groups` (
  `id` bigint(20) unsigned NOT NULL,
  `item_id` bigint(20) unsigned NOT NULL,
  `group_id` bigint(20) unsigned NOT NULL,
  `shared_explicitly` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'true when an item is shared explicitly with a group (not via a container share with a group)',
  `update_permitted` tinyint(1) NOT NULL,
  `ownership_control` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_ig_1` (`item_id`,`group_id`,`shared_explicitly`),
  KEY `fk_item_groups_1_idx` (`item_id`),
  KEY `fk_item_groups_2_idx` (`group_id`),
  CONSTRAINT `fk_item_groups_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_item_groups_2` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_groups`
--

LOCK TABLES `item_groups` WRITE;
/*!40000 ALTER TABLE `item_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `item_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `container_assignment_id` bigint(20) unsigned NOT NULL,
  `security_classification` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'this works in conjunction with a user''s security clearance\ndocuments exceeding a users security clearance may not be shared with that user',
  `shareable` tinyint(1) NOT NULL,
  `shared` tinyint(1) NOT NULL,
  `name` varchar(64) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `expires_at` datetime DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  `shares_expire_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `combo_idx_uq2` (`user_id`,`name`,`container_assignment_id`),
  KEY `container_assignments_fk_idx` (`container_assignment_id`),
  KEY `fk_user_id_idx` (`user_id`),
  KEY `name_idx` (`name`),
  CONSTRAINT `fk_item_container_assignments` FOREIGN KEY (`container_assignment_id`) REFERENCES `container_assignments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_item_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ldap`
--

DROP TABLE IF EXISTS `ldap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `ldap` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `ldapuri` varchar(128) NOT NULL,
  `base_dn` varchar(128) NOT NULL,
  `bind_dn` varchar(128) DEFAULT NULL,
  `bind_pass` varchar(64) DEFAULT NULL,
  `auth_attr` varchar(64) NOT NULL,
  `start_tls` tinyint(1) DEFAULT NULL,
  `authorize` tinyint(1) DEFAULT NULL,
  `authorization_mode` varchar(64) DEFAULT NULL,
  `attr_val` varchar(64) DEFAULT NULL,
  `filter` varchar(64) DEFAULT NULL,
  `l_group` varchar(64) DEFAULT NULL,
  `group_attr` varchar(64) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `keystore_path` varchar(256) DEFAULT NULL,
  `keystore_password` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ldap`
--

LOCK TABLES `ldap` WRITE;
/*!40000 ALTER TABLE `ldap` DISABLE KEYS */;
/*!40000 ALTER TABLE `ldap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mystery`
--

DROP TABLE IF EXISTS `mystery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `mystery` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mystery`
--

LOCK TABLES `mystery` WRITE;
/*!40000 ALTER TABLE `mystery` DISABLE KEYS */;
/*!40000 ALTER TABLE `mystery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_templates`
--

DROP TABLE IF EXISTS `notification_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_templates` (
  `id` int(10) unsigned NOT NULL,
  `subject` varchar(64) NOT NULL,
  `message` blob NOT NULL,
  `html_message` blob DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Defines the message templates used for notifications sent to users for various system actions.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_templates`
--

LOCK TABLES `notification_templates` WRITE;
/*!40000 ALTER TABLE `notification_templates` DISABLE KEYS */;
INSERT INTO `notification_templates` VALUES
(171,'Dynamic','{\r\n    \"id\":171,\r\n    \"logo_image_url\": \"https://spenego.com/logo.png\",\r\n    \"action_url\": \"\",\r\n    \"button_title\": \"Sign In\",\r\n    \"button_trouble\": \"If you are having trouble clicking the button above, copy and paste the URL below into the address bar of your web browser.\",\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"An Obidos (by Spenego) account is created for you. Please contact your administrator for your initial login credentials. Please click the button below to sign in.\",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"New Obidos (by Spenego) user account.\",\r\n    \"text_message\":\"An Obidos (by Spenego) account is created for you. Please copy and paste the URL below to sign in with the Login Name and Password set by your administrator.\",\r\n    \"title\": \"Obidos (by Spenego) Secure Artifacts Management and Sharing Software\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(209,'Dynamic','{\r\n    \"id\":209,\r\n    \"action_url\": \"\",\r\n    \"button_title\": \"Reset your Password\",\r\n    \"button_trouble\": \"If you are having trouble with the button above, copy and paste the URL below into your web browser.\",\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"We received a request to reset your password. Click the button below to reset your password. If you did not request to reset your password, please ignore this email and the link will expire on its own.\",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"Password Reset Request\",\r\n    \"text_message\": \"We received a request to reset your password. Please copy and paste the link below into your web browser to reset your password. If you did not request to reset your password, please ignore this email and the link will expire on its own.\",\r\n    \"title\": \"Obidos (by Spenego) Secure Artifacts Management and Sharing Software\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(312,'Dynamic','{\r\n    \"id\":312,\r\n    \"action_url\": \"\",\r\n    \"button_title\": \"\",\r\n    \"button_trouble\": \"\",\r\n    \"contact\": \"\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi\",\r\n    \"html_message\":\"You (or someone else) entered this email address while attempting to change the password of a Spenego Obidos account. However, this email address is not in our database of registered users. And therefore, the attempted password change has failed. \",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"Password Reset Request\",\r\n    \"title\": \"Obidos (by Spenego) Secure Artifacts Management and Sharing Software\",\r\n    \"text_message\": \"You (or someone else) entered this email address while attempting to change the password of a Spenego Obidos account. However, this email address is not in our database of registered users. And therefore, the attempted password change has failed.\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(417,'Dynamic','{\r\n    \"id\":417,\r\n    \"action_url\": \"\",\r\n    \"button_title\": \"View Shared Item\",\r\n    \"button_trouble\": \"If you are having trouble with the button above, copy and paste the URL below into your web browser.\",\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"{{owner_name}} has shared a secured item with you. After login, please click on the notification bell for details.\",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com/\",\r\n    \"subject\": \"A secured Item is shared with you\",\r\n    \"text_message\": \"{{owner_name}} has shared a secured item with you. After login, please click on the notification bell for details.\",\r\n    \"title\": \"A secure Item is shared with you\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(524,'Dynamic','{\r\n    \"id\": 524,\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"{{owner_name}} has revoked sharing a secured item  and is no longer available to you. After login, please click on the notification bell for details.\",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"A secured Item is no longer shared with you\",\r\n    \"text_message\": \"{{owner_name}} has revoked sharing a secured item  and is no longer available to you. After login, please click on the notification bell for details.\",\r\n    \"title\": \"Secure Item no longer shared with you\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(525,'Dynamic','{\r\n    \"id\":525,\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"{{owner_name}} has deleted an secured item shared you with you.. After login, please click on the notification bell for details.\",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"A secured shared Item is deleted by owner\",\r\n    \"text_message\": \"{{owner_name}} has deleted an secured item shared you with you.. After login, please click on the notification bell for details.\",\r\n    \"title\": \"A shared item is deleted by the owner\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(633,'Dynamic','{\r\n    \"id\": 633,\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"{{owner_name}} has shared a Container with secured items with you. After login, please click on the notification bell for details.\",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"A Container with secured Items is shared with you\",\r\n    \"text_message\": \"{{owner_name}} has shared a Container with secured items with you. After login, please click on the notification bell for details.\",\r\n    \"title\": \"Container with secure items is shared with you\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(744,'Dynamic','{\r\n    \"id\": 744,\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"{{owner_name}} has revoked sharing a container with secured items and is no longer available to you. After login, please click on the notification bell for details.\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"A container is no longer shared with you\",\r\n    \"text_message\": \"{{owner_name}} has revoked sharing a container with secured items and is no longer available to you. After login, please click on the notification bell for details.\",\r\n    \"title\": \"A Container is no longer shared with you\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(867,'Dynamic','{\r\n    \"id\": 867,\r\n    \"action_url\": \"\",\r\n    \"button_title\": \"Reset your Passphrase\",\r\n    \"button_trouble\": \"If you are having trouble with the button above, copy and paste the URL below into your web browser.\",\r\n    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi {{name}}\",\r\n    \"html_message\": \"We received a request to reset your passphrase. Click the button below to reset your passphrase. If you did not request to reset your passphrase, please ignore this email and the link will expire on its own.\",\r\n    \"warning_message\": \"Warning! This is a last resort if you forget the Passphrase. If you proceed, none of the existing Items/Notes can be retrieved, so they will be deleted. There is no back door, so only reset it if there is no other choice.\",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"Passphrase Reset Request\",\r\n    \"text_message\": \"We received a request to reset your passphrase. Please copy and paste the link below into your web browser to reset your passphrase. If you did not request to reset your passphrase, please ignore this email and the link will expire on its own.\",\r\n    \"title\": \"Obidos (by Spenego) Secure Artifacts Management and Sharing Software\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72),
(869,'Dynamic','{\r\n    \"id\": 869,\r\n    \"action_url\": \"\",\r\n    \"button_title\": \"\",\r\n    \"button_trouble\": \"\",\r\n    \"contact\": \"\",\r\n    \"footer\": \"Example Inc., Address, City, State/Region, Country.\",\r\n    \"hello\": \"Hi\",\r\n    \"html_message\":\"You (or someone else) entered this email address while attempting to change the passphrase of a Spenego Obidos account. However, this email address is not in our database of registered users. And therefore, the attempted passphrase change has failed. \",\r\n    \"product_name\": \"Obidos Digital Artifacts Management and Sharing Platform from Spenego\",\r\n    \"product_url\": \"https://spenego.com\",\r\n    \"subject\": \"Passphrase Reset Request\",\r\n    \"title\": \"Obidos (by Spenego) Secure Artifacts Management and Sharing Software\",\r\n    \"text_message\": \"You (or someone else) entered this email address while attempting to change the passphrase of a Spenego Obidos account. However, this email address is not in our database of registered users. And therefore, the attempted passphrase change has failed.\"\r\n}\r\n',NULL,'2025-12-28 22:15:28','2025-12-28 22:15:28',72);
/*!40000 ALTER TABLE `notification_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'recipient of notification\n',
  `owner_id` bigint(20) unsigned DEFAULT NULL COMMENT 'sender of notification or message, null if from system',
  `target_id` bigint(20) unsigned DEFAULT NULL,
  `action` int(10) unsigned NOT NULL COMMENT '0 - message from system\n1 - item share\n2 - item revoke\n3 - container share\n4 - container revoke',
  `unread` tinyint(4) NOT NULL,
  `created_at` datetime NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notifications_1_idx` (`user_id`),
  KEY `fk_notifications_2_idx` (`owner_id`),
  KEY `notifications_action_index` (`action`) USING BTREE,
  KEY `target_id_idx` (`target_id`),
  CONSTRAINT `fk_notifications_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_notifications_2` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_members`
--

DROP TABLE IF EXISTS `organization_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization_members` (
  `id` bigint(20) unsigned NOT NULL,
  `org_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index4` (`org_id`,`user_id`),
  KEY `fk_user_id_idx` (`user_id`),
  KEY `fk_org_id_idx` (`org_id`),
  CONSTRAINT `fk_org_id_om` FOREIGN KEY (`org_id`) REFERENCES `organizations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_id_om` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci PACK_KEYS=0;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_members`
--

LOCK TABLES `organization_members` WRITE;
/*!40000 ALTER TABLE `organization_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `organization_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organizations`
--

DROP TABLE IF EXISTS `organizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `organizations` (
  `id` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci PACK_KEYS=0;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organizations`
--

LOCK TABLES `organizations` WRITE;
/*!40000 ALTER TABLE `organizations` DISABLE KEYS */;
/*!40000 ALTER TABLE `organizations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset`
--

DROP TABLE IF EXISTS `password_reset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset` (
  `id` bigint(20) unsigned NOT NULL,
  `state` tinyint(4) NOT NULL COMMENT '0 - reset requested\n1 - send warning email (requested email address not in system)\n2 - email sent',
  `user_id` bigint(20) unsigned DEFAULT NULL,
  `token` varchar(64) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL COMMENT 'The user supplied an email address since they forgot their username. if user_id is null, the email address does not exist within the system.',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `token_UNIQUE` (`token`),
  KEY `pw_reset_user_id_fk_idx` (`user_id`),
  KEY `pw_reset_state_idx` (`state`),
  CONSTRAINT `pw_reset_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset`
--

LOCK TABLES `password_reset` WRITE;
/*!40000 ALTER TABLE `password_reset` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending_item_hashes`
--

DROP TABLE IF EXISTS `pending_item_hashes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pending_item_hashes` (
  `id` bigint(20) unsigned NOT NULL COMMENT '	',
  `user_id` bigint(20) unsigned NOT NULL,
  `item_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pending_item_hashes_1_idx` (`item_id`),
  KEY `fk_pending_item_hashes_2_idx` (`user_id`),
  CONSTRAINT `fk_item_assignments_100` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_item_assignments_200` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='When a user updates an item, new item_value_hashes are created so that the user can search for encrypted values.  The item_value_hashes must also be create for any users sharing that item since the hashes are based on the user''s private key. Since that private key is only available to the owner of the key, the creation of the new item_value_hashes must be initiated after the user logs in. This table exists as notifications to those users that the item_value_hashes need to be created.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending_item_hashes`
--

LOCK TABLES `pending_item_hashes` WRITE;
/*!40000 ALTER TABLE `pending_item_hashes` DISABLE KEYS */;
/*!40000 ALTER TABLE `pending_item_hashes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pre_defined_values`
--

DROP TABLE IF EXISTS `pre_defined_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pre_defined_values` (
  `id` bigint(20) NOT NULL,
  `type` int(10) unsigned NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `type_index` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Various forms require string values that are common to most users and in fact should be identical. This table defines commonly used strings and types. For example, this can be used for the Office or Region field of a User. We would want user to select from a set of values defined by the admin. Values are sub-divided into types, for example, Office type = 1, Region type = 2.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pre_defined_values`
--

LOCK TABLES `pre_defined_values` WRITE;
/*!40000 ALTER TABLE `pre_defined_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `pre_defined_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smtp_config`
--

DROP TABLE IF EXISTS `smtp_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `smtp_config` (
  `id` bigint(20) unsigned NOT NULL,
  `name` varchar(32) NOT NULL,
  `smtp_server` varchar(64) NOT NULL,
  `smtp_port` int(10) unsigned NOT NULL,
  `use_authentication` tinyint(1) NOT NULL,
  `use_ssl` tinyint(1) NOT NULL,
  `use_start_tls` tinyint(1) NOT NULL,
  `to_address` varchar(255) DEFAULT NULL,
  `from_address` varchar(255) DEFAULT NULL,
  `smtp_username` varchar(255) DEFAULT NULL,
  `smtp_password` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smtp_config`
--

LOCK TABLES `smtp_config` WRITE;
/*!40000 ALTER TABLE `smtp_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `smtp_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `id` bigint(20) NOT NULL,
  `use_security_clearances` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'If true, security clearances are checked against the security classification of items before sharing.',
  `session_timeout_seconds` int(11) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `memory_wipe_delay` int(10) unsigned NOT NULL COMMENT 'delay between object being marked for wiping and being wiped - 0 means no wipe',
  `server_port` int(10) unsigned NOT NULL DEFAULT 443,
  `updated_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `scheme` varchar(16) DEFAULT NULL,
  `date_format` varchar(32) DEFAULT NULL,
  `two_factor_auth_issuer` varchar(64) DEFAULT NULL,
  `password_complexity_name` varchar(64) NOT NULL COMMENT 'The name of the complexity requirements record to use for system wide password complexity rules',
  `fqdn` varchar(128) NOT NULL DEFAULT 'spenego.com',
  `admin_email` varchar(128) DEFAULT NULL COMMENT 'delay (in milliseconds) between object being marked for wiping and being wiped - 0 means no wipe',
  `context_path` varchar(255) DEFAULT NULL,
  `document_storage_directory` varchar(255) NOT NULL DEFAULT '/usr/local/spenego/obidos/DataStore' COMMENT 'Where uploaded files are stored.',
  `license` varchar(4096) DEFAULT 'YAML Encoded license object string',
  `license_public_key` blob DEFAULT NULL,
  `sms_key` blob DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT INTO `system_config` VALUES
(1,0,600,NULL,2000,8443,'2020-04-30 23:11:21','2018-11-12 00:00:00','https','mm/dd/yyyy','t105.spenego.com','default','test.spenego.com',NULL,NULL,'/usr/local/spenego/obidos/DataStore','LS0tCmxpY2Vuc2VUeXBlOiAiT3BlbnNvdXJjZSIKcHJvZHVjdE5hbWU6ICJTcGVuZWdvIE9iaWRvcyIKY29tcGFueU5hbWU6ICJPYmlkb3MgT3BlblNvdXJjZSIKY29tcGFueUVtYWlsOiAib3BlbnNvdXJjZUBzcGVuZWdvLmNvbSIKY3VzdG9tZXJJZDogInNwZW5lZ28iCnBob25lTnVtYmVyOiBudWxsCmNvbnRhY3ROYW1lOiBudWxsCnV1aWQ6IG51bGwKbGljZW5zZVRlcm1zOiAiVGhpcyBpcyBhbiBPcGVuIFNvdXJjZSBFZGl0aW9uIGxpY2Vuc2UgZm9yIE9iaWRvcyBzb2Z0d2FyZSBmcm9tIFNwZW5lZ29cCiAgXCBTb2Z0d2FyZSBMTEMuXG5UaGVyZSBhcmUgbm8gd2FycmFudGllcyBvciBzdXBwb3J0IGZvciB0aGlzIGVkaXRpb24uXG5QbGVhc2UgdmlzaXRcCiAgXCA8YSBocmVmPVwiaHR0cHM6Ly9zcGVuZWdvLmNvbVwiIHRhcmdldD1cIl9ibGFua1wiPmh0dHBzOi8vc3BlbmVnby5jb208L2E+IGZvclwKICBcIG1vcmUgaW5mbyBhbmQgb3RoZXIgbGljZW5zaW5nIG9wdGlvbnMuIFxuPGJyLz5cblRoYW5rIHlvdSBmb3IgdXNpbmcgT2JpZG9zIVxuIgpub25jZTogInJ5Y3JkSm83QVVoQVpENnczZjNkVG5BbFdnbklKc1lrZVhoZW5nekh0WE09IgpzaWduYXR1cmU6ICJEMWEraW5NeGZGQ3c1SFZIVHJ0NTNuNWpQQTVjUFlMQjFkdlNUSzI1djFNZmN0ZzVYL0ZZdkpGZXRLU1RzQW1jazRWdEM1TzJIbGthSjJtbFZnOTVEZz09IgptYXhVc2VyczogMApleHBpcmF0aW9uRXBvY2g6IG51bGwKc2lnbmluZ0Vwb2NoOiAxNzgxNTQ3ODQ0Cmhhc0V4cGlyZWQ6IG51bGwKYWxsb3dEb2N1bWVudFVwbG9hZGluZzogdHJ1ZQphbGxvd1FSQ29kZVVwbG9hZGluZzogdHJ1ZQphbGxvd0F1ZGl0aW5nOiB0cnVlCmFsbG93U01UUDogdHJ1ZQphbGxvd0NvbnRhaW5lclNoYXJpbmc6IHRydWUKYWxsb3dDb250YWluZXJPd25lcnNoaXBUcmFuc2ZlcjogdHJ1ZQpzbm1wU3VwcG9ydDogZmFsc2UKc21zU3VwcG9ydDogdHJ1ZQo=','C7ÚÉ~5+oÒr·yƒ¬¾ú©+_)&²&sŸæCÅ9To',NULL);
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_data`
--

DROP TABLE IF EXISTS `user_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_data` (
  `id` bigint(20) unsigned NOT NULL,
  `profile_pic` blob DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_data`
--

LOCK TABLES `user_data` WRITE;
/*!40000 ALTER TABLE `user_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_defined_blobs`
--

DROP TABLE IF EXISTS `user_defined_blobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_defined_blobs` (
  `id` bigint(20) unsigned NOT NULL,
  `encryption_mode` tinyint(3) unsigned NOT NULL COMMENT '0 - raw, unpadded\n1 - padded values (32 byte boundary)\n',
  `value` blob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='A table used to store encrypted data.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_defined_blobs`
--

LOCK TABLES `user_defined_blobs` WRITE;
/*!40000 ALTER TABLE `user_defined_blobs` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_defined_blobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_defined_field_values`
--

DROP TABLE IF EXISTS `user_defined_field_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_defined_field_values` (
  `id` bigint(20) unsigned NOT NULL,
  `user_defined_type_value_id` bigint(20) unsigned NOT NULL,
  `user_defined_field_id` bigint(20) unsigned NOT NULL,
  `blob_value_id` bigint(20) unsigned DEFAULT NULL,
  `document_id` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `document_blob_value_id_UNIQUE` (`document_id`,`blob_value_id`),
  KEY `field_idx` (`user_defined_field_id`),
  KEY `blob_id_idx` (`blob_value_id`),
  KEY `fk_udt_id_idx` (`user_defined_type_value_id`),
  CONSTRAINT `blob_id` FOREIGN KEY (`blob_value_id`) REFERENCES `user_defined_blobs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_document_id` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_field_id` FOREIGN KEY (`user_defined_field_id`) REFERENCES `user_defined_fields` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_row_id` FOREIGN KEY (`user_defined_type_value_id`) REFERENCES `user_defined_type_values` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_defined_field_values`
--

LOCK TABLES `user_defined_field_values` WRITE;
/*!40000 ALTER TABLE `user_defined_field_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_defined_field_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_defined_fields`
--

DROP TABLE IF EXISTS `user_defined_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_defined_fields` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'similar to a database column id',
  `type_id` bigint(20) unsigned NOT NULL,
  `type` bigint(20) unsigned NOT NULL COMMENT '1 = string\n2 = int\n3 = boolean\n4 = encrypted\n5 = document\n6 = qrcode\n> 10000000 = UDT ID',
  `position` int(10) unsigned NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `name` varchar(128) NOT NULL COMMENT 'similar to a database column name',
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `type_fk_idx` (`type_id`),
  CONSTRAINT `type_fk` FOREIGN KEY (`type_id`) REFERENCES `user_defined_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Same Pa a database column.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_defined_fields`
--

LOCK TABLES `user_defined_fields` WRITE;
/*!40000 ALTER TABLE `user_defined_fields` DISABLE KEYS */;
INSERT INTO `user_defined_fields` VALUES
(2000,1001,4,1,1,'Notes','2025-12-28 22:15:48','2025-12-28 22:15:48'),
(488343245017258377,1005,4,5,1,'Coverage Amount','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(655004398029063926,1011,4,3,1,'Comment 1','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(766413319065103799,1005,4,1,1,'Insurance Company','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(905409510222921761,1011,4,2,1,'Account','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(1037925517068876791,1005,4,7,1,'Notes','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(1073993775523752652,1004,4,7,1,'Details','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(1429565346496945741,1002,4,1,1,'Full Name','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(1621336973847377017,1009,4,1,1,'Venue/Location','2025-12-28 22:15:49','2025-12-28 22:15:49'),
(1675913398077870009,1006,4,3,1,'License/Key','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(1716830801759087463,1007,4,6,1,'Dues','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(1906010079773699072,1003,4,3,1,'Password','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(2143232760707397628,1007,4,1,1,'Institution','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(2184764882178136841,1004,4,6,1,'Support Phone#','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(2201409845934018614,1010,4,4,1,'Comments','2025-12-28 22:15:49','2025-12-28 22:15:49'),
(2274407186200235648,1007,4,5,1,'Expiration Date','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(2375649353309962458,1000,4,8,1,'Bank Web Site','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(2424497276471995866,1000,4,9,1,'Bank Phone Number','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(2425170265731012291,1006,4,1,1,'Vendor','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(2494343028421881627,1002,4,2,1,'Title','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(2559548483311756300,1004,4,3,1,'Expiration','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(2628504119895221274,1002,4,9,1,'Twitter','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(3075892419078580165,1008,4,3,1,'Customer Id','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(3160554184122680731,1008,4,2,1,'Product/Item','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(3339910248234214597,1008,4,7,1,'Notes','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(3422661497922703665,1008,4,6,1,'Expiration Date','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(3426644777466236907,1002,4,3,1,'Address','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(3537661987937073039,1007,4,4,1,'Contact Phone#','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(3614937784735509784,1011,6,5,1,'Add 2FA QR Code','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(3626761585660117821,1002,4,5,1,'Phone (M)','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(3666767270833646127,1002,4,11,1,'Notes','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(3873099757588190335,1012,4,1,1,'Info','2025-12-28 22:15:56','2025-12-28 22:15:56'),
(3950384624711686886,1007,4,8,1,'Notes','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(4005860008958997894,1011,4,4,1,'Comment 2','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(4049041890535074369,1012,4,2,1,'Comment','2025-12-28 22:15:56','2025-12-28 22:15:56'),
(4073809094963709753,1002,4,8,1,'Fax','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(4092748885381107594,1000,4,5,1,'Account Owner','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(4279489225431562752,1000,4,2,1,'Routing Number','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(4411409702651336711,1008,4,4,1,'Contract#','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(4753549451095984611,1000,4,10,1,'Other Details','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(4815795397549750905,1005,4,2,1,'Policy#','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(4876286138977072925,1012,5,3,1,'Attachment','2025-12-28 22:15:56','2025-12-28 22:15:56'),
(5147099917010439157,1000,4,7,1,'Swift/IFSC Code','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(5164982241418138382,1006,4,2,1,'Product','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(5184141928436384611,1009,4,2,1,'SSID','2025-12-28 22:15:49','2025-12-28 22:15:49'),
(5189967075987809794,1005,4,3,1,'Type (Auto/Building/..)','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(5315718799898957357,1000,4,4,1,'Account Type','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(5523580127054888601,1004,4,2,1,'Card Number','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(5646337896832789303,1006,4,4,1,'Expiration','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(5657776033061900080,1008,4,5,1,'Support Phone#','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(6260919189309885135,1011,4,1,1,'Issuer','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(6503831479175606307,1004,4,1,1,'Brand','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(6537407060556268638,1007,4,3,1,'Subscriber Name','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(6605691228772766021,1008,4,1,1,'Vendor','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(6632186714263748499,1003,4,1,1,'Target','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(6644684795556814732,1000,4,1,1,'Bank Name','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(6951725385732047364,1009,4,4,1,'Details','2025-12-28 22:15:49','2025-12-28 22:15:49'),
(6979204393793961632,1007,4,7,1,'Payment Method','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(7024793134078248848,1002,4,4,1,'Email','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(7131643610470393320,1009,4,3,1,'WiFi Password','2025-12-28 22:15:49','2025-12-28 22:15:49'),
(7137288026855618015,1004,4,4,1,'Security Code','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(7205020188282032186,1004,4,5,1,'Issuing Bank','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(7216354890223536569,1000,4,6,1,'ATM/Debit card PIN','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(7255015560223996843,1010,4,2,1,'Web Url','2025-12-28 22:15:49','2025-12-28 22:15:49'),
(7401696623410961054,1007,4,2,1,'Membership Number','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(7560644026904150273,1000,4,3,1,'Account Number','2025-12-28 22:15:55','2025-12-28 22:15:55'),
(7708797274512595819,1002,4,7,1,'Phone (H)','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(8124563149648893147,1003,4,4,1,'Details','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(8193275062032738833,1005,4,4,1,'Expiration Date','2025-12-28 22:15:53','2025-12-28 22:15:53'),
(8204302019006731804,1003,4,2,1,'Username','2025-12-28 22:15:52','2025-12-28 22:15:52'),
(8499948868674096362,1010,4,1,1,'Title','2025-12-28 22:15:48','2025-12-28 22:15:48'),
(8518359679243113769,1008,5,8,1,'Attachment','2025-12-28 22:15:50','2025-12-28 22:15:50'),
(8565262819193164986,1002,4,6,1,'Phone (V)','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(8716461995139231463,1010,4,3,1,'Description','2025-12-28 22:15:49','2025-12-28 22:15:49'),
(8756531303520752408,1006,4,5,1,'Details','2025-12-28 22:15:51','2025-12-28 22:15:51'),
(9071422000093145250,1002,4,10,1,'Facebook','2025-12-28 22:15:54','2025-12-28 22:15:54'),
(9220034768682626390,1005,4,6,1,'Phone (Claims)','2025-12-28 22:15:53','2025-12-28 22:15:53');
/*!40000 ALTER TABLE `user_defined_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_defined_strings`
--

DROP TABLE IF EXISTS `user_defined_strings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_defined_strings` (
  `id` bigint(20) unsigned NOT NULL,
  `value` blob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_defined_strings`
--

LOCK TABLES `user_defined_strings` WRITE;
/*!40000 ALTER TABLE `user_defined_strings` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_defined_strings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_defined_type_values`
--

DROP TABLE IF EXISTS `user_defined_type_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_defined_type_values` (
  `id` bigint(20) unsigned NOT NULL,
  `user_defined_type_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `item_assignment_id` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_udt_id_idx` (`user_defined_type_id`),
  KEY `fk_user_id_idx` (`user_id`),
  KEY `fk_udtv_item_assignment_id_idx` (`item_assignment_id`),
  CONSTRAINT `fk_udtv_item_assignment_id` FOREIGN KEY (`item_assignment_id`) REFERENCES `item_assignments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_udtv_udt_id` FOREIGN KEY (`user_defined_type_id`) REFERENCES `user_defined_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_udtv_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Defines a row of user defined fields';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_defined_type_values`
--

LOCK TABLES `user_defined_type_values` WRITE;
/*!40000 ALTER TABLE `user_defined_type_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_defined_type_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_defined_types`
--

DROP TABLE IF EXISTS `user_defined_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_defined_types` (
  `id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `free_form_item_id` bigint(20) unsigned DEFAULT NULL COMMENT 'allows cascade delete when Item is deleted, not used otherwise',
  `personal` tinyint(1) NOT NULL COMMENT 'See the comments on ''global'' for a descripton of Global/Personal/Local types and when COW is done.',
  `ad_hoc` tinyint(1) NOT NULL DEFAULT 0,
  `global` tinyint(1) DEFAULT NULL COMMENT 'We need to have a global column so we can have a global unique name index. The global flag also helps us implement a tertiary state for UDF Types necessary for COW references.\n1 - Global - everyone can see when searched\n2 - Local - (global set to null, personal false) a local copy. UDF types become local when the Global type is modified. When this happens, A copy of the global type is made and the former global type is made Local by clearing the global flag.\n3 - Personal - If a type is created personal, it is personal forever. If a user attempts to modify a Global or Local type, the type becomes Personal as a copy of the Global type is made.\n\nShould we allow the user the option to affect changes on all items using a previously Global type?',
  `name` varchar(128) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `combo_idx_uq1` (`user_id`,`name`,`personal`),
  UNIQUE KEY `combo_idx_uq2` (`name`,`global`),
  KEY `user_id_fk_idx` (`user_id`),
  KEY `fk_free_form_item_id_idx` (`free_form_item_id`),
  CONSTRAINT `fk_free_form_item_id` FOREIGN KEY (`free_form_item_id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='Same paradigm as a database table.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_defined_types`
--

LOCK TABLES `user_defined_types` WRITE;
/*!40000 ALTER TABLE `user_defined_types` DISABLE KEYS */;
INSERT INTO `user_defined_types` VALUES
(1000,0,NULL,0,0,1,'Bank Account','2025-12-28 22:15:55','2025-12-28 22:15:55',1),
(1001,0,NULL,0,0,1,'Notes','2025-12-28 22:15:48','2025-12-28 22:15:48',1),
(1002,0,NULL,0,0,1,'Contact Information','2025-12-28 22:15:53','2025-12-28 22:15:53',1),
(1003,0,NULL,0,0,1,'Login Credentials','2025-12-28 22:15:52','2025-12-28 22:15:52',1),
(1004,0,NULL,0,0,1,'Credit Card','2025-12-28 22:15:54','2025-12-28 22:15:54',1),
(1005,0,NULL,0,0,1,'Insurance Details','2025-12-28 22:15:52','2025-12-28 22:15:52',1),
(1006,0,NULL,0,0,1,'Software License Key','2025-12-28 22:15:50','2025-12-28 22:15:50',1),
(1007,0,NULL,0,0,1,'Membership/Subscription','2025-12-28 22:15:51','2025-12-28 22:15:51',1),
(1008,0,NULL,0,0,1,'Support Contract','2025-12-28 22:15:50','2025-12-28 22:15:50',1),
(1009,0,NULL,0,0,1,'WiFi Credentials','2025-12-28 22:15:49','2025-12-28 22:15:49',1),
(1010,0,NULL,0,0,1,'Web Site','2025-12-28 22:15:48','2025-12-28 22:15:48',1),
(1011,0,NULL,0,0,1,'2FA QR Code','2025-12-28 22:15:51','2025-12-28 22:15:51',1),
(1012,0,NULL,0,0,1,'File Share','2025-12-28 22:15:56','2025-12-28 22:15:56',1);
/*!40000 ALTER TABLE `user_defined_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'integer userid from AAS backend system',
  `version` int(10) unsigned NOT NULL,
  `hide_item_delay` int(10) unsigned NOT NULL DEFAULT 10 COMMENT 'The time in seconds until the contents of an item are hidden from view.',
  `default_page` int(10) unsigned NOT NULL DEFAULT 0,
  `security_clearance` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '0 - none\n100 - public trust\n200 - confidential\n300 - secret\n400 - top secret\n\n',
  `preference_flags` bigint(20) unsigned NOT NULL DEFAULT 0,
  `login_count` int(10) unsigned NOT NULL DEFAULT 0,
  `unsuccessful_login_attempts` int(10) unsigned NOT NULL DEFAULT 0,
  `deleted` tinyint(1) NOT NULL,
  `locked` tinyint(1) NOT NULL,
  `administrator` tinyint(1) NOT NULL,
  `password_change_required` tinyint(1) NOT NULL,
  `twofa_required` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'true if the admin configured this user as someone who MUST use 2FA',
  `twofa_password_reset_enabled` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `last_login_via_cookie` datetime DEFAULT NULL,
  `last_login_via_credentials` datetime DEFAULT NULL,
  `last_password_reset_time` datetime DEFAULT NULL,
  `minimum_password_age` bigint(20) DEFAULT NULL COMMENT 'in days',
  `maximum_password_age` bigint(20) DEFAULT NULL COMMENT 'in days',
  `username` varchar(64) NOT NULL,
  `auth_source` varchar(64) NOT NULL DEFAULT 'local',
  `two_factor_auth_secret` varchar(64) DEFAULT NULL,
  `salt` varchar(24) DEFAULT NULL,
  `nonce` varchar(32) DEFAULT NULL,
  `publickey` varchar(44) DEFAULT NULL,
  `privatekey` varchar(64) DEFAULT NULL,
  `fullname` varchar(255) DEFAULT NULL,
  `email1` varchar(255) DEFAULT NULL,
  `email2` varchar(255) DEFAULT NULL,
  `email3` varchar(255) DEFAULT NULL,
  `phone` varchar(64) DEFAULT NULL,
  `mobile1` varchar(64) DEFAULT NULL,
  `mobile2` varchar(64) DEFAULT NULL,
  `mobile3` varchar(64) DEFAULT NULL,
  `twitter` varchar(64) DEFAULT NULL,
  `facebook` varchar(64) DEFAULT NULL,
  `password_digest` varchar(255) DEFAULT NULL,
  `last_password_digest_1` varchar(255) DEFAULT NULL,
  `last_password_digest_2` varchar(255) DEFAULT NULL,
  `last_password_digest_3` varchar(255) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `authURN` varchar(255) DEFAULT NULL,
  `employee_id` varchar(255) DEFAULT NULL,
  `region_id` varchar(255) DEFAULT NULL,
  `job_title` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `office` varchar(255) DEFAULT NULL,
  `instant_message_id` varchar(255) DEFAULT NULL,
  `profile_pic` blob DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci PACK_KEYS=0 COMMENT='This is the table for holding authentication information. Accounts can be set locally or for remote authentication. ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
(0,2,10,0,100,0,0,0,0,0,1,1,0,0,'2025-12-28 22:15:45','2025-12-28 22:15:48',NULL,NULL,NULL,'2025-12-28 22:15:48',NULL,NULL,'admin','local',NULL,NULL,NULL,NULL,NULL,'Obidos Administrator','admin@obidos.local',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'$argon2id$v=19$m=65536,t=2,p=1$bIsZFx5MxlytWkbqEI/Ogg$lYlUnnyEF/sWpADsRuDlJKRcCm9Azv1mL3Fk3NHqVRs',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `version` (
  `id` int(10) unsigned NOT NULL,
  `upgrade_start` timestamp NOT NULL,
  `upgrade_end` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `version`
--

LOCK TABLES `version` WRITE;
/*!40000 ALTER TABLE `version` DISABLE KEYS */;
INSERT INTO `version` VALUES
(72,'2025-12-28 22:15:26','2025-12-28 22:15:26');
/*!40000 ALTER TABLE `version` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-12-28 22:15:57
