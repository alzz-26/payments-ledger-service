package com.alzz26.payments_ledger_service.ledger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints under /api/accounts: create an account, deposit money, get an account with its balance.
 * Each method passes the request to LedgerService and returns the result.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final LedgerService ledgerService;

    // Receives LedgerService from Spring (constructor injection)
    public AccountController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    // POST /api/accounts  body: {"owner":"alice","currency":"INR"}  -> 201 Created + the new account
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        AccountResponse created = ledgerService.createAccount(request.owner(), request.currency());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // POST /api/accounts/{id}/deposits  body: {"amount":50000}  -> 201 Created + the transaction id
    @PostMapping("/{id}/deposits")
    public ResponseEntity<DepositResponse> deposit(@PathVariable Long id, @RequestBody DepositRequest request) {
        DepositResponse result = ledgerService.deposit(id, request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // GET /api/accounts/{id}  -> 200 OK + the account with its balance
    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id) {
        return ledgerService.getAccount(id);
    }
}
