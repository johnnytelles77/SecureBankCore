package com.johnny.securebank.controller;

import com.johnny.securebank.dto.AccountAmountRequestDTO;
import com.johnny.securebank.dto.TransferRequestDTO;
import com.johnny.securebank.model.Transaction;
import com.johnny.securebank.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public Transaction deposit(@RequestBody AccountAmountRequestDTO request) {
        return transactionService.deposit(
                request.getAccountId(),
                request.getAmount());
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestBody AccountAmountRequestDTO request) {
        return transactionService.withdraw(
                request.getAccountId(),
                request.getAmount()
        );
    }

    @PostMapping("/transfer")
    public Transaction transfer(@RequestBody TransferRequestDTO request) {
        return transactionService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );
    }

    @GetMapping("/account/{id}")
    public List<Transaction> getTransactionByAccountId(@PathVariable Long id) {
        return transactionService.getTransactionsByAccountId(id);
    }
}
