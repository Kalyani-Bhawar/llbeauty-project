package com.llbeauty.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MerchantApplicationRequest {
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
    private String mobile;

    @NotBlank(message = "Email address is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    private String gstNumber; // Optional

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Enter a valid PAN number")
    private String panNumber;

    @NotBlank(message = "Aadhar number is required")
    @Pattern(regexp = "^\\d{12}$", message = "Enter a valid 12-digit Aadhar number")
    private String aadharNumber;

    @NotBlank(message = "Business type is required")
    private String businessType;

    @NotBlank(message = "Bank Account Holder Name is required")
    private String bankAccountHolderName;

    @NotBlank(message = "Bank Account Number is required")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC Code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Enter a valid 11-digit IFSC code")
    private String ifscCode;

    @NotBlank(message = "PAN upload is required")
    private String panDocumentUrl;

    @NotBlank(message = "Aadhar upload is required")
    private String aadharDocumentUrl;


    private String gstDocumentUrl; // Optional

    private Boolean onlineSelling = false;
    private Boolean offlineSelling = false;

    public MerchantApplicationRequest() {}

    // Getters and Setters
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getBankAccountHolderName() { return bankAccountHolderName; }
    public void setBankAccountHolderName(String bankAccountHolderName) { this.bankAccountHolderName = bankAccountHolderName; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getPanDocumentUrl() { return panDocumentUrl; }
    public void setPanDocumentUrl(String panDocumentUrl) { this.panDocumentUrl = panDocumentUrl; }
    public String getAadharDocumentUrl() { return aadharDocumentUrl; }
    public void setAadharDocumentUrl(String aadharDocumentUrl) { this.aadharDocumentUrl = aadharDocumentUrl; }
    public String getGstDocumentUrl() { return gstDocumentUrl; }
    public void setGstDocumentUrl(String gstDocumentUrl) { this.gstDocumentUrl = gstDocumentUrl; }
    public Boolean getOnlineSelling() { return onlineSelling; }
    public void setOnlineSelling(Boolean onlineSelling) { this.onlineSelling = onlineSelling; }
    public Boolean getOfflineSelling() { return offlineSelling; }
    public void setOfflineSelling(Boolean offlineSelling) { this.offlineSelling = offlineSelling; }
}
