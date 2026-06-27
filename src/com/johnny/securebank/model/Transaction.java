package com.johnny.securebank.model;

import com.johnny.securebank.model.enums.TransactionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    private Account fromAccount;

    @ManyToOne
    private Account toAccount;

    private LocalDateTime createdAt;

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
    public Account getFromAccount() {
        return fromAccount;
    }
    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }
    public Account getToAccount() {
        return toAccount;
    }
    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Transaction() {

    }
    public Transaction(
            Long id,
            Double amount,
            String description,
            TransactionType type,
            Account fromAccount,
            Account toAccount) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }
}
