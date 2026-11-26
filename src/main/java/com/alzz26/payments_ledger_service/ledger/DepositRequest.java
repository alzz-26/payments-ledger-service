package com.alzz26.payments_ledger_service.ledger;

// Request body for POST /api/accounts/{id}/deposits, e.g. {"amount":50000} (= 500.00 INR, in paise)
public record DepositRequest(Long amount) {
}
