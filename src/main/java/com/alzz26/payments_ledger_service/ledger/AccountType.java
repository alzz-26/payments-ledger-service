package com.alzz26.payments_ledger_service.ledger;

/**
 * Kinds of account.
 * USER   - a customer's account.
 * SYSTEM - the counterparty account for one currency (money coming in from outside).
 */
public enum AccountType {
    USER,
    SYSTEM
}
