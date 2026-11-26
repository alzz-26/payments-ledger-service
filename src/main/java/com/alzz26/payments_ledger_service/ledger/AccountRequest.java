package com.alzz26.payments_ledger_service.ledger;

// Request body for POST /api/accounts, e.g. {"owner":"alice","currency":"INR"}
public record AccountRequest(String owner, String currency) {
}
