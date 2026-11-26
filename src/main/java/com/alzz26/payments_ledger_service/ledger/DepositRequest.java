package com.alzz26.payments_ledger_service.ledger;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Request body for POST /api/accounts/{id}/deposits, e.g. {"amount":50000} (= 500.00 INR, in paise)
public record DepositRequest(@NotNull @Positive Long amount) {
}
