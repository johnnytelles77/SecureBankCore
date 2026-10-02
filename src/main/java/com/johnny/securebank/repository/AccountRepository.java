package com.johnny.securebank.repository;

import com.johnny.securebank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);
    List<Account> findByUserId(Long userId);
}
