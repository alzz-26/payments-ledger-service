package com.alzz26.payments_ledger_service.ledger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {
    private final LedgerEntryRepository ledgerRepo;
    private final AccountRepository accountRepo;
    private final LedgerTransactionRepository transactionRepo;


    public LedgerService(
        LedgerEntryRepository ledgerRepo,
        AccountRepository accountRepo,
        LedgerTransactionRepository transactionRepo
    ) {
        this.ledgerRepo = ledgerRepo;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    public long getBalance(Long accountId) {
        return ledgerRepo.getBalanceOf(accountId);
    }

    public AccountResponse createAccount(String owner, String currency) {
        Account newAccount = new Account(owner, currency, AccountType.USER);
        Account account = accountRepo.save(newAccount);
        return new AccountResponse(account.getId(), account.getOwner(), account.getCurrency(), account.getType(), 0);
    }

    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepo.findById(accountId).orElseThrow();
        long balance = getBalance(accountId);
        return new AccountResponse(account.getId(), account.getOwner(), account.getCurrency(), account.getType(), balance);
    }

    @Transactional
    public DepositResponse deposit(Long accountId, long amount) {
        //Get the accounts
        Account userAccount = accountRepo.findById(accountId).orElseThrow();
        Account systemAccount = accountRepo.findByTypeAndCurrency(AccountType.SYSTEM, userAccount.getCurrency()).orElseThrow();
        
        //New transaction entry
        LedgerTransaction newTransaction = new LedgerTransaction(TransactionType.DEPOSIT);
        LedgerTransaction transaction = transactionRepo.save(newTransaction);

        //Post balanced entries: +amount to user, -amount to SYSTEM
        LedgerEntry userEntry = new LedgerEntry(transaction, userAccount, +amount);
        ledgerRepo.save(userEntry);
        LedgerEntry systemEntry = new LedgerEntry(transaction, systemAccount, -amount);
        ledgerRepo.save(systemEntry);

        return new DepositResponse(transaction.getId());
    }
}