package com.alzz26.payments_ledger_service.ledger;

// Response body for a deposit: the id of the created ledger transaction
public record DepositResponse(Long transactionId) {
}
