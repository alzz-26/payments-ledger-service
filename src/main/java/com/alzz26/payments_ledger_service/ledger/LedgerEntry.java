package com.alzz26.payments_ledger_service.ledger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private LedgerTransaction transactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account accountId;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LedgerEntry() {
        //For JPA only
    }

    public LedgerEntry(LedgerTransaction transaction_id, Account account_id, Long amount) {
        this.transactionId = transaction_id;
        this.accountId = account_id;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() { return this.id; }
    public LedgerTransaction getTransactionId() { return this.transactionId; }
    public Account getAccountId() { return this.accountId; }
    public Long getAmount() { return this.amount; }
    public Instant getCreatedAt() { return this.createdAt; }
}