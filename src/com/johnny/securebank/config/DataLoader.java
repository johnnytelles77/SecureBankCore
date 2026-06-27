package com.johnny.securebank.config;


import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountType;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.service.AccountService;
import com.johnny.securebank.service.TransactionService;
import com.johnny.securebank.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public DataLoader(UserService userService,  AccountService accountService, TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "johnnyt7@test.com",
                "12345",
                Role.ADMIN
        );
        User savedUser = userService.registerUser(user);
        System.out.println(savedUser.getId());
        System.out.println(savedUser.getEmail());

        Account account = new Account();
                account.setAccountNumber("CHK-001");
                account.setType(AccountType.CHECKING);
                account.setUser(savedUser);
        Account savedAccount = accountService.createAccount(account);
        System.out.println(savedAccount.getId());
        System.out.println(savedAccount.getAccountNumber());

        Account savings = new Account();
        savings.setAccountNumber("SAV-001");
        savings.setType(AccountType.SAVINGS);
        savings.setUser(savedUser);

        Account savingsAccount = accountService.createAccount(savings);

        transactionService.deposit(savedAccount.getId(), 1000.0);

        transactionService.transfer(
                savedAccount.getId(),
                savingsAccount.getId(),
                250.0);
        Account updatedChecking = accountService.getAccountById(savedAccount.getId());
        Account updatedSavings = accountService.getAccountById(savingsAccount.getId());

        System.out.println(updatedChecking.getBalance());
        System.out.println(updatedSavings.getBalance());
    }
}