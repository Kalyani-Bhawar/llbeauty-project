package com.llbeauty.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Formatter;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook.secret:dummysecret}")
    private String razorpayWebhookSecret;

    @Value("${razorpay.merchant.upi.id:placeholder@upi}")
    private String merchantUpiId;

    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }

    public String getMerchantUpiId() {
        return merchantUpiId;
    }

    public Order createOrder(Double amount, String receipt) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        // Razorpay expects amount in paise (1 INR = 100 paise)
        orderRequest.put("amount", (int) Math.round(amount * 100)); 
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);
        return client.orders.create(orderRequest);
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(razorpayKeySecret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes("UTF-8"));
            
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            String generatedSignature = formatter.toString();
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public com.razorpay.Refund refundPayment(String paymentId, Double amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject refundRequest = new JSONObject();
        if (amount != null) {
            refundRequest.put("amount", (int) Math.round(amount * 100)); // partial refund
        }
        return client.payments.refund(paymentId, refundRequest);
    }

    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(razorpayWebhookSecret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes("UTF-8"));
            
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            String generatedSignature = formatter.toString();
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
