package com.aryan.fintech_ledger.controller;

import com.aryan.fintech_ledger.dto.CreateAccountRequest;
import com.aryan.fintech_ledger.dto.DepositRequest;
import com.aryan.fintech_ledger.dto.TransferRequest;
import com.aryan.fintech_ledger.dto.WithdrawRequest;
import com.aryan.fintech_ledger.entity.Account;
import com.aryan.fintech_ledger.entity.LedgerEntry;
import com.aryan.fintech_ledger.entity.Transaction;
import com.aryan.fintech_ledger.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @PostMapping("/{id}/deposit")
    public Transaction deposit(@PathVariable Long id, @Valid @RequestBody DepositRequest request) {
        return accountService.deposit(id, request);
    }

    @PostMapping("/{id}/withdraw")
    public Transaction withdraw(@PathVariable Long id, @Valid @RequestBody WithdrawRequest request) {
        return accountService.withdraw(id, request);
    }

    @PostMapping("/{id}/transfer")
    public Transaction transfer(@PathVariable Long id, @Valid @RequestBody TransferRequest request) {
        return accountService.transfer(id, request);
    }

    @GetMapping("/{id}/ledger")
    public Page<LedgerEntry> getLedger(@PathVariable Long id, Pageable pageable) {
        return accountService.getLedger(id, pageable);
    }
}
