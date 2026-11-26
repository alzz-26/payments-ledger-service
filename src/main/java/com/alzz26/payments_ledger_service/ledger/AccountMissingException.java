package com.alzz26.payments_ledger_service.ledger;

public class AccountMissingException extends RuntimeException{
    private final Long id;

    public AccountMissingException(Long id) {
        super("Account not found with ID: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
