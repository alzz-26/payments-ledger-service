package com.alzz26.payments_ledger_service.ledger;

public class SystemAccountMissingException extends RuntimeException{
    private final String currency;

    public SystemAccountMissingException(String currency) {
        super("System account missing with currency: " + currency);
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
