package com.llbeauty.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OtpVerifyRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;

    // Optional: used to redirect after login
    private String redirect;

    public OtpVerifyRequest() {
    }

    public OtpVerifyRequest(String email, String otp, String redirect) {
        this.email = email;
        this.otp = otp;
        this.redirect = redirect;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return this.otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRedirect() {
        return this.redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    @Override
    public String toString() {
        return "OtpVerifyRequest(email=" + this.getEmail() + ", otp=" + this.getOtp() + ", redirect=" + this.getRedirect() + ")";
    }
}
