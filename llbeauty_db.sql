-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: llbeauty_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_notifications`
--

DROP TABLE IF EXISTS `admin_notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `link_url` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_notifications`
--

LOCK TABLES `admin_notifications` WRITE;
/*!40000 ALTER TABLE `admin_notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (269,'2026-06-22 16:08:59.156578','admin@llbeauty.com','L.L. Beauty Admin','$2a$10$s4gTzHfUQn2aIPNMZULP5.sZ/zNtdONOwgocbmVEy1rIGnh9yzxgi');
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agent_profiles`
--

DROP TABLE IF EXISTS `agent_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agent_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `agent_id` varchar(255) NOT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk6sn7budpmjnn8dposhoe5d0` (`agent_id`),
  UNIQUE KEY `UKmdpwfv3egvqq4q9oqx1wc9t88` (`user_id`),
  UNIQUE KEY `agent_id` (`agent_id`),
  UNIQUE KEY `UKsg482o9ihm083y4t5nn3ypk17` (`referral_code`),
  CONSTRAINT `FK9w9b1ollcdraeirmj2i55kxrx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agent_profiles`
--

LOCK TABLES `agent_profiles` WRITE;
/*!40000 ALTER TABLE `agent_profiles` DISABLE KEYS */;
INSERT INTO `agent_profiles` VALUES (7,'2026-06-19 08:52:21.519069','LLB-EXE-12','REF12361','ACTIVE',12),(8,'2026-06-21 08:58:05.612725','LLB-EXE-1','REF1496','ACTIVE',1);
/*!40000 ALTER TABLE `agent_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appointment_date` date DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `service_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `time_slot` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `user_mobile` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `advance_paid` double DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `services` varchar(1000) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `beautician_id` bigint DEFAULT NULL,
  `beautician_name` varchar(255) DEFAULT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `razorpay_order_id` varchar(255) DEFAULT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,'2026-06-25','2026-06-22 16:29:27.698749','Gold Glow Facial','COMPLETED','12:00 PM',2,'9876543210','Kalyani Vilas Bhawar',100,'PAID','Gold Glow Facial','LL-SLOT-1532',NULL,NULL,'REF1496',1999,'order_T4k97K9pxzxsWC','pay_T4k9CrKZvgXxgB');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `performed_by` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` VALUES (1,'USER_DELETED','User prachi@gmail.com deleted','admin@llbeauty.com','2026-06-12 06:58:33.838013'),(2,'ORDER_UPDATED','Order #7 status updated to PENDING','admin@llbeauty.com','2026-06-12 07:01:35.556558'),(3,'ORDER_UPDATED','Order #7 status updated to CANCELLED','admin@llbeauty.com','2026-06-12 07:01:45.507683'),(4,'ORDER_UPDATED','Order #1 status updated to DELIVERED','admin@llbeauty.com','2026-06-12 15:53:41.742164'),(5,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-12 15:54:08.937870'),(6,'USER_DELETED','User kalyanibhawar3@gmail.com deleted','admin@llbeauty.com','2026-06-12 15:54:16.535249'),(7,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-12 16:01:32.801028'),(8,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-13 14:30:15.754252'),(9,'USER_DELETED','User kalyanibhawar3@gmail.com deleted','admin@llbeauty.com','2026-06-13 14:30:19.188424'),(10,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-13 14:30:27.066677'),(11,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-15 04:59:28.996369'),(12,'ORDER_UPDATED','Order #2 status updated to DELIVERED','admin@llbeauty.com','2026-06-15 07:19:09.628175'),(13,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-16 06:50:33.051562'),(14,'ORDER_UPDATED','Order #2 status updated to DELIVERED','admin@llbeauty.com','2026-06-16 06:51:43.065182'),(15,'ORDER_UPDATED','Order #1 status updated to DELIVERED','admin@llbeauty.com','2026-06-17 05:06:47.268993'),(16,'ORDER_UPDATED','Order #7 status updated to DELIVERED','admin@llbeauty.com','2026-06-17 05:07:05.330722'),(17,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-17 12:41:36.230978'),(18,'ORDER_UPDATED','Order #2 status updated to SHIPPED','admin@llbeauty.com','2026-06-17 12:47:58.610835'),(19,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-17 16:21:33.643428'),(20,'USER_DELETED','User kalyanibhawar465@gmail.com deleted','admin@llbeauty.com','2026-06-17 16:22:24.877788'),(21,'ORDER_UPDATED','Order #6 status updated to DELIVERED','admin@llbeauty.com','2026-06-17 20:02:51.131155'),(22,'ORDER_UPDATED','Order #2 status updated to DELIVERED','admin@llbeauty.com','2026-06-21 18:17:56.069899'),(23,'ORDER_UPDATED','Order #1 status updated to DELIVERED','admin@llbeauty.com','2026-06-22 16:38:10.900646');
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `beauticians`
--

DROP TABLE IF EXISTS `beauticians`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `beauticians` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `specialization` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beauticians`
--

LOCK TABLES `beauticians` WRITE;
/*!40000 ALTER TABLE `beauticians` DISABLE KEYS */;
/*!40000 ALTER TABLE `beauticians` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commissions`
--

DROP TABLE IF EXISTS `commissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `agent_profile_id` bigint DEFAULT NULL,
  `appointment_id` bigint DEFAULT NULL,
  `commission_type` varchar(255) DEFAULT 'REGISTRATION',
  PRIMARY KEY (`id`),
  KEY `FK74o51v4w0wquckjqtc2j19n9r` (`agent_profile_id`),
  KEY `FK_commissions_appointment` (`appointment_id`),
  CONSTRAINT `FK74o51v4w0wquckjqtc2j19n9r` FOREIGN KEY (`agent_profile_id`) REFERENCES `agent_profiles` (`id`),
  CONSTRAINT `FK_commissions_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
INSERT INTO `commissions` VALUES (3,50.00,'2026-06-19 08:56:01.378114','User Registration Referral','APPROVED',7,NULL,'REGISTRATION'),(4,147.44,'2026-06-19 11:24:11.771257','Product Order #10','APPROVED',7,NULL,'REGISTRATION'),(5,50.00,'2026-06-19 16:03:58.501586','User Registration Referral','APPROVED',7,NULL,'REGISTRATION'),(6,29.50,'2026-06-19 16:05:40.001304','Product Order #14','APPROVED',7,NULL,'REGISTRATION'),(7,50.00,'2026-06-21 08:59:30.312653','User Registration Referral','APPROVED',8,NULL,'SALON_BOOKING'),(8,147.44,'2026-06-21 09:01:18.643886','Product Order #1','APPROVED',8,NULL,'SALON_BOOKING'),(9,149.90,'2026-06-21 09:07:56.968502','Commission for Appointment #1','APPROVED',8,1,'SALON_BOOKING'),(10,100000.00,'2026-06-22 05:57:56.132001','Franchise Referral Commission - Lead #3','APPROVED',8,NULL,'FRANCHISE'),(11,100000.00,'2026-06-22 07:32:49.132075','Franchise Referral Commission - Lead #4','APPROVED',8,NULL,'FRANCHISE'),(12,89.90,'2026-06-22 10:48:56.288221','Commission for Appointment #10','APPROVED',8,10,'SALON_BOOKING'),(13,149.95,'2026-06-22 12:16:39.661255','Membership Referral - Eva Pink Card - User: Kalyani Vilas Bhawar','PENDING',8,NULL,'MEMBERSHIP'),(14,149.95,'2026-06-22 12:39:59.107080','Membership Referral - Eva Pink Card - User: Kalyani','PENDING',8,NULL,'MEMBERSHIP'),(15,499.95,'2026-06-22 12:44:29.531503','Membership Referral - Eva Gold Card - User: Kalyani','PAID',8,NULL,'MEMBERSHIP'),(16,50.00,'2026-06-22 16:27:44.487461','User Registration Referral','APPROVED',8,NULL,'SALON_BOOKING'),(17,206.44,'2026-06-22 16:29:03.346682','Product Order #1','APPROVED',8,NULL,'SALON_BOOKING'),(18,149.95,'2026-06-22 16:31:42.096679','Membership Referral - Eva Pink Card - User: Kalyani Vilas Bhawar','PAID',8,NULL,'MEMBERSHIP'),(19,150000.00,'2026-06-22 16:33:44.768490','Franchise Referral Commission - Lead #1','APPROVED',8,NULL,'FRANCHISE'),(20,499.95,'2026-06-22 16:58:00.677607','Membership Referral - Eva Gold Card - User: Kalyani  Bhawar','PAID',8,NULL,'MEMBERSHIP');
/*!40000 ALTER TABLE `commissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact_messages`
--

DROP TABLE IF EXISTS `contact_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `message` text,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_messages`
--

LOCK TABLES `contact_messages` WRITE;
/*!40000 ALTER TABLE `contact_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `contact_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `franchise_leads`
--

DROP TABLE IF EXISTS `franchise_leads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `franchise_leads` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `budget` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `franchise_type` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `preferred_location` varchar(255) DEFAULT NULL,
  `business_type` varchar(255) DEFAULT NULL,
  `experience` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `commission_generated` bit(1) DEFAULT NULL,
  `final_franchise_amount` decimal(15,2) DEFAULT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `franchise_leads`
--

LOCK TABLES `franchise_leads` WRITE;
/*!40000 ALTER TABLE `franchise_leads` DISABLE KEYS */;
INSERT INTO `franchise_leads` VALUES (1,'₹10L - ₹20L','PUNE','2026-06-22 16:31:01.829875','kalyanibhawar3@gmail.com','Studio','9876543210','Kalyani Vilas Bhawar','Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune',NULL,NULL,NULL,'',NULL,'APPROVED',_binary '',1500000.00,'REF1496');
/*!40000 ALTER TABLE `franchise_leads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `home_banners`
--

DROP TABLE IF EXISTS `home_banners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_banners` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `display_order` int DEFAULT NULL,
  `image_url` varchar(255) NOT NULL,
  `link_url` varchar(255) DEFAULT NULL,
  `sub_title` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `home_banners`
--

LOCK TABLES `home_banners` WRITE;
/*!40000 ALTER TABLE `home_banners` DISABLE KEYS */;
/*!40000 ALTER TABLE `home_banners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bulk_discounts` double NOT NULL,
  `date` datetime(6) NOT NULL,
  `final_payable_amount` double NOT NULL,
  `invoice_number` varchar(50) NOT NULL,
  `order_number` varchar(50) NOT NULL,
  `product_discounts` double NOT NULL,
  `razorpay_amount_paid` double NOT NULL,
  `subtotal` double NOT NULL,
  `total_savings` double NOT NULL,
  `wallet_amount_used` double NOT NULL,
  `merchant_order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKl1x55mfsay7co0r3m9ynvipd5` (`invoice_number`),
  UNIQUE KEY `UK965ary9nbroujw1vxp2uxlt3` (`merchant_order_id`),
  CONSTRAINT `FKqw6i0wh2lurru2fhej4acdqb5` FOREIGN KEY (`merchant_order_id`) REFERENCES `merchant_orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leads`
--

DROP TABLE IF EXISTS `leads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leads` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `remarks` text,
  `status` varchar(255) NOT NULL,
  `agent_user_id` bigint DEFAULT NULL,
  `executive_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpy9tbjgo1o2wvtaxqtum1ucaa` (`agent_user_id`),
  CONSTRAINT `FKpy9tbjgo1o2wvtaxqtum1ucaa` FOREIGN KEY (`agent_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leads`
--

LOCK TABLES `leads` WRITE;
/*!40000 ALTER TABLE `leads` DISABLE KEYS */;
/*!40000 ALTER TABLE `leads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manual_payment_requests`
--

DROP TABLE IF EXISTS `manual_payment_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manual_payment_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `payment_purpose` varchar(255) DEFAULT NULL,
  `reference_id` varchar(255) DEFAULT NULL,
  `screenshot_path` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `utr_number` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `admin_remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa21xe1jlgfj4f5528j5lqupj8` (`user_id`),
  CONSTRAINT `FKa21xe1jlgfj4f5528j5lqupj8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manual_payment_requests`
--

LOCK TABLES `manual_payment_requests` WRITE;
/*!40000 ALTER TABLE `manual_payment_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `manual_payment_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_profiles`
--

DROP TABLE IF EXISTS `member_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `join_date` datetime(6) NOT NULL,
  `member_id` varchar(255) NOT NULL,
  `membership_type` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmjtvll0csj4h8vjxqgbeo1hcw` (`member_id`),
  UNIQUE KEY `UKiad47gmdf6bkeoq4xluvhs95g` (`uuid`),
  UNIQUE KEY `UK824ab3o0rnt46qxa83t9fiy51` (`user_id`),
  CONSTRAINT `FKju65j2qvdc95e6jqssqmvdti7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_profiles`
--

LOCK TABLES `member_profiles` WRITE;
/*!40000 ALTER TABLE `member_profiles` DISABLE KEYS */;
INSERT INTO `member_profiles` VALUES (1,'2026-06-22 16:31:42.179712','LLB-P-00001','Eva Pink Card','cebd22fe-cab0-4e2b-8b38-4d5fbaa640ab',2),(2,'2026-06-22 16:57:08.375405','LLB-G-00001','Eva Gold Card','cd3b6aed-04e0-4960-b8b0-9f1b02db417c',1);
/*!40000 ALTER TABLE `member_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_history`
--

DROP TABLE IF EXISTS `membership_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `plan_name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `status` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsm0g6hnrstu5briti90pxdtsq` (`user_id`),
  CONSTRAINT `FKsm0g6hnrstu5briti90pxdtsq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_history`
--

LOCK TABLES `membership_history` WRITE;
/*!40000 ALTER TABLE `membership_history` DISABLE KEYS */;
INSERT INTO `membership_history` VALUES (1,'2027-06-22 16:31:42.084762','pay_T4kAwtlNg6bKTM','Eva Pink Card',2999.00,'2026-06-22 16:31:42.084762','ACTIVE',2),(2,'2027-06-22 16:57:08.355308','pay_T4kbp1C5yDh0mQ','Eva Pink Card',2999.00,'2026-06-22 16:57:08.355308','EXPIRED',1),(3,'2027-06-22 16:58:00.664526','pay_T4kcjqS9758CSB','Eva Gold Card',9999.00,'2026-06-22 16:58:00.664526','ACTIVE',1);
/*!40000 ALTER TABLE `membership_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_purchases`
--

DROP TABLE IF EXISTS `membership_purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_purchases` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount_paid` double DEFAULT NULL,
  `purchase_date` datetime(6) DEFAULT NULL,
  `razorpay_order_id` varchar(255) DEFAULT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `membership_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjybu0kog9cr36br9ny793sss5` (`membership_id`),
  KEY `FKnj6n4f9owayr6woedspjtq9bn` (`user_id`),
  CONSTRAINT `FKjybu0kog9cr36br9ny793sss5` FOREIGN KEY (`membership_id`) REFERENCES `memberships` (`id`),
  CONSTRAINT `FKnj6n4f9owayr6woedspjtq9bn` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_purchases`
--

LOCK TABLES `membership_purchases` WRITE;
/*!40000 ALTER TABLE `membership_purchases` DISABLE KEYS */;
INSERT INTO `membership_purchases` VALUES (1,3538.8199999999997,'2026-06-22 16:31:21.097755','order_T4kArBrARUJxSZ','pay_T4kAwtlNg6bKTM','SUCCESS',4,2),(2,3538.8199999999997,'2026-06-22 16:56:47.219958','order_T4kbjCwugYQQTa','pay_T4kbp1C5yDh0mQ','SUCCESS',4,1),(3,8260,'2026-06-22 16:57:38.352813','order_T4kccsCAkwUhkG','pay_T4kcjqS9758CSB','SUCCESS',5,1);
/*!40000 ALTER TABLE `membership_purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_qrcodes`
--

DROP TABLE IF EXISTS `membership_qrcodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_qrcodes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `qr_url` varchar(255) NOT NULL,
  `user_membership_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK23m81x57b5pamonhttw1f03x` (`user_membership_id`),
  CONSTRAINT `FKh0scaqc71qip2rxaj4o91d7hw` FOREIGN KEY (`user_membership_id`) REFERENCES `user_memberships` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_qrcodes`
--

LOCK TABLES `membership_qrcodes` WRITE;
/*!40000 ALTER TABLE `membership_qrcodes` DISABLE KEYS */;
INSERT INTO `membership_qrcodes` VALUES (1,'2026-06-22 16:31:42.182675','/member/verify/cebd22fe-cab0-4e2b-8b38-4d5fbaa640ab',1),(2,'2026-06-22 16:57:08.378624','/member/verify/cd3b6aed-04e0-4960-b8b0-9f1b02db417c',2),(3,'2026-06-22 16:58:00.714643','/member/verify/c2abc0c7-5e2d-4e2a-97cb-cb5bb723d73c',3);
/*!40000 ALTER TABLE `membership_qrcodes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memberships`
--

DROP TABLE IF EXISTS `memberships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `memberships` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `benefits` text,
  `cashback_percent` double NOT NULL,
  `duration_days` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `welcome_credits` double NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `duration_months` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memberships`
--

LOCK TABLES `memberships` WRITE;
/*!40000 ALTER TABLE `memberships` DISABLE KEYS */;
INSERT INTO `memberships` VALUES (4,'5% Discount\r\nEarly Access to Sales & Events\r\nExclusive Birthday Offers\r\nReward Points Earning\r\nStandard Member Support',0.05,365,'Eva Pink Card',2999,300,1,1),(5,'15% Discount\nFree Delivery on All Orders\nVIP Launch Access\nDouble Reward Points\nEvent Invitations',0.15,365,'Eva Gold Card',9999,1000,1,1),(6,'25% Discount\nConcierge Support\nLuxury Gifts on Signup\nTriple Reward Points\nVIP Lounge Access',0.25,365,'Eva Black Card',24999,2500,1,1);
/*!40000 ALTER TABLE `memberships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_applications`
--

DROP TABLE IF EXISTS `merchant_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aadhar_document_url` varchar(255) NOT NULL,
  `aadhar_number` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `business_type` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email_address` varchar(255) NOT NULL,
  `gst_document_url` varchar(255) DEFAULT NULL,
  `gst_number` varchar(255) DEFAULT NULL,
  `mobile_number` varchar(255) NOT NULL,
  `owner_name` varchar(255) NOT NULL,
  `pan_document_url` varchar(255) NOT NULL,
  `pan_number` varchar(255) NOT NULL,
  `state` varchar(255) NOT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdwsh342l02isqf1u5q9rgst2b` (`user_id`),
  CONSTRAINT `FKdwsh342l02isqf1u5q9rgst2b` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_applications`
--

LOCK TABLES `merchant_applications` WRITE;
/*!40000 ALTER TABLE `merchant_applications` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_customers`
--

DROP TABLE IF EXISTS `merchant_customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_customers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `merchant_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6jv8tosa8c1d50ijqju4id62r` (`merchant_user_id`),
  CONSTRAINT `FK6jv8tosa8c1d50ijqju4id62r` FOREIGN KEY (`merchant_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_customers`
--

LOCK TABLES `merchant_customers` WRITE;
/*!40000 ALTER TABLE `merchant_customers` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_inventories`
--

DROP TABLE IF EXISTS `merchant_inventories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_inventories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `merchant_user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK23g9o4kuuttrrdn428d8d0xnt` (`merchant_user_id`,`product_id`),
  KEY `FKnj80x7vgnlqr6xd8t9jpfovkk` (`product_id`),
  CONSTRAINT `FKal3okxvblhp0kjpid2ipgifkn` FOREIGN KEY (`merchant_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKnj80x7vgnlqr6xd8t9jpfovkk` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_inventories`
--

LOCK TABLES `merchant_inventories` WRITE;
/*!40000 ALTER TABLE `merchant_inventories` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_inventories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_order_items`
--

DROP TABLE IF EXISTS `merchant_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bulk_discount_percent` double NOT NULL,
  `final_price` double NOT NULL,
  `merchant_discount_percent` double NOT NULL,
  `merchant_price` double NOT NULL,
  `mrp` double NOT NULL,
  `quantity` int NOT NULL,
  `merchant_order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKilhcbd2f4bvnwqlrvis7ln88b` (`merchant_order_id`),
  KEY `FKxoosrywlx60wlm6i88kdofjn` (`product_id`),
  CONSTRAINT `FKilhcbd2f4bvnwqlrvis7ln88b` FOREIGN KEY (`merchant_order_id`) REFERENCES `merchant_orders` (`id`),
  CONSTRAINT `FKxoosrywlx60wlm6i88kdofjn` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_order_items`
--

LOCK TABLES `merchant_order_items` WRITE;
/*!40000 ALTER TABLE `merchant_order_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_orders`
--

DROP TABLE IF EXISTS `merchant_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bulk_discounts` double NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `final_amount` double NOT NULL,
  `order_status` varchar(20) NOT NULL,
  `product_discounts` double NOT NULL,
  `razorpay_amount_paid` double NOT NULL,
  `razorpay_order_id` varchar(255) DEFAULT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  `razorpay_signature` varchar(255) DEFAULT NULL,
  `subtotal` double NOT NULL,
  `total_savings` double NOT NULL,
  `wallet_amount_used` double NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn9xkqknkx6ru1yo5s9sfyioe3` (`user_id`),
  CONSTRAINT `FKn9xkqknkx6ru1yo5s9sfyioe3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_orders`
--

LOCK TABLES `merchant_orders` WRITE;
/*!40000 ALTER TABLE `merchant_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_profiles`
--

DROP TABLE IF EXISTS `merchant_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `business_type` varchar(255) DEFAULT NULL,
  `city` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `gst_number` varchar(255) DEFAULT NULL,
  `merchant_id` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `offline_selling` bit(1) DEFAULT NULL,
  `online_selling` bit(1) DEFAULT NULL,
  `owner_name` varchar(255) NOT NULL,
  `pan_number` varchar(255) DEFAULT NULL,
  `state` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  `aadhar_document_url` varchar(255) DEFAULT NULL,
  `aadhar_number` varchar(255) DEFAULT NULL,
  `gst_document_url` varchar(255) DEFAULT NULL,
  `pan_document_url` varchar(255) DEFAULT NULL,
  `bank_account_holder_name` varchar(255) DEFAULT NULL,
  `bank_account_number` varchar(255) DEFAULT NULL,
  `ifsc_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr77afogh8o46a4e1wwfx00vfk` (`merchant_id`),
  UNIQUE KEY `UKp8boa8pxwwkh88crtbxna5xbe` (`user_id`),
  CONSTRAINT `FKnhhri67wse4kb68qv43blyokr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_profiles`
--

LOCK TABLES `merchant_profiles` WRITE;
/*!40000 ALTER TABLE `merchant_profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchants`
--

DROP TABLE IF EXISTS `merchants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchants`
--

LOCK TABLES `merchants` WRITE;
/*!40000 ALTER TABLE `merchants` DISABLE KEYS */;
INSERT INTO `merchants` VALUES (5,'+91 99999 88888','2026-06-18 16:33:09.097211','Aundh, Pune','L.L. Beauty Flagship Spa','ACTIVE'),(6,'+91 98888 77777','2026-06-18 16:33:09.107205','Koregaon Park, Pune','L.L. Beauty Lounge','ACTIVE');
/*!40000 ALTER TABLE `merchants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `message` text NOT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `type` enum('ERROR','INFO','SUCCESS','WARNING') NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notif_user` (`user_id`),
  KEY `idx_notif_read` (`is_read`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'2026-06-17 05:06:15.948098',_binary '\0','Congratulations! Your Executive application has been approved. You can now access your executive dashboard.',NULL,'Application Approved','SUCCESS',3);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nxl_wallet_transactions`
--

DROP TABLE IF EXISTS `nxl_wallet_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nxl_wallet_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(12,2) NOT NULL,
  `balance_after` decimal(12,2) NOT NULL,
  `date_time` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(255) NOT NULL,
  `type` varchar(10) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKl7dm5wrgkj60m428lyw02hmh9` (`transaction_id`),
  KEY `FKgnjxfdftmhslc82h8bd9g3gg4` (`user_id`),
  CONSTRAINT `FKgnjxfdftmhslc82h8bd9g3gg4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nxl_wallet_transactions`
--

LOCK TABLES `nxl_wallet_transactions` WRITE;
/*!40000 ALTER TABLE `nxl_wallet_transactions` DISABLE KEYS */;
INSERT INTO `nxl_wallet_transactions` VALUES (1,1000.00,1000.00,'2026-06-16 06:40:55.587626','Welcome credit for Eva Gold Card activation','NXL-TXN-1781592055585-595','CREDIT',2),(2,2500.00,5100.00,'2026-06-16 07:01:25.974268','Welcome credit for Eva Black Card activation','NXL-TXN-1781593285974-658','CREDIT',1),(3,1106.03,6206.03,'2026-06-16 07:03:12.330779','Cashback for Order #4 (Eva Black Card)','NXL-TXN-1781593392330-710','CREDIT',1),(4,2322.46,8528.49,'2026-06-16 07:12:39.950352','Cashback for Order #7 (Eva Black Card)','NXL-TXN-1781593959950-882','CREDIT',1);
/*!40000 ALTER TABLE `nxl_wallet_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nxl_wallets`
--

DROP TABLE IF EXISTS `nxl_wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nxl_wallets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(12,2) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjkm5kadbwo8rrx9htnfcgrqiq` (`user_id`),
  CONSTRAINT `FKk7ltkgqfibvqkd9s3cldvcpu5` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nxl_wallets`
--

LOCK TABLES `nxl_wallets` WRITE;
/*!40000 ALTER TABLE `nxl_wallets` DISABLE KEYS */;
INSERT INTO `nxl_wallets` VALUES (1,8528.49,1),(2,1000.00,2),(3,0.00,3);
/*!40000 ALTER TABLE `nxl_wallets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price_at_purchase` double DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,3499,1,1,4);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `razorpay_order_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `order_status` varchar(255) DEFAULT NULL,
  `order_type` varchar(255) DEFAULT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2026-06-22 16:28:39.384517','pay_T4k88ziwA44X2w','SUCCESS',4128.82,2,NULL,'2026-06-22 16:38:10.881543','DELIVERED',NULL,'REF1496');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otps`
--

DROP TABLE IF EXISTS `otps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otps` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `used` bit(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otps`
--

LOCK TABLES `otps` WRITE;
/*!40000 ALTER TABLE `otps` DISABLE KEYS */;
INSERT INTO `otps` VALUES (22,'186338','2026-06-19 06:30:27.450572',NULL,_binary '','nehereprachi1@gmail.com'),(23,'200256','2026-06-19 06:41:29.125411',NULL,_binary '','nehareprachi2@gmail.com'),(41,'368272','2026-06-19 16:08:58.571585',NULL,_binary '','neharesapana2@gmail.com'),(86,'455760','2026-06-22 16:32:44.520946',NULL,_binary '','kalyanibhawar3@gmail.com'),(88,'413110','2026-06-22 17:00:07.809557',NULL,_binary '','kalyanibhawar465@gmail.com');
/*!40000 ALTER TABLE `otps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `razorpay_order_id` varchar(255) DEFAULT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  `razorpay_signature` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `reference_id` varchar(255) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `payment_for` varchar(255) DEFAULT NULL,
  `total_amount_paid` double DEFAULT NULL,
  `wallet_deduction_amount` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj94hgy9v5fw1munb90tar2eje` (`user_id`),
  CONSTRAINT `FKj94hgy9v5fw1munb90tar2eje` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,2948.82,'2026-06-15 07:00:21.153019','RAZORPAY',NULL,'order_T1ohpSPnzluhMW',NULL,NULL,'CREATED',1,'1','INR','PRODUCT',NULL,0),(2,2948.82,'2026-06-15 07:00:21.153019','RAZORPAY',NULL,'order_T1ohpSXO5cwqIL','pay_T1ohzzWezWhhJN','81ade6efff80940c5d5368add6074384db13f48b51a5660ac81c9c1f894ddc8a','REFUNDED_WALLET',1,'2','INR','PRODUCT',NULL,0),(3,3538.8199999999997,'2026-06-15 07:03:49.312283','RAZORPAY',NULL,'order_T1olUiX2cZy8c9','pay_T1olaXmAxQxf4X','e52550ea769caf3b2fdf2231701853482f689cd5497996fbb7178bc0b0a7d6b7','SUCCESS',1,'1','INR','MEMBERSHIP',NULL,0),(4,3538.8199999999997,'2026-06-15 07:04:23.786877','RAZORPAY',NULL,'order_T1om6N2dwpo3jZ','pay_T1onAqSgAKpsih','43b334b1546c823625c025c39bbe35def894159ea4c33e001b992773d344ff72','SUCCESS',1,'2','INR','MEMBERSHIP',NULL,0),(5,8260,'2026-06-15 07:08:10.085262','RAZORPAY',NULL,'order_T1oq5OwvbAUjm3','pay_T1oqBtpQPeFQjj','bcc89252679dd7b80f170898bb281a122154bfe5f62d164a34781d683675eda2','SUCCESS',1,'3','INR','MEMBERSHIP',NULL,0),(6,11798.82,'2026-06-15 07:08:51.837313','RAZORPAY',NULL,'order_T1oqoypy6sACtC','pay_T1oqwGvcZW8Cik','3eb4b5a4ec039185eb1e904514a77dfdc40bff31fccf7e659d417bfc0da111cb','SUCCESS',1,'4','INR','MEMBERSHIP',NULL,0),(7,50000,'2026-06-15 07:16:23.613742','RAZORPAY',NULL,'order_T1oym4dWJAx9Ke','pay_T1oytci2hpIOkP','40213ddaf61b55fc2d9f1febf8c164b88ecbc16cc7b555f15be68ee4480bcc50','SUCCESS',1,'merch_dep_1781507783453','INR','MERCHANT_DEPOSIT',NULL,0),(8,50000,'2026-06-15 14:04:54.939506','RAZORPAY',NULL,'order_T1vwK3uBtknKEB','pay_T1vwkgtcFe9isy','ebdd1eba052e3eda743e1c932eef3982d01559d3f11c07a01fea67a614dd1b45','SUCCESS',2,'merch_dep_1781532294597','INR','MERCHANT_DEPOSIT',NULL,0),(9,11798.82,'2026-06-16 06:39:59.555067','RAZORPAY',NULL,'order_T2CtSItyy8NOiY','pay_T2CuA3BwVlRBqq','1e9749e64e94e1433fc14401b173c23903b5e718918c54a7d87755bcb47a90f1','SUCCESS',2,'5','INR','MEMBERSHIP',NULL,0),(10,17700,'2026-06-16 07:01:01.923389','RAZORPAY',NULL,'order_T2DFgH4WD6MuRS','pay_T2DFqKa6xJEtGf','754e3bf02d0fc0529e5e6e81640acb66e1e44ee938f341c76abb57835181758c','SUCCESS',1,'6','INR','MEMBERSHIP',NULL,0),(11,4424.115,'2026-06-16 07:02:42.052380','RAZORPAY',NULL,'order_T2DHRaJklHDA9K',NULL,NULL,'CREATED',1,'3','INR','PRODUCT',NULL,0),(12,4424.115,'2026-06-16 07:02:50.854908','RAZORPAY',NULL,'order_T2DHbC4XLhfv83','pay_T2DHi6H5Ys6Feh','d5f842f3eaf1e039dd805f463d7d1eabe768fb4b02e303fa1d56742273ffa001','SUCCESS',1,'4','INR','PRODUCT',NULL,0),(13,9289.845,'2026-06-16 07:11:31.597335','RAZORPAY',NULL,'order_T2DQlbdlBnJ1Hw',NULL,NULL,'CREATED',1,'5','INR','PRODUCT',NULL,0),(14,9289.845,'2026-06-16 07:11:31.745454','RAZORPAY',NULL,'order_T2DQlkwBGZXPop',NULL,NULL,'CREATED',1,'6','INR','PRODUCT',NULL,0),(15,9289.845,'2026-06-16 07:12:16.408099','RAZORPAY',NULL,'order_T2DRYVrbxzrnEI','pay_T2DRf6ESQ3k2gJ','07198480d1d7b1967b5284b49a4fa5a2d0ae99f0914ab8629db32206094dc13b','SUCCESS',1,'7','INR','PRODUCT',NULL,0),(16,10000,'2026-06-17 05:04:08.952730','RAZORPAY',NULL,'order_T2ZnLk1LIG99H5','pay_T2ZnaiynA5AlIZ','0a737f5e54f6ce8f53ca99bd83159f107986f93e055ed6eeca0f9a881ef73ee2','SUCCESS',3,'exe_start_1781672648142','INR','EXECUTIVE_STARTER',NULL,0),(17,5898.82,'2026-06-17 12:11:43.147660','RAZORPAY',NULL,'order_T2h4zrHxRcmXVz',NULL,NULL,'CREATED',3,'8','INR','PRODUCT',NULL,0),(18,5898.82,'2026-06-17 12:11:43.147660','RAZORPAY',NULL,'order_T2h4zr4ZKfD35o','pay_T2h5I30Wczpg4I','57959eb90efec6253bedd6035ee1bf4fd1726eac33647cf4c0a5b814333ec282','SUCCESS',3,'9','INR','PRODUCT',NULL,0),(19,11798.82,'2026-06-17 12:18:00.813619','RAZORPAY',NULL,'order_T2hBe74WJ0AGzt',NULL,NULL,'CREATED',3,'7','INR','MEMBERSHIP',NULL,0),(20,11798.82,'2026-06-17 12:18:00.993035','RAZORPAY',NULL,'order_T2hBeHEJSvFwz7','pay_T2hBoDsNWW2304','410ceb5ebdc181288d138a633fbada24beadb3b4b3e779069a226434f0023e25','SUCCESS',3,'8','INR','MEMBERSHIP',NULL,0),(21,3538.8199999999997,'2026-06-17 12:18:30.950473','RAZORPAY',NULL,'order_T2hCB0HkKEPFZE',NULL,NULL,'CREATED',3,'9','INR','MEMBERSHIP',NULL,0),(22,29498.82,'2026-06-17 12:26:55.381721','RAZORPAY',NULL,'order_T2hL3dEm7sLh3z',NULL,NULL,'CREATED',1,'10','INR','MEMBERSHIP',NULL,0),(23,2948.82,'2026-06-19 11:23:42.849124','RAZORPAY',NULL,'order_T3TKWXYvTdcX1i','pay_T3TKk9gRSNtDbj','30cbe17de17303488fb4af15512f7f900d9f75ad938b45c9daf7dc320c26f905','SUCCESS',14,'10','INR','PRODUCT',NULL,0),(24,590,'2026-06-19 16:04:50.558931','RAZORPAY',NULL,'order_T3Y7Uepgikz8Ys',NULL,NULL,'CREATED',15,'12','INR','PRODUCT',NULL,0),(25,590,'2026-06-19 16:04:50.786272','RAZORPAY',NULL,'order_T3Y7UzDR8ZKeyf',NULL,NULL,'CREATED',15,'11','INR','PRODUCT',NULL,0),(26,590,'2026-06-19 16:05:07.138836','RAZORPAY',NULL,'order_T3Y7mpHGyTFdx2',NULL,NULL,'CREATED',15,'13','INR','PRODUCT',NULL,0),(27,590,'2026-06-19 16:05:07.373346','RAZORPAY',NULL,'order_T3Y7n6m4DoFrrL','pay_T3Y85fKpSwRQIu','4fef3d2c0226313e2d607adfcea5e54cfad91a5082fb915475182a69c35783e1','SUCCESS',15,'14','INR','PRODUCT',NULL,0),(28,826,'2026-06-19 17:16:41.741331','RAZORPAY',NULL,'order_T3ZLOYXcv3DiCa',NULL,NULL,'CREATED',14,'15','INR','PRODUCT',NULL,0),(29,3538.8199999999997,'2026-06-20 02:30:15.870795','RAZORPAY',NULL,'order_T3im8v9w70tZuH','pay_T3imNSBjnVTUUZ','a5d475534238500f889141780b6694658108eb624ee2db8ffed0ce1c9fb42bf2','SUCCESS',12,'11','INR','MEMBERSHIP',NULL,0),(30,100,'2026-06-21 08:24:45.020096','RAZORPAY',NULL,'order_T4DLhzXCq5V4P9',NULL,NULL,'CREATED',1,'14','INR','SALON_DEPOSIT',NULL,0),(31,2948.82,'2026-06-21 09:00:48.801189','RAZORPAY',NULL,'order_T4DxnkEX3pQImp',NULL,NULL,'CREATED',2,'2','INR','PRODUCT',NULL,0),(32,2948.82,'2026-06-21 09:00:49.394580','RAZORPAY',NULL,'order_T4DxoWlV2eqNvA','pay_T4Dy3DJH5xU1PJ','1c84599bd0b2bceec7eda9b8d5af1efb698620d049167065921bc90f51423f0d','SUCCESS',2,'1','INR','PRODUCT',NULL,0),(33,100,'2026-06-21 09:01:47.102411','RAZORPAY',NULL,'order_T4DypWAZOmfZcH',NULL,NULL,'CREATED',2,'1','INR','SALON_DEPOSIT',NULL,0),(34,100,'2026-06-21 09:01:56.809063','RAZORPAY',NULL,'order_T4Dz079RQ2izRD',NULL,NULL,'CREATED',2,'1','INR','SALON_DEPOSIT',NULL,0),(35,100,'2026-06-21 09:02:15.907545','RAZORPAY',NULL,'order_T4DzKsahp0uZ6L','pay_T4Dzczk9jHNmzw','8c46963c1dd672c584f9d91b85998a8c57b1c243fff1ae1459cc02b07c9b76d6','SUCCESS',2,'1','INR','SALON_DEPOSIT',NULL,0),(36,100,'2026-06-21 10:04:22.481439','RAZORPAY',NULL,'order_T4F2wtYPJHnWH9','pay_T4F3OztF6Fo8Q1','a520de17df69813a41ee45803be0c0efb04cc100fd0b51c35bbad85289cf4e7d','SUCCESS',1,'2','INR','SALON_DEPOSIT',NULL,0),(37,100,'2026-06-21 10:10:11.963261','RAZORPAY',NULL,'order_T4F96UHHtvk15A','pay_T4FBKjeDTExSsK','8b1efb45b6ef8602e4a5fa2d7ba817977dae8be96d9b0bb98b47d099cc86dbb6','SUCCESS',1,'3','INR','SALON_DEPOSIT',NULL,0),(38,1180,'2026-06-21 10:13:11.785597','RAZORPAY',NULL,'order_T4FCGlmqdU4v2d',NULL,NULL,'CREATED',1,'3','INR','PRODUCT',NULL,0),(39,3538.8199999999997,'2026-06-21 18:23:24.521322','RAZORPAY',NULL,'order_T4NY6QnbVIlFCe',NULL,NULL,'CREATED',1,'12','INR','MEMBERSHIP',NULL,0),(40,100,'2026-06-22 08:43:01.907923','RAZORPAY',NULL,'order_T4cC9MBnIZNrX6',NULL,NULL,'CREATED',2,'10','INR','SALON_DEPOSIT',NULL,0),(41,100,'2026-06-22 08:46:02.092512','RAZORPAY',NULL,'order_T4cFK4dkGQBryh',NULL,NULL,'CREATED',2,'10','INR','SALON_DEPOSIT',NULL,0),(42,100,'2026-06-22 08:47:36.541356','RAZORPAY',NULL,'order_T4cGzALxSgxNYs',NULL,NULL,'CREATED',2,'11','INR','SALON_DEPOSIT',NULL,0),(43,100,'2026-06-22 08:47:43.840475','RAZORPAY',NULL,'order_T4cH7870frQK6G',NULL,NULL,'CREATED',2,'11','INR','SALON_DEPOSIT',NULL,0),(44,100,'2026-06-22 08:47:48.677266','RAZORPAY',NULL,'order_T4cHCPjwq5S6JZ',NULL,NULL,'CREATED',2,'11','INR','SALON_DEPOSIT',NULL,0),(45,100,'2026-06-22 08:50:45.652388','RAZORPAY',NULL,'order_T4cKJaN2fZXPpG',NULL,NULL,'CREATED',1,'12','INR','SALON_DEPOSIT',NULL,0),(46,100,'2026-06-22 08:51:02.739195','RAZORPAY',NULL,'order_T4cKc75jGRbONa',NULL,NULL,'CREATED',1,'12','INR','SALON_DEPOSIT',NULL,0),(47,100,'2026-06-22 08:52:00.341552','RAZORPAY',NULL,'order_T4cLd6qCeEk2S6',NULL,NULL,'CREATED',1,'12','INR','SALON_DEPOSIT',NULL,0),(48,100,'2026-06-22 08:52:10.443290','RAZORPAY',NULL,'order_T4cLo8tZqGjx5W',NULL,NULL,'CREATED',1,'12','INR','SALON_DEPOSIT',NULL,0),(49,5898.82,'2026-06-22 08:54:35.726573','RAZORPAY',NULL,'order_T4cOMjGejhWKHK',NULL,NULL,'CREATED',1,'4','INR','PRODUCT',NULL,0),(50,100,'2026-06-22 10:29:37.495444','RAZORPAY',NULL,'order_T4e0kEJCiPMPWg',NULL,NULL,'CREATED',1,'13','INR','SALON_DEPOSIT',NULL,0),(51,100,'2026-06-22 10:31:17.210089','RAZORPAY',NULL,'order_T4e2V7mjel1NFy',NULL,NULL,'CREATED',1,'13','INR','SALON_DEPOSIT',NULL,0),(52,100,'2026-06-22 10:37:41.703740','RAZORPAY',NULL,'order_T4e9GotLA6Tr2Z',NULL,NULL,'CREATED',1,'13','INR','SALON_DEPOSIT',NULL,0),(53,100,'2026-06-22 10:40:13.011654','RAZORPAY',NULL,'order_T4eBvyHhVhizpL',NULL,NULL,'CREATED',1,'13','INR','SALON_DEPOSIT',NULL,0),(54,100,'2026-06-22 10:40:41.020224','RAZORPAY',NULL,'order_T4eCQXl8ihjrgp',NULL,NULL,'CREATED',1,'13','INR','SALON_DEPOSIT',NULL,0),(55,100,'2026-06-22 10:42:32.947008','RAZORPAY',NULL,'order_T4eEOijckOnVSd',NULL,NULL,'CREATED',1,'13','INR','SALON_DEPOSIT',NULL,0),(56,100,'2026-06-22 10:43:45.130240','RAZORPAY',NULL,'order_T4eFfSQPoeE89q',NULL,NULL,'CREATED',1,'13','INR','SALON_DEPOSIT',NULL,0),(57,100,'2026-06-22 10:46:22.226651','RAZORPAY',NULL,'order_T4eIQzHyNNTvsI','pay_T4eInM5oT5uzD1','938873e1a99123730139670c8cc3f8494b951c95a2ade27304c4dadda134f710','SUCCESS',1,'13','INR','SALON_DEPOSIT',NULL,0),(58,3538.8199999999997,'2026-06-22 10:52:14.156360','RAZORPAY',NULL,'order_T4eOd7CRd4CE0P',NULL,NULL,'CREATED',2,'13','INR','MEMBERSHIP',NULL,0),(59,3538.8199999999997,'2026-06-22 10:52:48.502295','RAZORPAY',NULL,'order_T4ePEcxWUwUX45',NULL,NULL,'CREATED',2,'14','INR','MEMBERSHIP',NULL,0),(60,3538.8199999999997,'2026-06-22 11:36:13.429103','RAZORPAY',NULL,'order_T4f960QvkWo4bs',NULL,NULL,'CREATED',2,'15','INR','MEMBERSHIP',NULL,0),(61,3538.8199999999997,'2026-06-22 12:16:10.670538','RAZORPAY',NULL,'order_T4fpIezSPEHcwY','pay_T4fpSwZWtjfUmz','5fb1be1ba501c3a36c4ae3dcc05ff19c7689732c8ad76a86c274a0d331c17427','SUCCESS',2,'16','INR','MEMBERSHIP',NULL,0),(62,3538.8199999999997,'2026-06-22 12:39:35.469835','RAZORPAY',NULL,'order_T4gE273uqqjfvq','pay_T4gEAq7holAX1a','764f3171d594f57e4cc713ed9f4070c0ddd976a8bcc9d177e2831a5e7e90867b','SUCCESS',1,'17','INR','MEMBERSHIP',NULL,0),(63,8260,'2026-06-22 12:44:04.966699','RAZORPAY',NULL,'order_T4gImFgNTXPFOJ','pay_T4gIv1vkAv5N0h','039d4995d6b76172afa765450f22e93d52e8f272fe82b188a488413d61292152','SUCCESS',1,'18','INR','MEMBERSHIP',NULL,0),(64,4128.82,'2026-06-22 16:28:39.801744','RAZORPAY',NULL,'order_T4k80zCVXDChos','pay_T4k88ziwA44X2w','3f3a4e83b4dbe0ae9d660ace50e9840f2af9114caf4045d1bdec32a328582538','SUCCESS',2,'1','INR','PRODUCT',NULL,0),(65,100,'2026-06-22 16:29:42.857476','RAZORPAY',NULL,'order_T4k97K9pxzxsWC','pay_T4k9CrKZvgXxgB','2261b905129a4541f3e8647a3dfc5c034485fcf07438af2f44902c16da8e2689','SUCCESS',2,'1','INR','SALON_DEPOSIT',NULL,0),(66,3538.8199999999997,'2026-06-22 16:31:21.202589','RAZORPAY',NULL,'order_T4kArBrARUJxSZ','pay_T4kAwtlNg6bKTM','272e15aa17d1677a2253f3bb939b57dfead34ded4137634cbe111004aa5fae2a','SUCCESS',2,'1','INR','MEMBERSHIP',NULL,0),(67,3538.8199999999997,'2026-06-22 16:56:47.493407','RAZORPAY',NULL,'order_T4kbjCwugYQQTa','pay_T4kbp1C5yDh0mQ','dad3a73957fcee478eb4d483833ffb836023c8192dd76233ab5bf0e75251c4af','SUCCESS',1,'2','INR','MEMBERSHIP',NULL,0),(68,8260,'2026-06-22 16:57:38.499191','RAZORPAY',NULL,'order_T4kccsCAkwUhkG','pay_T4kcjqS9758CSB','6011cc06e953e3c7a4250a79c4f31dc1ce0515808ca86c438e901141a1329157','SUCCESS',1,'3','INR','MEMBERSHIP',NULL,0);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_reviews`
--

DROP TABLE IF EXISTS `product_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `review_text` text,
  `status` varchar(255) NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK35kxxqe2g9r4mww80w9e3tnw9` (`product_id`),
  KEY `FK58i39bhws2hss3tbcvdmrm60f` (`user_id`),
  CONSTRAINT `FK35kxxqe2g9r4mww80w9e3tnw9` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK58i39bhws2hss3tbcvdmrm60f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_reviews`
--

LOCK TABLES `product_reviews` WRITE;
/*!40000 ALTER TABLE `product_reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `wholesale_price` double DEFAULT NULL,
  `merchant_discount` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Skincare','2026-05-23 05:31:29.422435','Advanced anti-aging serum with pure rose extract and gold particles.','/images/skincare.png','Rose Gold Elixir',2499,100,'ACTIVE',2124.15,15),(2,'Perfumes','2026-05-23 05:31:29.442728','Exclusive oriental fragrance with deep oud, amber and musk notes.','/images/perfume.png','Oud Majestic',4999,50,'ACTIVE',4249.15,15),(3,'Haircare','2026-05-23 05:31:29.450627','Salon-grade keratin formula for silky, frizz-free hair every day.','/images/haircare.png','Keratin Pro Shampoo',1299,150,'ACTIVE',1169.1,10),(4,'Spa / Detox','2026-05-23 05:31:29.459188','Complete spa kit with essential oils, detox mask, and aromatherapy candles.','/images/spa.png','Luxury Detox Kit',3499,30,NULL,NULL,0),(7,'Skincare','2026-05-25 07:19:13.429453','Create a smooth, hydrated makeup base with our lightweight primer that minimizes pores, controls oil, and keeps makeup fresh all day.','/uploads/products/1779693553389_images.jfif','Primer',1000,5,'ACTIVE',NULL,0),(10,'Perfumes','2026-06-18 06:51:29.950288','Best perfumes','/uploads/products/1781765489934_gettyimages-637623730-612x612.jpg','Skinn by Titan',500,60,'ACTIVE',450,10),(11,'Haircare','2026-06-19 05:13:35.364463','Ayurvedic herbal oils are therapeutic blends of pure plant base oils (such as sesame or coconut) slowly infused with plant extracts, roots, and leaves. ','/uploads/products/1781846015339_Ayurvedic_Herb_Oil.png','Ayurvedik Herb Oil',500,50,'ACTIVE',450,10),(12,'Spa','2026-06-19 05:24:36.704608','Nivea lotions are dermatologically tested moisturizers formulated to deeply hydrate, nourish, and protect the skin. ','/uploads/products/1781846676692_nivea-body-milk-body-lotion.png','Nivea ',700,30,'ACTIVE',595,15);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qr_codes`
--

DROP TABLE IF EXISTS `qr_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qr_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `qr_data` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `merchant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh5fv8mndmjgo3dhp89ukflnld` (`merchant_id`),
  CONSTRAINT `FKh5fv8mndmjgo3dhp89ukflnld` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qr_codes`
--

LOCK TABLES `qr_codes` WRITE;
/*!40000 ALTER TABLE `qr_codes` DISABLE KEYS */;
INSERT INTO `qr_codes` VALUES (1,'2026-06-18 16:33:09.118211','/wallet/redeem?merchantId=5','ACTIVE',5),(2,'2026-06-18 16:33:09.132664','/wallet/redeem?merchantId=6','ACTIVE',6);
/*!40000 ALTER TABLE `qr_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reward_points`
--

DROP TABLE IF EXISTS `reward_points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reward_points` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `available_points` int NOT NULL,
  `redeemed_points` int NOT NULL,
  `total_points` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK5402qxjmo5rx73gqmovh0p3es` (`user_id`),
  CONSTRAINT `FKovyxai0u4f1lfi8brc9kovumt` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reward_points`
--

LOCK TABLES `reward_points` WRITE;
/*!40000 ALTER TABLE `reward_points` DISABLE KEYS */;
INSERT INTO `reward_points` VALUES (4,0,0,0,12),(5,0,0,0,1),(6,0,0,0,2);
/*!40000 ALTER TABLE `reward_points` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reward_transactions`
--

DROP TABLE IF EXISTS `reward_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reward_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) NOT NULL,
  `points` int NOT NULL,
  `type` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2qgkqf0r6hmvgqq40t8daay6d` (`user_id`),
  CONSTRAINT `FK2qgkqf0r6hmvgqq40t8daay6d` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reward_transactions`
--

LOCK TABLES `reward_transactions` WRITE;
/*!40000 ALTER TABLE `reward_transactions` DISABLE KEYS */;
INSERT INTO `reward_transactions` VALUES (1,'2026-06-16 07:03:12.341805','Earned reward points on purchase of value ₹4424.12',132,'CREDIT',1),(2,'2026-06-16 07:12:39.961843','Earned reward points on purchase of value ₹9289.85',276,'CREDIT',1);
/*!40000 ALTER TABLE `reward_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salon_info`
--

DROP TABLE IF EXISTS `salon_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salon_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` text,
  `contact_email` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `description` text,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tagline` varchar(255) DEFAULT NULL,
  `timings` varchar(255) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salon_info`
--

LOCK TABLES `salon_info` WRITE;
/*!40000 ALTER TABLE `salon_info` DISABLE KEYS */;
INSERT INTO `salon_info` VALUES (1,'123, Beauty Lane, Near City Mall, Pune, Maharashtra - 411001','salon@beauty.com','+91 98765 43210','Our premium studio in Pune offers cutting-edge hair styling, skin treatments, and customized wellness therapies in a deeply relaxing luxury environment.','/uploads/salon/1779643850920_bridal.png','EVA Beauty Salon','Main Flagship Branch','Mon - Sat: 07:00 AM - 10:00 PM (Sunday Closed)',0);
/*!40000 ALTER TABLE `salon_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salon_services`
--

DROP TABLE IF EXISTS `salon_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salon_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` text,
  `duration_minutes` int DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salon_services`
--

LOCK TABLES `salon_services` WRITE;
/*!40000 ALTER TABLE `salon_services` DISABLE KEYS */;
INSERT INTO `salon_services` VALUES (1,'Professional cut, shampoo, conditioning and blow-dry styling.',45,'/images/haircare.png','Hair Styling & Cut',899,1,NULL),(2,'Deep nourishment, repair treatment, and relaxing scalp massage.',60,'/images/haircare.png','Luxury Hair Spa',1499,1,NULL),(3,'Premium exfoliating facial with botanical extracts for instant radiance.',50,'/images/skincare.png','Gold Glow Facial',1999,1,NULL),(4,'Elite luxury bridal makeover including saree draping, hair, and makeup.',180,'/images/spa.png','Bridal Makeover',9999,1,NULL),(5,'Premium global hair coloring and highlights.',120,'/images/haircare.png','Hair Color',2999,1,NULL),(9,'vvsgytdtrse',30,NULL,'haircut',600,1,NULL);
/*!40000 ALTER TABLE `salon_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_applications`
--

DROP TABLE IF EXISTS `store_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `business_name` varchar(255) NOT NULL,
  `contact_email` varchar(255) NOT NULL,
  `contact_phone` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `details` text,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `application_type` enum('AGENT','MERCHANT') NOT NULL,
  `user_id` bigint NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `aadhar_document_url` varchar(255) DEFAULT NULL,
  `aadhar_number` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `bank_account_holder_name` varchar(255) DEFAULT NULL,
  `bank_account_number` varchar(255) DEFAULT NULL,
  `business_type` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `gst_document_url` varchar(255) DEFAULT NULL,
  `gst_number` varchar(255) DEFAULT NULL,
  `ifsc_code` varchar(255) DEFAULT NULL,
  `owner_name` varchar(255) DEFAULT NULL,
  `pan_document_url` varchar(255) DEFAULT NULL,
  `pan_number` varchar(255) DEFAULT NULL,
  `shop_photo_url` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi9806p8j67a488cshvt8islay` (`user_id`),
  CONSTRAINT `FKi9806p8j67a488cshvt8islay` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_applications`
--

LOCK TABLES `store_applications` WRITE;
/*!40000 ALTER TABLE `store_applications` DISABLE KEYS */;
INSERT INTO `store_applications` VALUES (1,'Kalyani Vilas Bhawar','kalyanibhawar465@gmail.com','9172051078','2026-06-22 16:13:39.402851','Address: Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune\nCity: PUNE\nState: Maharashtra\nOccupation: seller\nExperience: 1\nReferral Code: \nRegistration Type: FREE','APPROVED','AGENT',1,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `store_applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_credit_transactions`
--

DROP TABLE IF EXISTS `store_credit_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_credit_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `transaction_type` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK11mhd2v97h14lxpd0t4f4qj2t` (`user_id`),
  CONSTRAINT `FK11mhd2v97h14lxpd0t4f4qj2t` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_credit_transactions`
--

LOCK TABLES `store_credit_transactions` WRITE;
/*!40000 ALTER TABLE `store_credit_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_credit_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_credits`
--

DROP TABLE IF EXISTS `store_credits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_credits` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(10,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi7y2o8hvho6rjc44noi1buamm` (`user_id`),
  CONSTRAINT `FK6fbwphggnesu5fm0wa32t0ys9` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_credits`
--

LOCK TABLES `store_credits` WRITE;
/*!40000 ALTER TABLE `store_credits` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_credits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_memberships`
--

DROP TABLE IF EXISTS `user_memberships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_memberships` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  `start_date` datetime(6) NOT NULL,
  `status` varchar(255) NOT NULL,
  `membership_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  `member_id` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9mdch3fjnr3pcayrfwe1i451u` (`uuid`),
  KEY `FKd578eqokdbymythx3ihdde1n` (`membership_id`),
  KEY `FK3aftj3ypdb19itnsapcxykedv` (`user_id`),
  CONSTRAINT `FK3aftj3ypdb19itnsapcxykedv` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKd578eqokdbymythx3ihdde1n` FOREIGN KEY (`membership_id`) REFERENCES `memberships` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_memberships`
--

LOCK TABLES `user_memberships` WRITE;
/*!40000 ALTER TABLE `user_memberships` DISABLE KEYS */;
INSERT INTO `user_memberships` VALUES (1,'2027-07-22 16:31:42.084762','pay_T4kAwtlNg6bKTM','2026-06-22 16:31:42.084762','ACTIVE',4,2,NULL,'REF1496','LLB-P-00001','cebd22fe-cab0-4e2b-8b38-4d5fbaa640ab'),(2,'2027-06-22 16:57:08.355308','pay_T4kbp1C5yDh0mQ','2026-06-22 16:57:08.355308','UPGRADED',4,1,NULL,NULL,'LLB-P-00002','cd3b6aed-04e0-4960-b8b0-9f1b02db417c'),(3,'2027-06-22 16:58:00.664526','pay_T4kcjqS9758CSB','2026-06-22 16:58:00.664526','ACTIVE',5,1,NULL,'REF1496','LLB-G-00001','c2abc0c7-5e2d-4e2a-97cb-cb5bb723d73c');
/*!40000 ALTER TABLE `user_memberships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `wallet_balance` decimal(38,2) DEFAULT NULL,
  `is_blocked` bit(1) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'USER',
  `active` bit(1) DEFAULT NULL,
  `agent_status` varchar(20) DEFAULT NULL,
  `membership_status` varchar(20) DEFAULT NULL,
  `merchant_status` varchar(20) DEFAULT NULL,
  `executive_status` varchar(20) DEFAULT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-06-22 16:12:48.167018','kalyanibhawar465@gmail.com','9898989898','Kalyani  Bhawar',NULL,1949.90,_binary '\0','USER',_binary '','ACTIVE','NOT_APPLIED','NOT_APPLIED',NULL,''),(2,'2026-06-22 16:27:44.449181','kalyanibhawar3@gmail.com','9876543210','Kalyani Vilas Bhawar',NULL,300.00,_binary '\0','USER',_binary '','NOT_APPLIED','NOT_APPLIED','NOT_APPLIED',NULL,'REF1496');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_transactions`
--

DROP TABLE IF EXISTS `wallet_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `razorpay_order_id` varchar(255) DEFAULT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `payment_id` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrtsa3qtjhd0rn4xb92na03vd` (`user_id`),
  CONSTRAINT `FKrtsa3qtjhd0rn4xb92na03vd` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
INSERT INTO `wallet_transactions` VALUES (1,149.95,'2026-06-22 16:31:42.116740','Membership Referral Commission - Eva Pink Card','CREDIT',1,NULL,NULL,NULL,'MEMBERSHIP_COMMISSION',NULL,NULL,'SUCCESS'),(2,300.00,'2026-06-22 16:31:42.239321','Welcome credit for Eva Pink Card activation','CREDIT',2,NULL,NULL,NULL,'MEMBERSHIP_WELCOME',NULL,NULL,'SUCCESS'),(3,300.00,'2026-06-22 16:57:08.403774','Welcome credit for Eva Pink Card activation','CREDIT',1,NULL,NULL,NULL,'MEMBERSHIP_WELCOME',NULL,NULL,'SUCCESS'),(4,499.95,'2026-06-22 16:58:00.700221','Membership Referral Commission - Eva Gold Card','CREDIT',1,NULL,NULL,NULL,'MEMBERSHIP_COMMISSION',NULL,NULL,'SUCCESS'),(5,1000.00,'2026-06-22 16:58:00.755496','Welcome credit for Eva Gold Card activation','CREDIT',1,NULL,NULL,NULL,'MEMBERSHIP_WELCOME',NULL,NULL,'SUCCESS');
/*!40000 ALTER TABLE `wallet_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallets`
--

DROP TABLE IF EXISTS `wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(10,2) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsswfdl9fq40xlkove1y5kc7kv` (`user_id`),
  CONSTRAINT `FKc1foyisidw7wqqrkamafuwn4e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
INSERT INTO `wallets` VALUES (1,1949.90,1),(2,300.00,2);
/*!40000 ALTER TABLE `wallets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'llbeauty_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-23  0:05:04
