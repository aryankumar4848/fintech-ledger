package com.aryan.fintech_ledger.service;

import com.aryan.fintech_ledger.dto.CreateAccountRequest;
import com.aryan.fintech_ledger.dto.DepositRequest;
import com.aryan.fintech_ledger.dto.WithdrawRequest;
import com.aryan.fintech_ledger.entity.Account;
import com.aryan.fintech_ledger.entity.LedgerEntry;
import com.aryan.fintech_ledger.entity.Transaction;
import com.aryan.fintech_ledger.repository.AccountRepository;
import com.aryan.fintech_ledger.repository.LedgerEntryRepository;
import com.aryan.fintech_ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public AccountService(AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public Account createAccount(CreateAccountRequest request) {
        // Check duplicate email
        accountRepository.findByEmail(request.getEmail())
                .ifPresent(acc -> {
                    throw new RuntimeException("Email already exists");
                });

        // Create entity
        Account account = new Account();
        account.setName(request.getName());
        account.setEmail(request.getEmail());
        account.setBalance(request.getInitialBalance());
        account.setStatus(Account.Status.ACTIVE);

        return accountRepository.save(account);
    }

    @Transactional
    public Transaction deposit(Long accountId, DepositRequest request) {
        // Idempotency check
        Optional<Transaction> existingTx = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingTx.isPresent()) {
            return existingTx.get();
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Update balance
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = new Transaction(
                null, // from system
                accountId,
                request.getAmount(),
                Transaction.Type.DEPOSIT,
                Transaction.Status.SUCCESS,
                request.getIdempotencyKey());
        transaction = transactionRepository.save(transaction);

        // Record ledger entry
        LedgerEntry ledgerEntry = new LedgerEntry(
                accountId,
                transaction.getId(),
                LedgerEntry.EntryType.CREDIT,
                request.getAmount(),
                account.getBalance());
        ledgerEntryRepository.save(ledgerEntry);

        return transaction;
    }

    @Transactional
    public Transaction withdraw(Long accountId, WithdrawRequest request) {
        // Idempotency check
        Optional<Transaction> existingTx = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingTx.isPresent()) {
            return existingTx.get();
        }

        // Fetch with lock to prevent concurrent balance issues
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Validate balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Update balance
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = new Transaction(
                accountId,
                null, // to system (withdrawal)
                request.getAmount(),
                Transaction.Type.WITHDRAW,
                Transaction.Status.SUCCESS,
                request.getIdempotencyKey());
        transaction = transactionRepository.save(transaction);

        // Record ledger entry
        LedgerEntry ledgerEntry = new LedgerEntry(
                accountId,
                transaction.getId(),
                LedgerEntry.EntryType.DEBIT,
                request.getAmount(),
                account.getBalance());
        ledgerEntryRepository.save(ledgerEntry);

        return transaction;
    }
}
