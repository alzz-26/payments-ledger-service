package com.alzz26.payments_ledger_service.web;

import com.alzz26.payments_ledger_service.ledger.AccountMissingException;        // different package, so import
import com.alzz26.payments_ledger_service.ledger.SystemAccountMissingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Converts exceptions thrown by any controller into RFC 9457 ProblemDetail responses
@RestControllerAdvice
public class GlobalExceptionHandler {

    // AccountMissingException -> 404 Not Found
    @ExceptionHandler(AccountMissingException.class)
    public ProblemDetail handleAccountMissing(AccountMissingException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // SystemAccountMissingException -> 422
    @ExceptionHandler(SystemAccountMissingException.class)
    public ProblemDetail handleSystemAccountMissing(SystemAccountMissingException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // DB constraint violation (duplicate owner + currency) -> 409 Conflict
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Data Violation: duplicate owner+currency");   // fixed message, not ex.getMessage()
    }
}