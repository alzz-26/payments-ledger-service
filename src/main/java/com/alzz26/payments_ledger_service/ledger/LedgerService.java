package com.alzz26.payments_ledger_service.ledger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Account and ledger operations: create accounts, read accounts with balances, make deposits.
 * Called by AccountController; uses the three repositories.
 */
@Service
public class LedgerService {
    private final LedgerEntryRepository ledgerRepo;
    private final AccountRepository accountRepo;
    private final LedgerTransactionRepository transactionRepo;

    // Receives the repositories from Spring (constructor injection)
    public LedgerService(
        LedgerEntryRepository ledgerRepo,
        AccountRepository accountRepo,
        LedgerTransactionRepository transactionRepo
    ) {
        this.ledgerRepo = ledgerRepo;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    // Returns the account's balance: the sum of its ledger entries
    public long getBalance(Long accountId) {
        return ledgerRepo.getBalanceOf(accountId);
    }

    // Saves a new USER account and returns it with balance 0
    public AccountResponse createAccount(String owner, String currency) {
        Account newAccount = new Account(owner, currency, AccountType.USER);
        Account account = accountRepo.save(newAccount);
        return new AccountResponse(account.getId(), account.getOwner(), account.getCurrency(), account.getType(), 0);
    }

    // Loads an account by id and returns it with its current balance
    public AccountResponse getAccount(Long accountId) {
        // TODO(M1c): orElseThrow() currently gives a 500; return a 404 instead
        Account account = accountRepo.findById(accountId).orElseThrow();
        long balance = getBalance(accountId);
        return new AccountResponse(account.getId(), account.getOwner(), account.getCurrency(), account.getType(), balance);
    }

    // Deposits money into an account: saves one DEPOSIT transaction and two entries,
    // +amount to the user account and -amount to the SYSTEM account of the same currency.
    // All three rows are saved in one database transaction.
    @Transactional
    public DepositResponse deposit(Long accountId, long amount) {
        // TODO(M1c): reject amount <= 0
        // Load the user account and the SYSTEM account for its currency
        Account userAccount = accountRepo.findById(accountId).orElseThrow();
        Account systemAccount = accountRepo.findByTypeAndCurrency(AccountType.SYSTEM, userAccount.getCurrency()).orElseThrow();

        // Save the transaction header
        LedgerTransaction newTransaction = new LedgerTransaction(TransactionType.DEPOSIT);
        LedgerTransaction transaction = transactionRepo.save(newTransaction);

        // Save the two entries: +amount to the user, -amount to SYSTEM
        LedgerEntry userEntry = new LedgerEntry(transaction, userAccount, +amount);
        ledgerRepo.save(userEntry);
        LedgerEntry systemEntry = new LedgerEntry(transaction, systemAccount, -amount);
        ledgerRepo.save(systemEntry);

        return new DepositResponse(transaction.getId());
    }
}
