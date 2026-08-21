package com.johnny.securebank.dto;

import com.johnny.securebank.model.enums.TransactionType;

import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long id;
    private Double amount;
    private String description;
    private TransactionType type;
    private Long fromAccountId;
    private Long toAccountId;
    private LocalDateTime createdAt;

    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(Long id, Double amount, String description, TransactionType type, Long fromAccountId, Long toAccountId, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }
    public Long getFromAccountId() {
        return fromAccountId;
    }
    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }
    public Long getToAccountId() {
        return toAccountId;
    }
    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt( LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
