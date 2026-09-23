EVA Beauty – Multi-Service O2O Platform

EVA Beauty is a full-stack Java Spring Boot web application that brings multiple beauty, shopping, membership, business, and social services together on a single platform.

The platform includes online product shopping, salon appointment booking, franchise applications, EVA membership, Agent and Merchant store modules, matrimony, wallet/NXL token functionality, referral commissions, cashback/rewards, payments, email OTP authentication, and a complete admin management system.

Note: The Events module is not included in this project overview.

✨ Project Highlights

Full-stack web application built with Java 21 and Spring Boot

Secure user authentication using email OTP + JWT

Separate Admin authentication and dashboard

Online product shopping and order tracking

Salon appointment booking with online payment

Franchise enquiry/application management

EVA membership purchase and membership-card functionality

EVA Agent referral and commission system

EVA Merchant wholesale shopping

Matrimony profile, matching, request, and chat functionality

NXL token/wallet system with top-up bonus and transaction tracking

Razorpay payment gateway integration

Product cashback/reward functionality

PDF receipt/invoice generation

Email notifications

Role-based access control

MySQL database with Spring Data JPA/Hibernate

Swagger/OpenAPI support

🧩 Main Modules

1. 🛍️ Products / Online Shopping

Users can browse and purchase products online through the EVA Beauty platform.

User Features

Browse products

View product details

Add products to cart

Update cart quantities

Checkout

Online payment

Order placement

Order history

Track order status

View order status history

Payment status tracking

PDF receipt/invoice support

NXL / Reward Integration

Product purchases can also participate in the platform's NXL/cashback system according to the configured business rules.

The current implementation includes cashback processing for eligible online shopping orders.

2. 💇 Salon

Users can book salon appointments online.

User Flow

User
  ↓
Select Salon
  ↓
Select Service
  ↓
Select Date / Time
  ↓
Create Appointment
  ↓
Pay ₹100 Advance
  ↓
Payment Verification
  ↓
Appointment Confirmed
  ↓
Booking Token Generated
  ↓
Salon Receipt

Features

View salon information

View available salon services

Select appointment date/time

Create salon appointment

₹100 advance booking payment

Razorpay payment integration

Optional NXL usage for eligible booking payment

Booking confirmation

Booking token generation

Payment status tracking

PDF salon receipt

Admin appointment management

The application also supports referral-related rewards for salon bookings.

3. 🏢 Franchise

Users can submit a franchise enquiry/application to EVA.

Franchise Application Includes

Applicant name

Mobile number

Email

City

Budget

Preferred location

Franchise type

Optional referral code

Flow

User
  ↓
Franchise Page
  ↓
Submit Franchise Application
  ↓
Application Stored
  ↓
Admin Reviews Application
  ↓
Approve / Reject
  ↓
If Approved → Final Franchise Amount
  ↓
Referral Commission Processing

Admin can manage franchise leads, update status, add remarks, and process the final franchise amount.

The implementation also supports referral commission generation for eligible franchise applications.

4. 👑 Membership

Users can purchase an EVA membership and become EVA members.

Features

View available membership plans

Purchase membership

Membership payment through Razorpay

Wallet balance can be used toward eligible membership purchases

Membership renewal

Membership upgrade

GST calculation

Membership history

Membership status

Membership ID

Membership QR code

Member profile

Membership verification

Membership Flow

User
  ↓
View Membership Plans
  ↓
Select Plan
  ↓
Payment / Wallet
  ↓
Razorpay Verification
  ↓
Membership Activated
  ↓
Membership ID + QR Code
  ↓
Member Dashboard

The membership implementation also includes referral and reward-related functionality.

5. 🏪 EVA Store

The Store module contains two business roles:

EVA Agent

EVA Merchant

5.1 EVA Agent

An EVA Agent can use a referral code to participate in EVA's referral network.

Agent Flow

User
  ↓
Apply as EVA Agent
  ↓
Admin Reviews Application
  ↓
Application Approved
  ↓
Agent Profile Created
  ↓
Unique Referral Code Generated
  ↓
Agent Shares Referral Code
  ↓
New User / Customer Uses Referral Code
  ↓
Eligible Activity Completed
  ↓
Commission Generated

Agent Features

Agent application

Admin approval/rejection

Agent dashboard

Unique Agent ID

Unique referral code

Referral tracking

Commission tracking

NXL-based commission/reward processing

UPI information

Agent profile

The project contains referral commission logic for activities such as eligible product orders, salon bookings, franchise referrals, and store/merchant-related referrals.

5.2 EVA Merchant

An EVA Merchant can purchase products at merchant/wholesale pricing for their business or shop.

Merchant Flow

User
  ↓
Apply as Merchant
  ↓
Submit Business Details
  ↓
Security Deposit / Payment
  ↓
Admin Review
  ↓
Application Approved
  ↓
Merchant Profile Activated
  ↓
Wholesale Product Access
  ↓
Merchant Cart
  ↓
Merchant Checkout
  ↓
Merchant Orders

Merchant Features

Merchant application

Business details

GST/PAN details

Bank details

Document upload

Referral code validation

Admin approval/rejection

Merchant dashboard

Wholesale product catalogue

Merchant cart

Bulk purchasing

Merchant-specific pricing

Merchant discount

Bulk discount

Merchant orders

Merchant order details

Merchant wallet

Merchant status management

The current implementation uses a ₹50,000 merchant security-deposit workflow. The exact wallet/NXL credit behavior after approval is controlled by the application's business logic.

6. 💍 Matrimony

The Matrimony module allows users to create profiles and find suitable matches.

User Features

Create matrimony profile

Multi-step profile creation

Personal information

Education and professional details

Family information

Partner preferences

Profile search

Search filters

View profiles

Send interest/request

Receive interest/request

Accept or reject interest

Match management

Matrimony chat

Email notifications

Profile approval workflow

Matrimony Flow

User
  ↓
Create Matrimony Profile
  ↓
Submit Profile
  ↓
Admin Review
  ↓
Profile Approved
  ↓
Search Profiles
  ↓
View Match
  ↓
Send Interest
  ↓
Other User Accepts
  ↓
Match Created
  ↓
Chat

Admin Matrimony Management

Admin can manage:

Pending profiles

Approved profiles

Rejected profiles

Blocked profiles

Profile details

Profile approval

Profile rejection

Profile blocking

7. 🪙 NXL Token / Wallet System

EVA includes an NXL token system for user wallet functionality, rewards, cashback, and selected platform transactions.

NXL Top-Up

Users can purchase NXL through the wallet top-up system.

The current implementation provides a 5% bonus on top-up.

Example

User pays ₹100
       ↓
5% bonus = 5 NXL
       ↓
Total = 105 NXL

So:

₹100 payment → 105 NXL credited

The bonus calculation is implemented in the wallet top-up flow.

NXL Features

NXL wallet

NXL balance

NXL top-up

5% top-up bonus

NXL debit

NXL credit

NXL transaction history

NXL usage for eligible payments

Cashback

Referral rewards/commissions

Membership-related transactions

Merchant-related transactions

Booking-related transactions

Payment-related transactions

Refund transaction support

Admin NXL transaction management

8. 💰 Cashback & Rewards

The application contains both wallet/NXL transactions and a separate reward-points system.

Examples of NXL sources supported by the application

EVA Beauty

Online Shopping

Membership

Referral

Merchant

Booking

Payment

Merchant Order

Refund

NXL Top-Up Bonus

Product orders can generate configured cashback for eligible purchases.

9. 💳 Payment Gateway

The application integrates Razorpay for online payments.

Payment-related workflows include:

Product checkout

Salon booking

Membership purchase

Wallet/NXL top-up

Agent/Merchant application payment workflows

Payment verification

Payment records

Razorpay order creation

Razorpay signature verification

Razorpay webhook handling

Payment status tracking

10. 🔐 Authentication & Security

User Authentication

Users can:

Sign up using their email

Receive OTP by email

Verify OTP

Complete registration/login

Receive JWT authentication

Access protected features

User Login Flow

Sign Up / Login
      ↓
Enter Email
      ↓
OTP Sent to Email
      ↓
Enter OTP
      ↓
OTP Verification
      ↓
JWT Authentication
      ↓
User Dashboard

Admin Authentication

Admin access is handled separately.

The current implementation uses:

Admin Email + Password
        ↓
Authentication
        ↓
JWT Cookie
        ↓
Admin Dashboard

11. 👨‍💼 Admin Dashboard

EVA is centrally managed through an Admin Dashboard.

Admin functionality covers the major platform modules.

Admin Management

Users

View users

Manage user accounts

User status/roles

Products

Add products

Edit products

Manage products

Product pricing

Product discounts

Orders

View orders

Update order status

Track order history

Manage order information

Salon

Manage salon information

Manage salon services

Manage appointments

View salon payments

Franchise

View franchise leads

Update application status

Add remarks

Manage franchise amount

Referral commission processing

Membership

Manage membership plans

Create/edit membership plans

View membership users

Manage membership purchases

Store

Manage Agent applications

Manage Merchant applications

Approve/reject applications

View application details

Manage store opportunities

Matrimony

Pending profiles

Approved profiles

Rejected profiles

Blocked profiles

Profile approval/rejection/blocking

Wallet / NXL

View wallet transactions

Manage NXL transactions

Review payment-related transactions

Notifications

Admin notifications

User notifications

Read/unread management

Audit

Audit logging for administrative activities

🏗️ System Architecture

The application follows a layered Spring Boot architecture.

                   ┌─────────────────────┐
                   │      Frontend       │
                   │ Thymeleaf / HTML /  │
                   │ CSS / JavaScript    │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │     Controllers     │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │      Services       │
                   │ Business Logic      │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │    Repositories     │
                   │ Spring Data JPA     │
                   └──────────┬──────────┘
                              │
                              ▼
                   ┌─────────────────────┐
                   │     MySQL DB        │
                   └─────────────────────┘

🧱 Backend Structure

src/main/java/com/llbeauty/
│
├── config
├── constants
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
└── service

Main Backend Components

Controllers

Authentication

Products

Checkout

Salon booking

Salon payments

Franchise

Membership

Wallet/NXL

Store

Agent

Merchant

Matrimony

Admin

Notifications

PDF receipts

Razorpay webhook

Services

Authentication

OTP

Email

Payments

Razorpay

Wallet

Membership

Products

Orders

Salon

Matrimony

Agent

Merchant

Store

Notifications

Rewards

PDF generation

Admin operations

🗄️ Database

The project uses:

MySQL + Spring Data JPA + Hibernate

The application contains entities for major business areas such as:

User

Admin

Product

Order

Order Item

Payment

Appointment

Salon

Salon Service

Membership

Membership Purchase

Membership History

User Membership

Membership QR Code

Wallet

Wallet Transaction

NXL Wallet

NXL Wallet Transaction

System Wallet

Commission

Agent Profile

Merchant

Merchant Profile

Merchant Order

Store Application

Franchise Lead

Matrimony Profile

Matrimony Preference

Matrimony Interest

Matrimony Match

Matrimony Chat Message

Notification

Reward Point

Reward Transaction

Audit Log

Invoice

QR Code

🛠️ Technology Stack

Category

Technology

Programming Language

Java 21

Backend Framework

Spring Boot 4.0.6

Security

Spring Security

Authentication

JWT

ORM

Hibernate

Data Access

Spring Data JPA

Frontend

Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap

Database

MySQL

Payment Gateway

Razorpay

Email

Spring Boot Mail

PDF

OpenPDF

API Documentation

SpringDoc OpenAPI / Swagger

Build Tool

Maven

Version Control

Git / GitHub

📁 Project Structure

EVA/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/llbeauty/
│   │   │       ├── config/
│   │   │       ├── constants/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── enums/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       └── templates/
│   │
│   └── test/
│
├── pom.xml
└── README.md

⚙️ Prerequisites

Install the following before running the project:

Java 21

Maven

MySQL

Git

IntelliJ IDEA / Eclipse / Spring Tool Suite

🚀 Installation & Setup

1. Clone the repository

git clone https://github.com/Kalyani-Bhawar/llbeauty-project.git
cd llbeauty-project

2. Create MySQL Database

CREATE DATABASE llbeauty_db;

3. Configure Application Properties

Create your local configuration using your own credentials.

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/llbeauty_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

jwt.secret=YOUR_JWT_SECRET

razorpay.key.id=YOUR_RAZORPAY_KEY_ID
razorpay.key.secret=YOUR_RAZORPAY_SECRET

spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_EMAIL_APP_PASSWORD

4. Build the project

mvn clean install

5. Run the application

mvn spring-boot:run

Application URL:

http://localhost:8080

📖 API Documentation

The project includes SpringDoc OpenAPI / Swagger support.

After starting the application, the configured Swagger/OpenAPI endpoint can be used to inspect the available APIs.

🔄 Complete Platform Flow

                         EVA BEAUTY
                              │
             ┌────────────────┼─────────────────┐
             │                │                 │
             ▼                ▼                 ▼
          USER             ADMIN             BUSINESS
             │                │                 │
             ▼                ▼                 ▼
       Email + OTP       Admin Login       Agent / Merchant
             │                │                 │
             ▼                ▼                 ▼
        JWT Login        Admin Dashboard     Store Module
             │                │
   ┌─────────┼─────────┐      │
   │         │         │      │
   ▼         ▼         ▼      ▼
Products   Salon   Matrimony  Manage Everything
   │         │         │
   ▼         ▼         ▼
Orders    Booking   Matches
   │         │         │
   ▼         ▼         ▼
Payment   ₹100      Request
   │      Advance      │
   │         │         ▼
   │         │        Chat
   │         │
   └────┬────┘
        ▼
   NXL / Wallet
        │
        ├── Top-Up + 5% Bonus
        ├── Cashback
        ├── Referral
        ├── Membership
        ├── Booking
        └── Eligible Payments

🔒 Security Notes

Sensitive credentials must never be committed to GitHub.

Do not upload real values for:

Database passwords

JWT secrets

Razorpay secret keys

Email passwords/app passwords

API keys

Other private credentials

Use environment variables or a local application.properties file.

A safe repository should contain an example configuration such as:

application-example.properties

with placeholder values only.

👩‍💻 Developer

Kalyani Bhawar

GitHub:
https://github.com/Kalyani-Bhawar

Project Repository:
https://github.com/Kalyani-Bhawar/llbeauty-project

📌 Project Purpose

EVA Beauty demonstrates the development of a multi-module full-stack platform using Java and Spring Boot, including authentication, authorization, payments, e-commerce, appointment booking, membership, referral/commission systems, business onboarding, matrimony workflows, wallet/NXL transactions, and centralized administration.
