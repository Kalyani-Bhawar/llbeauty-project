package com.llbeauty.dto;

public class MatrimonyInterestDTO {

    private Long senderId;
    private Long receiverId;
    private Long interestId;
    private String status;

    public MatrimonyInterestDTO() {
    }

    public MatrimonyInterestDTO(Long senderId, Long receiverId, Long interestId, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.interestId = interestId;
        this.status = status;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getInterestId() {
        return interestId;
    }

    public void setInterestId(Long interestId) {
        this.interestId = interestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
