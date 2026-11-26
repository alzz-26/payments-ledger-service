package com.alzz26.payments_ledger_service.ledger;

// Response body for the account endpoints; balance is in minor units (paise)
public record AccountResponse(Long id, String owner, String currency, AccountType type, long balance) {
}
