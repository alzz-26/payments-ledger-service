package com.alzz26.payments_ledger_service.ledger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final LedgerService ledgerService;                       // the one dependency

    public AccountController(LedgerService ledgerService) {          // constructor injection, same as the service
        this.ledgerService = ledgerService;
    }

    // POST /api/accounts   body: {"owner":"alice","currency":"INR"}
    @PostMapping()
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {   // JSON body → record
        AccountResponse created = ledgerService.createAccount(request.owner(), request.currency());   // record accessors
        return ResponseEntity.status(HttpStatus.CREATED).body(created);                       // 201
    }

    // POST /api/accounts/{id}/deposits   body: {"amount":50000}
    @PostMapping("/{id}/deposits")
    public ResponseEntity<DepositResponse> deposit(@PathVariable Long id, @RequestBody DepositRequest request) {   // URL part, then body
        DepositResponse result = ledgerService.deposit(id, request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // GET /api/accounts/{id}
    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id) {   // plain return type = 200 OK
        return ledgerService.getAccount(id);
    }
}