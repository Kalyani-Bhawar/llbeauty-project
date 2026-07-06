package com.llbeauty.constants;

import java.util.List;

public class NxlConstants {

    // 🔥 Transaction Types
    public static final String CREDIT = "CREDIT";
    public static final String DEBIT  = "DEBIT";

    // 🔥 Reward / Cashback Sources (MAIN BUSINESS LOGIC)
    public static final String SOURCE_LLBEAUTY = "EVA_BEAUTY";
    public static final String SOURCE_BAG_SHOP = "BAG_SHOP";
    public static final String SOURCE_BOOK_STORE = "BOOK_STORE";
    public static final String SOURCE_MEDICAL_STORE = "MEDICAL_STORE";
    public static final String SOURCE_GYM_MANAGEMENT = "GYM_MANAGEMENT";
    public static final String SOURCE_ONLINE_SHOPPING = "ONLINE_SHOPPING";

    // 💰 NEW SYSTEM SOURCES
    public static final String SOURCE_TOPUP_BONUS = "NXL_TOPUP_BONUS";
    public static final String SOURCE_MEMBERSHIP = "MEMBERSHIP";
    public static final String SOURCE_MEMBERSHIP_PURCHASE = "MEMBERSHIP_PURCHASE";
    public static final String SOURCE_REFERRAL = "REFERRAL";
    public static final String SOURCE_MERCHANT = "MERCHANT_APPROVAL";
    public static final String SOURCE_BOOKING = "BOOKING";
    public static final String SOURCE_PAYMENT = "PAYMENT";
    
    public static final String SOURCE_MERCHANT_ORDER = "MERCHANT_ORDER";

    // 🔥 FINAL ALLOWED SOURCES LIST
    public static final List<String> ALLOWED_SOURCES = List.of(
            SOURCE_LLBEAUTY,
            SOURCE_BAG_SHOP,
            SOURCE_BOOK_STORE,
            SOURCE_MEDICAL_STORE,
            SOURCE_GYM_MANAGEMENT,
            SOURCE_ONLINE_SHOPPING,

            // 💰 NXL CORE SYSTEM
            SOURCE_TOPUP_BONUS,
            SOURCE_MEMBERSHIP,
            SOURCE_MEMBERSHIP_PURCHASE,
            SOURCE_REFERRAL,
            SOURCE_MERCHANT,
            SOURCE_BOOKING,
            SOURCE_PAYMENT,
            SOURCE_MERCHANT_ORDER 
    );
}