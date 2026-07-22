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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_notifications`
--

LOCK TABLES `admin_notifications` WRITE;
/*!40000 ALTER TABLE `admin_notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_nxl_transactions`
--

DROP TABLE IF EXISTS `admin_nxl_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_nxl_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(10) NOT NULL,
  `amount` decimal(14,2) NOT NULL,
  `balance_before` decimal(14,2) NOT NULL,
  `balance_after` decimal(14,2) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_nxl_transactions`
--

LOCK TABLES `admin_nxl_transactions` WRITE;
/*!40000 ALTER TABLE `admin_nxl_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_nxl_transactions` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=413 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (412,'2026-07-21 17:27:56.811810','admin@llbeauty.com','L.L. Beauty Admin','$2a$10$rOFDHHF3aom24JoiY45aX.kGyjAxZlZP4eRH0VpU88WCOkotlTXxi');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agent_profiles`
--

LOCK TABLES `agent_profiles` WRITE;
/*!40000 ALTER TABLE `agent_profiles` DISABLE KEYS */;
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
  `earned_nxl` double DEFAULT NULL,
  `final_paid_amount` double DEFAULT NULL,
  `nxl_rewarded` bit(1) DEFAULT NULL,
  `nxl_used` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `franchise_leads`
--

LOCK TABLES `franchise_leads` WRITE;
/*!40000 ALTER TABLE `franchise_leads` DISABLE KEYS */;
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
-- Table structure for table `matrimony_chat_messages`
--

DROP TABLE IF EXISTS `matrimony_chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matrimony_chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) DEFAULT NULL,
  `message` text,
  `timestamp` datetime(6) DEFAULT NULL,
  `match_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mat_chat_match` (`match_id`),
  KEY `idx_mat_chat_sender` (`sender_id`),
  KEY `idx_mat_chat_receiver` (`receiver_id`),
  CONSTRAINT `FK3f12e18n1t1bu0koqlqwuy0br` FOREIGN KEY (`match_id`) REFERENCES `matrimony_matches` (`id`),
  CONSTRAINT `FK5qo753occynkqffnm04wym2xd` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKc2kd0wir9dmt52uqjd42y2g4n` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matrimony_chat_messages`
--

LOCK TABLES `matrimony_chat_messages` WRITE;
/*!40000 ALTER TABLE `matrimony_chat_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `matrimony_chat_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matrimony_interests`
--

DROP TABLE IF EXISTS `matrimony_interests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matrimony_interests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `status` enum('ACCEPTED','PENDING','REJECTED') DEFAULT NULL,
  `receiver_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mat_interest_sender` (`sender_id`),
  KEY `idx_mat_interest_receiver` (`receiver_id`),
  KEY `idx_mat_interest_status` (`status`),
  CONSTRAINT `FK6eckv9w93bx6winhdwtyqantw` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKaxtcxhf5xr5rotgpot823ukds` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matrimony_interests`
--

LOCK TABLES `matrimony_interests` WRITE;
/*!40000 ALTER TABLE `matrimony_interests` DISABLE KEYS */;
/*!40000 ALTER TABLE `matrimony_interests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matrimony_matches`
--

DROP TABLE IF EXISTS `matrimony_matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matrimony_matches` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `user_one_id` bigint NOT NULL,
  `user_two_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mat_match_user_one` (`user_one_id`),
  KEY `idx_mat_match_user_two` (`user_two_id`),
  CONSTRAINT `FK4gmp2c31avqfd7mbfyd5nmtt2` FOREIGN KEY (`user_one_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrm9sbh295cs9ctgana2h3iit1` FOREIGN KEY (`user_two_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matrimony_matches`
--

LOCK TABLES `matrimony_matches` WRITE;
/*!40000 ALTER TABLE `matrimony_matches` DISABLE KEYS */;
/*!40000 ALTER TABLE `matrimony_matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matrimony_preferences`
--

DROP TABLE IF EXISTS `matrimony_preferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matrimony_preferences` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `maximum_age` int DEFAULT NULL,
  `minimum_age` int DEFAULT NULL,
  `preferred_city` varchar(255) DEFAULT NULL,
  `preferred_education` varchar(255) DEFAULT NULL,
  `preferred_occupation` varchar(255) DEFAULT NULL,
  `preferred_religion` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mat_pref_user` (`user_id`),
  CONSTRAINT `FK3fnyu4o335g39xdv5we1uf5w4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matrimony_preferences`
--

LOCK TABLES `matrimony_preferences` WRITE;
/*!40000 ALTER TABLE `matrimony_preferences` DISABLE KEYS */;
/*!40000 ALTER TABLE `matrimony_preferences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matrimony_profiles`
--

DROP TABLE IF EXISTS `matrimony_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matrimony_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `about_me` varchar(1000) DEFAULT NULL,
  `annual_income` decimal(15,2) DEFAULT NULL,
  `brother_count` int DEFAULT NULL,
  `caste` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `education` varchar(255) DEFAULT NULL,
  `family_type` varchar(255) DEFAULT NULL,
  `father_occupation` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `height` double DEFAULT NULL,
  `mother_occupation` varchar(255) DEFAULT NULL,
  `mother_tongue` varchar(255) DEFAULT NULL,
  `occupation` varchar(255) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `religion` varchar(255) DEFAULT NULL,
  `sister_count` int DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','BLOCKED','PENDING','REJECTED') DEFAULT NULL,
  `sub_caste` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mat_profile_user` (`user_id`),
  KEY `idx_mat_profile_status` (`status`),
  KEY `idx_mat_profile_city` (`city`),
  CONSTRAINT `FKle4vjg025ltqj693h2s5fbwdi` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matrimony_profiles`
--

LOCK TABLES `matrimony_profiles` WRITE;
/*!40000 ALTER TABLE `matrimony_profiles` DISABLE KEYS */;
INSERT INTO `matrimony_profiles` VALUES (1,'2026-07-21 17:42:51.597895','2026-07-21 18:02:25.983724','sags mnagysda nytdrf nbhtr fsksayw jsauysad jhayas mnafwe dsvxtsa',500000.00,1,'Maratha','PUNE','Cognizant','India','2003-07-26','MCS (Computer Science)','Joint','Farmer','Kalyani Vilas Bhawar','Female',159,'Housewife','Hindi Marathi English','Developr','/uploads/documents/3629ece1-7850-462c-9684-a93fccf4a06f.jpeg','Hindu',0,'Maharashtra','APPROVED','',1);
/*!40000 ALTER TABLE `matrimony_profiles` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_profiles`
--

LOCK TABLES `member_profiles` WRITE;
/*!40000 ALTER TABLE `member_profiles` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_history`
--

LOCK TABLES `membership_history` WRITE;
/*!40000 ALTER TABLE `membership_history` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_purchases`
--

LOCK TABLES `membership_purchases` WRITE;
/*!40000 ALTER TABLE `membership_purchases` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_qrcodes`
--

LOCK TABLES `membership_qrcodes` WRITE;
/*!40000 ALTER TABLE `membership_qrcodes` DISABLE KEYS */;
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
INSERT INTO `memberships` VALUES (4,'5% Discount\r\nEarly Access to Sales & Events\r\nExclusive Birthday Offers\r\nReward Points Earning\r\nStandard Member Support',0.05,365,'Eva Pink Card',2999,1,1),(5,'10% Discount\r\nFree Delivery on All Orders\r\nVIP Launch Access\r\nDouble Reward Points\r\nEvent Invitations',0.1,365,'Eva Gold Card',9999,1,1),(6,'15% Discount\r\nConcierge Support\r\nLuxury Gifts on Signup\r\nTriple Reward Points\r\nVIP Lounge Access',0.15,365,'Eva Black Card',24999,1,1);
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
  `shop_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr77afogh8o46a4e1wwfx00vfk` (`merchant_id`),
  UNIQUE KEY `UKp8boa8pxwwkh88crtbxna5xbe` (`user_id`),
  CONSTRAINT `FKnhhri67wse4kb68qv43blyokr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchants`
--

LOCK TABLES `merchants` WRITE;
/*!40000 ALTER TABLE `merchants` DISABLE KEYS */;
INSERT INTO `merchants` VALUES (1,'+91 99999 88888','2026-07-21 17:27:56.981809','Aundh, Pune','L.L. Beauty Flagship Spa','ACTIVE'),(2,'+91 98888 77777','2026-07-21 17:27:56.987432','Koregaon Park, Pune','L.L. Beauty Lounge','ACTIVE');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'2026-07-21 18:02:11.646083',_binary '\0','Your matrimony profile has been approved.',NULL,'Profile Approved','SUCCESS',1),(2,'2026-07-21 18:02:11.692652',_binary '\0','Your profile has been approved.',NULL,'Profile Approved','INFO',1);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nxl_system_wallet`
--

DROP TABLE IF EXISTS `nxl_system_wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nxl_system_wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(14,2) NOT NULL,
  `total_added` decimal(14,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nxl_system_wallet`
--

LOCK TABLES `nxl_system_wallet` WRITE;
/*!40000 ALTER TABLE `nxl_system_wallet` DISABLE KEYS */;
INSERT INTO `nxl_system_wallet` VALUES (1,10000.00,0.00);
/*!40000 ALTER TABLE `nxl_system_wallet` ENABLE KEYS */;
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
  `admin_balance_after` decimal(14,2) NOT NULL,
  `admin_balance_before` decimal(14,2) NOT NULL,
  `source` varchar(30) NOT NULL,
  `user_balance_before` decimal(14,2) NOT NULL,
  `reward_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg0cr4jmbmjl8u1mk2qatao8lw` (`source`,`transaction_id`),
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
  `total_earned` decimal(14,2) NOT NULL,
  `total_spent` decimal(14,2) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status_history`
--

DROP TABLE IF EXISTS `order_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `remarks` text,
  `status` varchar(50) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` varchar(255) NOT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnmcbg3mmbt8wfva97ra40nmp3` (`order_id`),
  CONSTRAINT `FKnmcbg3mmbt8wfva97ra40nmp3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status_history`
--

LOCK TABLES `order_status_history` WRITE;
/*!40000 ALTER TABLE `order_status_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_status_history` ENABLE KEYS */;
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
  `billing_mobile` varchar(255) DEFAULT NULL,
  `billing_name` varchar(255) DEFAULT NULL,
  `courier_name` varchar(255) DEFAULT NULL,
  `expected_delivery_date` datetime(6) DEFAULT NULL,
  `last_status_updated_at` datetime(6) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL,
  `tracking_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otps`
--

LOCK TABLES `otps` WRITE;
/*!40000 ALTER TABLE `otps` DISABLE KEYS */;
INSERT INTO `otps` VALUES (2,'557185','2026-07-21 18:11:57.319055',NULL,_binary '','kalyanibhawar465@gmail.com');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Skincare','2026-05-23 05:31:29.422435','Advanced anti-aging serum with pure rose extract and gold particles.','/images/skincare.png','Rose Gold Elixir',2500,100,'ACTIVE',2250,10),(2,'Perfumes','2026-05-23 05:31:29.442728','Exclusive oriental fragrance with deep oud, amber and musk notes.','/images/perfume.png','Oud Majestic',4999,50,'ACTIVE',4249.15,15),(3,'Haircare','2026-05-23 05:31:29.450627','Salon-grade keratin formula for silky, frizz-free hair every day.','/images/haircare.png','Keratin Pro Shampoo',1299,150,'ACTIVE',1169.1,10),(4,'Spa / Detox','2026-05-23 05:31:29.459188','Complete spa kit with essential oils, detox mask, and aromatherapy candles.','/images/spa.png','Luxury Detox Kit',3499,10,NULL,NULL,0),(7,'Skincare','2026-05-25 07:19:13.429453','Create a smooth, hydrated makeup base with our lightweight primer that minimizes pores, controls oil, and keeps makeup fresh all day.','/uploads/products/1779693553389_images.jfif','Primer',1000,100,'ACTIVE',900,10),(10,'Perfumes','2026-06-18 06:51:29.950288','Best perfumes','/uploads/products/1781765489934_gettyimages-637623730-612x612.jpg','Skinn by Titan',500,25,'ACTIVE',450,10),(11,'Haircare','2026-06-19 05:13:35.364463','Ayurvedic herbal oils are therapeutic blends of pure plant base oils (such as sesame or coconut) slowly infused with plant extracts, roots, and leaves. ','/uploads/products/1781846015339_Ayurvedic_Herb_Oil.png','Ayurvedik Herb Oil',500,30,'ACTIVE',450,10),(12,'Spa','2026-06-19 05:24:36.704608','Nivea lotions are dermatologically tested moisturizers formulated to deeply hydrate, nourish, and protect the skin. ','/uploads/products/1781846676692_nivea-body-milk-body-lotion.png','Nivea ',700,30,'ACTIVE',595,15),(14,'Perfumes','2026-07-05 13:28:06.378169','sfsyt hjyil ','/uploads/products/1783258086356_perfume1.jpeg','Kaorini',1000,30,'ACTIVE',950,5),(15,'Perfumes','2026-07-05 13:28:51.269647','nhgsdfsd hggdgfsamanj','/uploads/products/1783258131245_perfume_2.jpeg','kaorine',500,50,'ACTIVE',485,3),(16,'Perfumes','2026-07-05 13:29:32.464190','vvjhsa vhhhhhhhh','/uploads/products/1783258172438_kaorine_perfume_3.jpeg','kaorini',1000,40,'ACTIVE',900,10),(17,'Perfumes','2026-07-05 13:30:25.144376','badshsiube vhjkj','/uploads/products/1783258225127_kaorine_perfumes.jpeg','kaorini',700,50,'ACTIVE',665,5),(18,'Accessories','2026-07-06 16:36:07.592225','Elegant antique gold-finish kundan jewellery set with pearl drop detailing. Includes matching maang tikka and earrings — perfect for weddings and festive wear.','/uploads/products/1783355767546_Accessories_1.jfif','Antique Gold Kundan Pearl Maang Tikka & Earring Set',1000,50,'ACTIVE',900,10),(19,'Gift Packs','2026-07-09 13:00:26.383068','quyewe wyuwetcfd','/uploads/products/1783602026355_WhatsApp_Image_2026-06-28_at_6.11.58_PM.jpeg','perfumes',2000,100,'ACTIVE',1800,10);
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
INSERT INTO `qr_codes` VALUES (1,'2026-07-21 17:27:57.011630','/wallet/redeem?merchantId=1','ACTIVE',1),(2,'2026-07-21 17:27:57.023061','/wallet/redeem?merchantId=2','ACTIVE',2);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reward_points`
--

LOCK TABLES `reward_points` WRITE;
/*!40000 ALTER TABLE `reward_points` DISABLE KEYS */;
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
INSERT INTO `salon_services` VALUES (1,'Professional cut, shampoo, conditioning and blow-dry styling.',45,'/images/haircare.png','Hair Styling & Cut',899,1,NULL),(2,'Deep nourishment, repair treatment, and relaxing scalp massage.',90,NULL,'Luxury Hair Spa',1500,1,NULL),(3,'Premium exfoliating facial with botanical extracts for instant radiance.',50,'/images/skincare.png','Gold Glow Facial',1999,1,NULL),(4,'Elite luxury bridal makeover including saree draping, hair, and makeup.',180,'/images/spa.png','Bridal Makeover',9999,1,NULL),(5,'Premium global hair coloring and highlights.',120,'/images/haircare.png','Hair Color',2999,1,NULL),(9,'vvsgytdtrse',30,NULL,'haircut',600,1,NULL),(10,'ksoiisuig',30,NULL,'NailArt',1000,1,NULL);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_applications`
--

LOCK TABLES `store_applications` WRITE;
/*!40000 ALTER TABLE `store_applications` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_memberships`
--

LOCK TABLES `user_memberships` WRITE;
/*!40000 ALTER TABLE `user_memberships` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-07-21 17:41:46.684147','kalyanibhawar465@gmail.com','9172051078','Kalyani Vilas Bhawar',NULL,0.00,_binary '\0','USER',_binary '','NOT_APPLIED','NOT_APPLIED','NOT_APPLIED',NULL,'');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
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

-- Dump completed on 2026-07-22  0:01:01
