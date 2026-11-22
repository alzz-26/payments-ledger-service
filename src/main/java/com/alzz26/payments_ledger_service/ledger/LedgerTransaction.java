package com.alzz26.payments_ledger_service.ledger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ledger_transactions")
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LedgerTransaction() {
        //For JPA only
    }

    public LedgerTransaction(TransactionType type) {
        this.type = type;
        this.createdAt = Instant.now();
    }

    public Long getId() { return this.id; }
    public TransactionType getType() { return this.type; }
    public Instant getCreatedAt() { return this.createdAt; }
}