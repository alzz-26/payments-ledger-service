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
    private LedgerTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private long amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LedgerEntry() {
        //For JPA only
    }

    public LedgerEntry(LedgerTransaction transaction, Account account, long amount) {
        this.transaction = transaction;
        this.account = account;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() { return this.id; }
    public LedgerTransaction getTransaction() { return this.transaction; }
    public Account getAccount() { return this.account; }
    public long getAmount() { return this.amount; }
    public Instant getCreatedAt() { return this.createdAt; }
}