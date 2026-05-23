package com.llbeauty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpVerifyRequest {

    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter valid mobile number")
    private String mobile;

    @NotBlank(message = "OTP is required")
    private String otp;

    // Optional: used to redirect after login
    private String redirect;
}
