package com.alzz26.payments_ledger_service.ledger;

public record AccountResponse(Long id, String owner, String currency, AccountType type, long balance) {
}