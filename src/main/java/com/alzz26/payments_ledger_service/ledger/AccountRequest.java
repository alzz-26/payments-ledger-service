package com.alzz26.payments_ledger_service.ledger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Request body for POST /api/accounts, e.g. {"owner":"alice","currency":"INR"}
public record AccountRequest(@NotBlank String owner, @NotBlank @Pattern(regexp="^[A-Z]{3}$") String currency) {
}
