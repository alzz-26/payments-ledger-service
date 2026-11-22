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
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Account() {
        // for JPA only
    }

    public Account(String owner, String currency, AccountType type) {
        this.owner = owner;
        this.currency = currency;
        this.type = type;
        this.createdAt = Instant.now();
    }

    public Long getId() { return this.id; }
    public String getOwner() { return this.owner; }
    public String getCurrency() { return this.currency; }
    public AccountType getType() { return this.type; }
    public Instant getCreatedAt() { return this.createdAt; }
}