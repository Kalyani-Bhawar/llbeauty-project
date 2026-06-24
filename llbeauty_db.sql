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
) ENGINE=InnoDB AUTO_INCREMENT=294 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (293,'2026-06-24 15:59:12.063138','admin@llbeauty.com','L.L. Beauty Admin','$2a$10$cl2fYEFDZEFANI374/WFseo6RsZlBNwIHsp9eXSWGc251.HbM7hDa');
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
  `joining_date` datetime(6) DEFAULT NULL,
  `pan_number` varchar(255) DEFAULT NULL,
  `registration_type` varchar(255) DEFAULT NULL,
  `upi_id` varchar(255) DEFAULT NULL,
  `referred_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk6sn7budpmjnn8dposhoe5d0` (`agent_id`),
  UNIQUE KEY `UKmdpwfv3egvqq4q9oqx1wc9t88` (`user_id`),
  UNIQUE KEY `agent_id` (`agent_id`),
  UNIQUE KEY `UKsg482o9ihm083y4t5nn3ypk17` (`referral_code`),
  KEY `FK7etiw8ok7l6aow5cbo7uapevq` (`referred_by_id`),
  CONSTRAINT `FK7etiw8ok7l6aow5cbo7uapevq` FOREIGN KEY (`referred_by_id`) REFERENCES `agent_profiles` (`id`),
  CONSTRAINT `FK9w9b1ollcdraeirmj2i55kxrx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agent_profiles`
--

LOCK TABLES `agent_profiles` WRITE;
/*!40000 ALTER TABLE `agent_profiles` DISABLE KEYS */;
INSERT INTO `agent_profiles` VALUES (1,'2026-06-23 07:59:10.747924','LLB-EXE-1','REF1541','ACTIVE',1,'2026-06-23 07:59:10.747924','ABCDE1234F','REGISTRATION','kalyanibhawar@upi',NULL);
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
INSERT INTO `appointments` VALUES (1,'2026-06-26','2026-06-24 16:57:57.297545','Gold Glow Facial','COMPLETED','11:00 AM',1,'9172051078','Kalyani  Bhawar',100,'PAID','Gold Glow Facial','LL-SLOT-9023',NULL,NULL,'REF1541',1999,'order_T5XhKQVJm83WQ4','pay_T5XhSWtWm0IY93');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` VALUES (1,'ORDER_UPDATED','Order #2 status updated to DELIVERED','admin@llbeauty.com','2026-06-23 09:50:26.467216'),(2,'MERCHANT_ORDER_COMPLETED','Order #1 fully paid via NXL Credits.','kalyanibhawar465@gmail.com','2026-06-23 19:28:26.281894'),(3,'MERCHANT_ORDER_FAILED','Order #2 payment failed. NXL Credits restored: ₹29258.5','kalyanibhawar465@gmail.com','2026-06-24 05:56:50.279834'),(4,'MERCHANT_ORDER_COMPLETED','Order #3 confirmed. Wallet: ₹29258.5 + Razorpay: ₹64876.5','kalyanibhawar465@gmail.com','2026-06-24 05:57:28.320436'),(5,'MERCHANT_ORDER_FAILED','Order #4 payment failed. NXL Credits restored: ₹0.0','kalyanibhawar465@gmail.com','2026-06-24 16:26:41.293713'),(6,'ORDER_UPDATED','Order #1 status updated to DELIVERED','admin@llbeauty.com','2026-06-24 17:15:52.350844');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
INSERT INTO `commissions` VALUES (1,50.00,'2026-06-23 09:11:58.211072','User Registration Referral','PENDING',1,NULL,'USER_REGISTRATION'),(2,294.94,'2026-06-23 09:13:34.414800','Product Order #2','PENDING',1,NULL,'PRODUCT'),(3,50.00,'2026-06-24 11:57:31.713780','User Registration Referral','PENDING',1,NULL,'USER_REGISTRATION'),(4,206.44,'2026-06-24 16:57:11.858605','Product Order #5','PENDING',1,NULL,'PRODUCT'),(5,199.90,'2026-06-24 17:12:52.613284','Commission for Appointment #1','PENDING',1,1,'SALON_BOOKING'),(6,150000.00,'2026-06-24 17:14:31.196709','Franchise Referral Commission - Lead #1','PENDING',1,NULL,'FRANCHISE');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_messages`
--

LOCK TABLES `contact_messages` WRITE;
/*!40000 ALTER TABLE `contact_messages` DISABLE KEYS */;
INSERT INTO `contact_messages` VALUES (1,'2026-06-24 17:09:51.961971','kalyanibhawar465@gmail.com','Information about merchant.','Kalyani Vilas Bhawar','9172051078',NULL),(2,'2026-06-24 17:09:55.337457','kalyanibhawar465@gmail.com','Information about merchant.','Kalyani Vilas Bhawar','9172051078',NULL);
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
INSERT INTO `franchise_leads` VALUES (1,'₹10L - ₹20L','PUNE','2026-06-24 16:59:25.116244','kalyanibhawar465@gmail.com','Studio','9172051078','Kalyani  Bhawar','Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune',NULL,NULL,NULL,'',NULL,'APPROVED',_binary '',1500000.00,'REF1541');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
INSERT INTO `invoices` VALUES (1,'2026-06-23 19:28:26.181183',25741.5,'INV-MERCH-1782242906170-1','ORD-MERCH-1',4248.5,0,29990,4248.5,25741.5,1),(2,'2026-06-24 05:57:28.295635',94135,'INV-MERCH-1782280648293-3','ORD-MERCH-3',4498.5,64876.5,102470,8335,29258.5,3);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_profiles`
--

LOCK TABLES `member_profiles` WRITE;
/*!40000 ALTER TABLE `member_profiles` DISABLE KEYS */;
INSERT INTO `member_profiles` VALUES (1,'2026-06-24 17:00:37.835935','LLB-P-00001','Eva Pink Card','090850b8-71a8-4a48-b000-8c7236cc8f92',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_history`
--

LOCK TABLES `membership_history` WRITE;
/*!40000 ALTER TABLE `membership_history` DISABLE KEYS */;
INSERT INTO `membership_history` VALUES (1,'2027-06-24 17:00:37.727025','pay_T5XjknAPGpfNXw','Eva Pink Card',2999.00,'2026-06-24 17:00:37.727025','ACTIVE',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_purchases`
--

LOCK TABLES `membership_purchases` WRITE;
/*!40000 ALTER TABLE `membership_purchases` DISABLE KEYS */;
INSERT INTO `membership_purchases` VALUES (1,3538.8199999999997,'2026-06-24 17:00:14.949942','order_T5XjdJLl2Hbfr1','pay_T5XjknAPGpfNXw','SUCCESS',4,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_qrcodes`
--

LOCK TABLES `membership_qrcodes` WRITE;
/*!40000 ALTER TABLE `membership_qrcodes` DISABLE KEYS */;
INSERT INTO `membership_qrcodes` VALUES (1,'2026-06-24 17:00:37.849487','/member/verify/090850b8-71a8-4a48-b000-8c7236cc8f92',1);
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
  `bank_account_holder_name` varchar(255) NOT NULL,
  `bank_account_number` varchar(255) NOT NULL,
  `ifsc_code` varchar(255) NOT NULL,
  `payment_amount` double DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) NOT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  `shop_name` varchar(255) NOT NULL,
  `upi_id` varchar(255) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_order_items`
--

LOCK TABLES `merchant_order_items` WRITE;
/*!40000 ALTER TABLE `merchant_order_items` DISABLE KEYS */;
INSERT INTO `merchant_order_items` VALUES (1,0,2124.15,15,2124.15,2499,10,1,1),(2,0,450,10,450,500,10,1,11),(3,0,2124.15,15,2124.15,2499,10,2,1),(4,5,3324.05,0,3499,3499,20,2,4),(5,5,427.5,10,450,500,15,2,10),(6,0,2124.15,15,2124.15,2499,10,3,1),(7,5,3324.05,0,3499,3499,20,3,4),(8,5,427.5,10,450,500,15,3,10),(9,0,2124.15,15,2124.15,2499,10,4,1),(10,5,427.5,10,450,500,30,4,10),(11,0,595,15,595,700,10,4,12),(12,0,2124.15,15,2124.15,2499,10,5,1),(13,5,1110.645,10,1169.1,1299,20,5,3),(14,5,427.5,10,450,500,30,5,10);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_orders`
--

LOCK TABLES `merchant_orders` WRITE;
/*!40000 ALTER TABLE `merchant_orders` DISABLE KEYS */;
INSERT INTO `merchant_orders` VALUES (1,'2026-06-23 19:28:24.663253',25741.5,'SUCCESS',4248.5,0,NULL,NULL,NULL,29990,4248.5,25741.5,1),(2,'2026-06-24 05:56:42.438371',94135,'PAYMENT_FAILED',4498.5,0,NULL,NULL,NULL,102470,8335,29258.5,1),(3,'2026-06-24 05:57:04.499659',94135,'SUCCESS',4498.5,64876.5,'order_T5MR5CZKhyVnEA','pay_T5MRDrUurDEKUP','0002dca0491f80d2da031b377513a8afac2f108a91b423da22fa95205bdc082b',102470,8335,29258.5,1),(4,'2026-06-24 16:26:33.962119',40016.5,'PAYMENT_FAILED',6298.5,0,NULL,NULL,NULL,46990,6973.5,0,1),(5,'2026-06-24 17:08:01.284093',56279.4,'PENDING',7846.5,0,NULL,NULL,NULL,65970,9690.599999999999,0,1);
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
  `shop_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr77afogh8o46a4e1wwfx00vfk` (`merchant_id`),
  UNIQUE KEY `UKp8boa8pxwwkh88crtbxna5xbe` (`user_id`),
  CONSTRAINT `FKnhhri67wse4kb68qv43blyokr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_profiles`
--

LOCK TABLES `merchant_profiles` WRITE;
/*!40000 ALTER TABLE `merchant_profiles` DISABLE KEYS */;
INSERT INTO `merchant_profiles` VALUES (1,'Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune','EVA Reseller','PUNE','2026-06-23 17:10:20.335987','kalyanibhawar465@gmail.com','27ABCDE1234F1Z5','LLB-MER-1','9876543210',NULL,NULL,'Kalyani Bhawar','CWVPV9577P','Maharashtra','ACTIVE',1,'/uploads/documents/cd524737-57f5-4f46-b5aa-4d17e8152b91.jpeg','609856241345','/uploads/documents/e9ed8578-33f8-4eb5-956c-cf9074ab45ae.jpeg','/uploads/documents/f16bbb71-b6eb-4926-aba7-2c3e1382cbb6.jpeg','kalyani bhawar','8765423233145','CBIN0180688',NULL),(2,'Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune','Cosmetic Shop Owner','PUNE','2026-06-24 12:25:59.971851','kalyanibhawar465@gmail.com','27ABCDE1234F1Z5','LLB-MER-3','9172051078',NULL,NULL,'Kalyani Bhawar','CWVPV9577P','Maharashtra','ACTIVE',3,'/uploads/documents/26e30f43-789b-4e77-a96c-baf209a3466d.png','609856241345','/uploads/documents/0ca7468e-d1af-4f96-8d01-a30375535a54.png','/uploads/documents/34eaf9df-4117-4780-8c2e-4411f84fba65.jpeg','kalyani bhawar','8765423233145','CBIN0180688',NULL),(3,'Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune','Salon Owner','PUNE','2026-06-24 14:16:15.193729','neharesapana2@gmail.com','27ABCDE1234F1Z5','LLB-MER-4','9898989898',NULL,NULL,'Sapna Nehare','CWVPV9577P','Maharashtra','ACTIVE',4,'/uploads/documents/b4b9dcf5-12da-4fc4-b105-4b875cb61f63.jpeg','609856241345','/uploads/documents/7d2f2813-de36-4256-a10a-7e335cbc1d54.jpeg','/uploads/documents/998b00e5-ff5e-47ae-bbbf-007f0b019c2c.jpeg','kalyani bhawar','8765423233145','CBIN0180688',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchants`
--

LOCK TABLES `merchants` WRITE;
/*!40000 ALTER TABLE `merchants` DISABLE KEYS */;
INSERT INTO `merchants` VALUES (1,'+91 99999 88888','2026-06-23 07:40:49.719555','Aundh, Pune','L.L. Beauty Flagship Spa','ACTIVE'),(2,'+91 98888 77777','2026-06-23 07:40:49.726554','Koregaon Park, Pune','L.L. Beauty Lounge','ACTIVE'),(3,'9876543210','2026-06-23 17:10:20.469214','Pending Setup','sai store','ACTIVE'),(4,'9172051078','2026-06-24 12:26:00.052755','Pending Setup','pragati shop','ACTIVE'),(5,'9898989898','2026-06-24 14:16:15.236291','Pending Setup','ShriSai Shop','ACTIVE');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nxl_wallet_transactions`
--

LOCK TABLES `nxl_wallet_transactions` WRITE;
/*!40000 ALTER TABLE `nxl_wallet_transactions` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nxl_wallets`
--

LOCK TABLES `nxl_wallets` WRITE;
/*!40000 ALTER TABLE `nxl_wallets` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,4999,1,1,2),(2,4999,1,2,2),(3,3499,1,3,4),(4,3499,1,4,4),(5,3499,1,5,4);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2026-06-23 09:13:05.401897',NULL,'PENDING',5898.82,2,NULL,'2026-06-24 17:15:52.331985','DELIVERED',NULL,'REF1541'),(2,'2026-06-23 09:13:11.721241','pay_T51FG6FHYuMKfl','SUCCESS',5898.82,2,NULL,'2026-06-23 09:50:26.383992','DELIVERED',NULL,'REF1541'),(3,'2026-06-24 16:56:12.477334',NULL,'PENDING',4128.82,1,NULL,'2026-06-24 16:56:12.477334',NULL,NULL,'REF1541'),(4,'2026-06-24 16:56:12.648095',NULL,'PENDING',4128.82,1,NULL,'2026-06-24 16:56:12.648095',NULL,NULL,'REF1541'),(5,'2026-06-24 16:56:45.734692','pay_T5Xg7DEzjqtNkk','SUCCESS',4128.82,1,NULL,'2026-06-24 16:57:11.906339',NULL,NULL,'REF1541');
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
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otps`
--

LOCK TABLES `otps` WRITE;
/*!40000 ALTER TABLE `otps` DISABLE KEYS */;
INSERT INTO `otps` VALUES (4,'531462','2026-06-23 09:16:58.281102',NULL,_binary '','nehereprachi1@gmail.com'),(32,'311126','2026-06-24 14:37:34.404269',NULL,_binary '','kalyanibhawar3@gmail.com'),(34,'231729','2026-06-24 14:47:50.654415',NULL,_binary '','neharesapana2@gmail.com'),(37,'210848','2026-06-24 17:37:05.682012',NULL,_binary '','kalyanibhawar465@gmail.com');
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,1000,'2026-06-23 07:44:07.544635','RAZORPAY',NULL,'order_T4zj2eNuPnzn11','pay_T4zjEoW1IQJRHF','6a1da6fd2245f53e1a47ac42744e421b65ab94b4a663db8175b57fcae8c7b3a3','SUCCESS',1,'exe_start_1782200646824','INR','AGENT_REGISTRATION',NULL,0),(2,5898.82,'2026-06-23 09:13:05.784220','RAZORPAY',NULL,'order_T51F1bsjEiHnLK',NULL,NULL,'CREATED',2,'1','INR','PRODUCT',NULL,0),(3,5898.82,'2026-06-23 09:13:11.835672','RAZORPAY',NULL,'order_T51F8FSocCZxQK','pay_T51FG6FHYuMKfl','37760d11ad9ee59fa040a73851fade195d6198241a49a29d3422af04581a13fd','SUCCESS',2,'2','INR','PRODUCT',NULL,0),(4,1000,'2026-06-23 09:14:57.075389','RAZORPAY',NULL,'order_T51Gz7qE6kaLzM','pay_T51H5V3S9UIH3O','eba1f4d5a05e2dfb356665365ad939e11de177f7153a81ecc9339bbb1890f278','SUCCESS',2,'exe_start_1782206097036','INR','AGENT_REGISTRATION',NULL,0),(5,50000,'2026-06-23 13:08:19.402330','RAZORPAY',NULL,'order_T55FVwG5bOn6Us',NULL,NULL,'CREATED',1,'merch_dep_1782220099122','INR','MERCHANT_DEPOSIT',NULL,0),(6,50000,'2026-06-23 17:08:40.419598','RAZORPAY',NULL,'order_T59LOYkEStfb9S','pay_T59LaYghZsdcic','4c63701e35811100a4f75a01c156f05b35912d36192311bd288b99284c8c819d','SUCCESS',1,'merch_dep_1782234518528','INR','MERCHANT_DEPOSIT',NULL,0),(7,64876.5,'2026-06-24 05:56:43.350278','RAZORPAY+WALLET',NULL,'order_T5MQhm7yCuXfCh',NULL,NULL,'CREATED',1,'2','INR','MERCHANT_PRODUCT',NULL,0),(8,64876.5,'2026-06-24 05:57:04.764755','RAZORPAY+WALLET',NULL,'order_T5MR5CZKhyVnEA','pay_T5MRDrUurDEKUP','0002dca0491f80d2da031b377513a8afac2f108a91b423da22fa95205bdc082b','SUCCESS',1,'3','INR','MERCHANT_PRODUCT',NULL,0),(9,50000,'2026-06-24 12:21:22.582296','RAZORPAY',NULL,'order_T5Sz2dUZUbWpGc','pay_T5SzBHrwCsD2iv','94094631b246d6443463e41ba7d15bb6caa637c38ae9cbf4a42049f2f9a40f37','SUCCESS',3,'merch_dep_1782303681898','INR','MERCHANT_DEPOSIT',NULL,0),(10,10000,'2026-06-24 12:22:23.023113','RAZORPAY',NULL,'order_T5T06g0HrisZdU','pay_T5T0BqUGbvI79p','e53982ccb586d816f084bfca57dac39e62208d5ae31e933ef7baa595e1ca96b9','SUCCESS',3,'exe_start_1782303742982','INR','AGENT_STARTER',NULL,0),(11,50000,'2026-06-24 14:14:54.776145','RAZORPAY',NULL,'order_T5Uuy6xe8sIwUa','pay_T5Uv7V4OE99iaE','1fd82e11b318e1673f57a00ccf74480098bf8c053ad6b91473da23f2f94d8df9','SUCCESS',4,'merch_dep_1782310494426','INR','MERCHANT_DEPOSIT',NULL,0),(12,40016.5,'2026-06-24 16:26:35.007789','RAZORPAY',NULL,'order_T5XA4356GzBMtu',NULL,NULL,'CREATED',1,'4','INR','MERCHANT_PRODUCT',NULL,0),(13,4128.82,'2026-06-24 16:56:12.827268','RAZORPAY',NULL,'order_T5XfMcyt4SfrC9',NULL,NULL,'CREATED',1,'3','INR','PRODUCT',NULL,0),(14,4128.82,'2026-06-24 16:56:12.823229','RAZORPAY',NULL,'order_T5XfMd79dxPDbz',NULL,NULL,'CREATED',1,'4','INR','PRODUCT',NULL,0),(15,4128.82,'2026-06-24 16:56:45.848994','RAZORPAY',NULL,'order_T5Xfwgl69D4I0f','pay_T5Xg7DEzjqtNkk','9d517f6e52462829fcf9e2b8031fc2b5e752f04ef2bc7fe404cca492d81c5a26','SUCCESS',1,'5','INR','PRODUCT',NULL,0),(16,100,'2026-06-24 16:58:04.144255','RAZORPAY',NULL,'order_T5XhK849oC0dIR',NULL,NULL,'CREATED',1,'1','INR','SALON_DEPOSIT',NULL,0),(17,100,'2026-06-24 16:58:04.397103','RAZORPAY',NULL,'order_T5XhKQVJm83WQ4','pay_T5XhSWtWm0IY93','d562cdefcf6a2ff366bd832db2a3b75c713b3df44ee17aa2d40748b62d7d0b51','SUCCESS',1,'1','INR','SALON_DEPOSIT',NULL,0),(18,3538.8199999999997,'2026-06-24 17:00:15.305989','RAZORPAY',NULL,'order_T5XjdJLl2Hbfr1','pay_T5XjknAPGpfNXw','fc597927e251ae59104831e155655dc42657e684facd475e0414485bf591f182','SUCCESS',1,'1','INR','MEMBERSHIP',NULL,0),(19,56279.4,'2026-06-24 17:08:01.606249','RAZORPAY',NULL,'order_T5XrqHdDT8W8z5','pay_T5Xry7mdSF3oZK','f3902d59709b20c7d6893e3f847d77d280277c2d7533bc3cbd5a51e0761c1fb7','SUCCESS',1,'5','INR','MERCHANT_PRODUCT',NULL,0);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payouts`
--

DROP TABLE IF EXISTS `payouts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payouts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `payment_method` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `utr_number` varchar(255) NOT NULL,
  `agent_profile_id` bigint NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjix6ef8xhgxo275eydmd8hyky` (`agent_profile_id`),
  CONSTRAINT `FKjix6ef8xhgxo275eydmd8hyky` FOREIGN KEY (`agent_profile_id`) REFERENCES `agent_profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payouts`
--

LOCK TABLES `payouts` WRITE;
/*!40000 ALTER TABLE `payouts` DISABLE KEYS */;
/*!40000 ALTER TABLE `payouts` ENABLE KEYS */;
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
INSERT INTO `products` VALUES (1,'Skincare','2026-05-23 05:31:29.422435','Advanced anti-aging serum with pure rose extract and gold particles.','/images/skincare.png','Rose Gold Elixir',2499,80,'ACTIVE',2124.15,15),(2,'Perfumes','2026-05-23 05:31:29.442728','Exclusive oriental fragrance with deep oud, amber and musk notes.','/images/perfume.png','Oud Majestic',4999,50,'ACTIVE',4249.15,15),(3,'Haircare','2026-05-23 05:31:29.450627','Salon-grade keratin formula for silky, frizz-free hair every day.','/images/haircare.png','Keratin Pro Shampoo',1299,150,'ACTIVE',1169.1,10),(4,'Spa / Detox','2026-05-23 05:31:29.459188','Complete spa kit with essential oils, detox mask, and aromatherapy candles.','/images/spa.png','Luxury Detox Kit',3499,10,NULL,NULL,0),(7,'Skincare','2026-05-25 07:19:13.429453','Create a smooth, hydrated makeup base with our lightweight primer that minimizes pores, controls oil, and keeps makeup fresh all day.','/uploads/products/1779693553389_images.jfif','Primer',1000,5,'ACTIVE',900,10),(10,'Perfumes','2026-06-18 06:51:29.950288','Best perfumes','/uploads/products/1781765489934_gettyimages-637623730-612x612.jpg','Skinn by Titan',500,45,'ACTIVE',450,10),(11,'Haircare','2026-06-19 05:13:35.364463','Ayurvedic herbal oils are therapeutic blends of pure plant base oils (such as sesame or coconut) slowly infused with plant extracts, roots, and leaves. ','/uploads/products/1781846015339_Ayurvedic_Herb_Oil.png','Ayurvedik Herb Oil',500,40,'ACTIVE',450,10),(12,'Spa','2026-06-19 05:24:36.704608','Nivea lotions are dermatologically tested moisturizers formulated to deeply hydrate, nourish, and protect the skin. ','/uploads/products/1781846676692_nivea-body-milk-body-lotion.png','Nivea ',700,30,'ACTIVE',595,15);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qr_codes`
--

LOCK TABLES `qr_codes` WRITE;
/*!40000 ALTER TABLE `qr_codes` DISABLE KEYS */;
INSERT INTO `qr_codes` VALUES (1,'2026-06-23 07:40:49.754584','/wallet/redeem?merchantId=1','ACTIVE',1),(2,'2026-06-23 07:40:49.772169','/wallet/redeem?merchantId=2','ACTIVE',2),(3,'2026-06-23 17:10:20.478328','/wallet/redeem?merchantId=3','ACTIVE',3),(4,'2026-06-24 12:26:00.055769','/wallet/redeem?merchantId=4','ACTIVE',4),(5,'2026-06-24 14:16:15.239255','/wallet/redeem?merchantId=5','ACTIVE',5);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reward_points`
--

LOCK TABLES `reward_points` WRITE;
/*!40000 ALTER TABLE `reward_points` DISABLE KEYS */;
INSERT INTO `reward_points` VALUES (1,0,0,0,2),(2,0,0,0,1);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reward_transactions`
--

LOCK TABLES `reward_transactions` WRITE;
/*!40000 ALTER TABLE `reward_transactions` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salon_services`
--

LOCK TABLES `salon_services` WRITE;
/*!40000 ALTER TABLE `salon_services` DISABLE KEYS */;
INSERT INTO `salon_services` VALUES (1,'Professional cut, shampoo, conditioning and blow-dry styling.',45,'/images/haircare.png','Hair Styling & Cut',899,1,NULL),(2,'Deep nourishment, repair treatment, and relaxing scalp massage.',60,'/images/haircare.png','Luxury Hair Spa',1499,1,NULL),(3,'Premium exfoliating facial with botanical extracts for instant radiance.',50,'/images/skincare.png','Gold Glow Facial',1999,1,NULL),(4,'Elite luxury bridal makeover including saree draping, hair, and makeup.',180,'/images/spa.png','Bridal Makeover',9999,1,NULL),(5,'Premium global hair coloring and highlights.',120,'/images/haircare.png','Hair Color',2999,1,NULL),(9,'vvsgytdtrse',30,NULL,'haircut',600,1,NULL),(10,'ksoiisuig',30,NULL,'NailArt',1000,1,NULL);
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
  `payment_amount` double DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `razorpay_payment_id` varchar(255) DEFAULT NULL,
  `referral_code` varchar(255) DEFAULT NULL,
  `registration_type` varchar(255) DEFAULT NULL,
  `upi_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi9806p8j67a488cshvt8islay` (`user_id`),
  CONSTRAINT `FKi9806p8j67a488cshvt8islay` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_applications`
--

LOCK TABLES `store_applications` WRITE;
/*!40000 ALTER TABLE `store_applications` DISABLE KEYS */;
INSERT INTO `store_applications` VALUES (1,'Kalyani Vilas Bhawar','kalyanibhawar465@gmail.com','9898989898','2026-06-23 07:44:34.375154','Address: Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune\nCity: PUNE\nState: Maharashtra\nOccupation: seller\nExperience: 1\nReferral Code: \nRegistration Type: REGISTRATION','APPROVED','AGENT',1,0,NULL,NULL,'Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune',NULL,NULL,NULL,'PUNE',NULL,NULL,NULL,'Kalyani Vilas Bhawar',NULL,'ABCDE1234F',NULL,'Maharashtra',1000,'2026-06-23 07:44:07.544635','SUCCESS','411057','pay_T4zjEoW1IQJRHF','','REGISTRATION','kalyanibhawar@upi'),(2,'Prachi Nehare','nehereprachi1@gmail.com','9876543210','2026-06-23 09:15:17.924101','Address: Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune\nCity: PUNE\nState: Maharashtra\nOccupation: seller\nExperience: 2\nReferral Code: REF1541\nRegistration Type: REGISTRATION','PENDING','AGENT',2,0,NULL,NULL,'Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune',NULL,NULL,NULL,'PUNE',NULL,NULL,NULL,'Prachi Nehare',NULL,'ABCDE1234F',NULL,'Maharashtra',1000,'2026-06-23 09:14:57.075389','SUCCESS','411057','pay_T51H5V3S9UIH3O','REF1541','REGISTRATION','prachi@upi'),(3,'sai store','kalyanibhawar465@gmail.com','9876543210','2026-06-23 17:09:08.061029','Shop Name: sai store\nOwner Name: Kalyani Bhawar\nAddress: Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune\nCity: PUNE\nState: Maharashtra\nPincode: 411057\nGST: 27ABCDE1234F1Z5\nPAN: CWVPV9577P\nAadhar: 609856241345\nBusiness Type: EVA Reseller\nBank Holder: kalyani bhawar\nOnline Selling: Yes\nOffline Selling: Yes\nRegistration Type: MERCHANT_DEPOSIT\nSecurity Deposit: ₹50,000','APPROVED','MERCHANT',1,0,'/uploads/documents/cd524737-57f5-4f46-b5aa-4d17e8152b91.jpeg','609856241345','Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune','kalyani bhawar','8765423233145','EVA Reseller','PUNE','/uploads/documents/e9ed8578-33f8-4eb5-956c-cf9074ab45ae.jpeg','27ABCDE1234F1Z5','CBIN0180688','Kalyani Bhawar','/uploads/documents/f16bbb71-b6eb-4926-aba7-2c3e1382cbb6.jpeg','CWVPV9577P',NULL,'Maharashtra',NULL,NULL,NULL,'411057',NULL,NULL,NULL,'kalyani@upi'),(4,'pragati shop','kalyanibhawar465@gmail.com','9172051078','2026-06-24 12:21:46.119762','Shop Name: pragati shop\nOwner Name: Kalyani Bhawar\nAddress: Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune\nCity: PUNE\nState: Maharashtra\nPincode: 411057\nGST: 27ABCDE1234F1Z5\nPAN: CWVPV9577P\nAadhar: 609856241345\nBusiness Type: Cosmetic Shop Owner\nBank Holder: kalyani bhawar\nOnline Selling: No\nOffline Selling: Yes\nRegistration Type: MERCHANT_DEPOSIT\nSecurity Deposit: ₹50,000','APPROVED','MERCHANT',3,0,'/uploads/documents/26e30f43-789b-4e77-a96c-baf209a3466d.png','609856241345','Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune','kalyani bhawar','8765423233145','Cosmetic Shop Owner','PUNE','/uploads/documents/0ca7468e-d1af-4f96-8d01-a30375535a54.png','27ABCDE1234F1Z5','CBIN0180688','Kalyani Bhawar','/uploads/documents/34eaf9df-4117-4780-8c2e-4411f84fba65.jpeg','CWVPV9577P',NULL,'Maharashtra',NULL,NULL,NULL,'411057',NULL,NULL,NULL,'kalyani@upi'),(5,'Kalyani Vilas Bhawar','kalyanibhawar465@gmail.com','9876543210','2026-06-24 12:22:43.076275','Address: Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune\nCity: PUNE\nState: Maharashtra\nOccupation: seller\nExperience: 2\nReferral Code: REF1541\nRegistration Type: STARTER_KIT\nAdmin Rejection Remarks: regect ','REJECTED','AGENT',3,0,NULL,NULL,'Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune',NULL,NULL,NULL,'PUNE',NULL,NULL,NULL,'Kalyani Vilas Bhawar',NULL,'ABCDE1234F',NULL,'Maharashtra',10000,'2026-06-24 12:22:23.023113','SUCCESS','411057','pay_T5T0BqUGbvI79p','REF1541','STARTER_KIT','kalyani@upi'),(6,'ShriSai Shop','neharesapana2@gmail.com','9898989898','2026-06-24 14:15:26.123123','Shop Name: ShriSai Shop\nOwner Name: Sapna Nehare\nAddress: Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune\nCity: PUNE\nState: Maharashtra\nPincode: 411057\nGST: 27ABCDE1234F1Z5\nPAN: CWVPV9577P\nAadhar: 609856241345\nBusiness Type: Salon Owner\nBank Holder: kalyani bhawar\nOnline Selling: No\nOffline Selling: Yes\nRegistration Type: MERCHANT_DEPOSIT\nSecurity Deposit: ₹50,000','APPROVED','MERCHANT',4,0,'/uploads/documents/b4b9dcf5-12da-4fc4-b105-4b875cb61f63.jpeg','609856241345','Bhagwan Nagar, Wkad, Pimpri-Chinchwad, Pune','kalyani bhawar','8765423233145','Salon Owner','PUNE','/uploads/documents/7d2f2813-de36-4256-a10a-7e335cbc1d54.jpeg','27ABCDE1234F1Z5','CBIN0180688','Sapna Nehare','/uploads/documents/998b00e5-ff5e-47ae-bbbf-007f0b019c2c.jpeg','CWVPV9577P',NULL,'Maharashtra',NULL,NULL,NULL,'411057',NULL,NULL,NULL,'sapna@upi');
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_credits`
--

LOCK TABLES `store_credits` WRITE;
/*!40000 ALTER TABLE `store_credits` DISABLE KEYS */;
INSERT INTO `store_credits` VALUES (1,0.00,'2026-06-23 17:11:28.479349',1),(2,0.00,'2026-06-24 12:27:47.722223',3),(3,0.00,'2026-06-24 14:32:08.278209',4);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_memberships`
--

LOCK TABLES `user_memberships` WRITE;
/*!40000 ALTER TABLE `user_memberships` DISABLE KEYS */;
INSERT INTO `user_memberships` VALUES (1,'2027-07-24 17:00:37.727025','pay_T5XjknAPGpfNXw','2026-06-24 17:00:37.727025','ACTIVE',4,1,NULL,NULL,'LLB-P-00001','090850b8-71a8-4a48-b000-8c7236cc8f92');
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-06-23 06:44:17.145801','kalyanibhawar465@gmail.com','9172051078','Kalyani  Bhawar',NULL,300.00,_binary '\0','MERCHANT',_binary '','ACTIVE','NOT_APPLIED','ACTIVE',NULL,''),(2,'2026-06-23 09:11:58.158401','nehereprachi1@gmail.com','9887776554','Prachi Nehare',NULL,0.00,_binary '\0','USER',_binary '','PENDING','NOT_APPLIED','NOT_APPLIED',NULL,'REF1541'),(3,'2026-06-24 11:57:31.599341','kalyanibhawar3@gmail.com','9172051078','Kalyani',NULL,55000.00,_binary '\0','MERCHANT',_binary '','REJECTED','NOT_APPLIED','ACTIVE',NULL,'REF1541'),(4,'2026-06-24 12:31:01.283483','neharesapana2@gmail.com','9887776554','Sapna Nehare',NULL,55000.00,_binary '\0','MERCHANT',_binary '','NOT_APPLIED','NOT_APPLIED','ACTIVE',NULL,'');
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
INSERT INTO `wallet_transactions` VALUES (1,55000.00,'2026-06-23 17:10:20.416706','NXL Security Deposit Bonus','CREDIT',1,NULL,NULL,NULL,'DEPOSIT',NULL,NULL,'SUCCESS'),(2,25741.50,'2026-06-23 19:28:26.112419','Reserved credits for Merchant Order #1','DEBIT',1,NULL,NULL,NULL,'MERCHANT_ORDER',NULL,NULL,'SUCCESS'),(3,29258.50,'2026-06-24 05:56:42.677280','Reserved NXL Credits for Merchant Order #2','DEBIT',1,NULL,NULL,NULL,'MERCHANT_ORDER',NULL,NULL,'SUCCESS'),(4,29258.50,'2026-06-24 05:56:50.270200','Restored NXL Credits — Merchant Order #2 payment failed','CREDIT',1,NULL,NULL,NULL,'MERCHANT_ORDER_ROLLBACK',NULL,NULL,'SUCCESS'),(5,29258.50,'2026-06-24 05:57:04.652259','Reserved NXL Credits for Merchant Order #3','DEBIT',1,NULL,NULL,NULL,'MERCHANT_ORDER',NULL,NULL,'SUCCESS'),(6,55000.00,'2026-06-24 12:25:59.999764','NXL Security Deposit Bonus','CREDIT',3,NULL,NULL,NULL,'DEPOSIT',NULL,NULL,'SUCCESS'),(7,55000.00,'2026-06-24 14:16:15.224256','NXL Security Deposit Bonus','CREDIT',4,NULL,NULL,NULL,'DEPOSIT',NULL,NULL,'SUCCESS'),(8,300.00,'2026-06-24 17:00:37.992520','Welcome credit for Eva Pink Card activation','CREDIT',1,NULL,NULL,NULL,'MEMBERSHIP_WELCOME',NULL,NULL,'SUCCESS');
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
INSERT INTO `wallets` VALUES (1,300.00,1),(2,0.00,2),(3,55000.00,3),(4,55000.00,4);
/*!40000 ALTER TABLE `wallets` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-24 23:25:29
