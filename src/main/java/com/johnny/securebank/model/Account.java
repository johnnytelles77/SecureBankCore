package com.johnny.securebank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.AccountType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private Double balance;

    @ManyToOne
    @JsonIgnore
    private User user;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToMany(mappedBy = "fromAccount")
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public AccountType getType() {
        return type;
    }
    public void setType(AccountType type) {
        this.type = type;
    }
    public AccountStatus getStatus() {
        return status;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void deposit(Double amount) {
        validateActiveAccount();
        validateAmount(amount);
        this.balance += amount;
    }

    public void withdraw(Double amount) {
        validateActiveAccount();
        validateAmount(amount);

        if(amount > balance) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        this.balance -= amount;
    }

    public void transferTo(Account targetAccount, Double amount) {
        validateTargetAccount(targetAccount);
        withdraw(amount);
        targetAccount.deposit(amount);
    }

    public Account() {
    }

    private void validateAmount(Double amount) {
        if(amount == null) {
            throw new IllegalArgumentException("Amount cannot be null.");
        }

        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    private void validateActiveAccount() {
        if(status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account status is not ACTIVE");
        }
    }

    private void validateTargetAccount(Account targetAccount) {
        if(targetAccount == null) {
            throw new IllegalArgumentException("Target account cannot be null.");
        }
        if(this == targetAccount) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
    }

    public Account(
          Long id,
          String accountNumber,
          Double balance,
          User user,
          LocalDateTime createdAt,
          AccountType type,
          AccountStatus status){
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.type = type;
        this.status = status;
    }
}